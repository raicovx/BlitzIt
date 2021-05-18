package au.com.blitzit.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.roomdata.SignUpRequest
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.kotlin.core.Amplify
import com.google.gson.Gson
import com.google.gson.GsonBuilder

enum class RegistrationState(val state: String)
{
    NotRegistered("Awaiting registration"),
    Registering("Awaiting Registration Results"),
    Registered("Registration received, awaiting email"),
    SignedUp("Registered and Signed up"),
    AlreadyRegistered("An account using this email has been found. Please contact Blitzit"),
    Failed01("Registration failed"),
    SignUpFailed01("Sign up failed")
}

data class RegistrationDetails(
        val date_of_birth: String,
        val email: String,
        val last_name: String,
        val ndis_number: String,
        val type: String
)
data class RegistrationResponse(
        val id: String?,
        val already_registered: String?
)

object AuthRegistration
{
    lateinit var appDatabase: AppDatabase

    val liveRegistrationState = MutableLiveData<RegistrationState>()
    private lateinit var registrationResponse: RegistrationResponse

    val signUpRequest = MutableLiveData<SignUpRequest>()

    init {
        liveRegistrationState.postValue(RegistrationState.NotRegistered)
    }

    suspend fun getSignUpRequest() = signUpRequest.postValue(appDatabase.signUpRequestDAO().getSignUpRequest())

    suspend fun attemptRegistration(context: Context, email: String, password: String, dob: String, last_name: String, type: String, ndis_number: String)
    {
        appDatabase = AppDatabase.getDatabase(context)

        liveRegistrationState.postValue(RegistrationState.Registering)

        val registrationDetails = RegistrationDetails(dob, email, last_name, ndis_number, type)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json: String = gson.toJson(registrationDetails)
        Log.i("GAZ_POST_INFO", json)

        val request = RestOptions.builder()
                .addPath("/registration")
                .addBody(json.toByteArray())
                .build()
        try
        {
            val response = Amplify.API.post(request, "mobileAPI")

            Log.i("GAZ_INFO", "POST succeeded, return Data: $response")

            registrationResponse = Gson().fromJson(response.data.asString(), RegistrationResponse::class.java)
            if(registrationResponse.id != null)
            {
                Log.i("GAZ_INFO", "Registration POST Accepted: ${registrationResponse.id}")
                Log.i("GAZ_INFO", "Registration POST response data: ${response.data.asString()}")

                appDatabase.signUpRequestDAO().nukeTable()

                appDatabase.signUpRequestDAO().upsertSignUpRequest(SignUpRequest(
                    registrationResponse.id!!,
                    email,
                    password,
                    1
                ))

                getSignUpRequest()

                liveRegistrationState.postValue(RegistrationState.Registered)
            }
            else {
                Log.i("GAZ_INFO", "Already Registered: ${registrationResponse.already_registered}")
                liveRegistrationState.postValue(RegistrationState.AlreadyRegistered)
            }
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "Register POST failed.", error)
            liveRegistrationState.postValue(RegistrationState.Failed01)
        }
    }

    suspend fun attemptConfirmation(confirmationCode: String)
    {
        val signUpDetails: SignUpRequest = signUpRequest.value!!

        val attrs = mapOf(
            AuthUserAttributeKey.email() to signUpDetails.email,
            AuthUserAttributeKey.custom("custom:requestId") to signUpDetails.request_id,
            AuthUserAttributeKey.custom("custom:code") to confirmationCode)

        val options = AuthSignUpOptions.builder()
            .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
            .build()

        try
        {
            val result = Amplify.Auth.signUp(signUpDetails.email, signUpDetails.password, options)
            Log.i("GAZ_INFO", "Sign up succeeded: $result")
            if(result.isSignUpComplete)
            {
                liveRegistrationState.postValue(RegistrationState.SignedUp)
                appDatabase.signUpRequestDAO().nukeTable()
            }
            else
            {
                Log.i("GAZ_INFO", "Sign up failed: $result")
                liveRegistrationState.postValue(RegistrationState.SignUpFailed01)
            }
        }
        catch (error: AuthException)
        {
            Log.e("AuthQuickStart", "Sign up failed", error)
            liveRegistrationState.postValue(RegistrationState.SignUpFailed01)
        }
    }

    fun resetRegistrationState()
    {
        liveRegistrationState.postValue(RegistrationState.NotRegistered)
    }

    suspend fun attemptResetPassword(email: String): AuthResetPasswordResult?
    {
        return try {
            val result = Amplify.Auth.resetPassword(email)
            Log.i("AuthQuickstart", "Password reset OK: $result")
            result
        } catch (error: AuthException) {
            Log.e("AuthQuickstart", "Password reset failed", error)
            null
        }
    }

    suspend fun confirmPasswordReset(password: String, confirmationCode: String): Boolean
    {
        return try {
            Amplify.Auth.confirmResetPassword(password, confirmationCode)
            Log.i("GAZ_RESET_PASSWORD", "New Password Confirmed")
            true
        } catch (error: AuthException){
            Log.e("GAZ_RESET_PASSWORD", "Failed to confirm password reset", error)
            false
        }
    }
}