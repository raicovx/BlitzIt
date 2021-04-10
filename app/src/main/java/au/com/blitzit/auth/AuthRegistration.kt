package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.helper.CranstekHelper
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.google.gson.Gson
import com.google.gson.GsonBuilder

enum class RegistrationState(val state: String)
{
    NotRegistered("Awaiting registration"),
    Registering("Awaiting Registration Results"),
    Registered("Registration received, awaiting email"),
    SigningUp("Unsure"),
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
    val liveRegistrationState = MutableLiveData<RegistrationState>()
    private lateinit var registrationDetails: RegistrationDetails
    private lateinit var registrationResponse: RegistrationResponse
    private lateinit var responseID: String

    init {
        liveRegistrationState.postValue(RegistrationState.NotRegistered)
    }

    fun attemptRegistration(lName: String, email: String, dob: String, ndis: String, type: String)
    {
        liveRegistrationState.postValue(RegistrationState.Registering)

        registrationDetails = RegistrationDetails(CranstekHelper.formatDate(dob), email, lName, ndis, type)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json: String = gson.toJson(registrationDetails)
        Log.i("GAZ_POST_INFO", json)

        val request = RestOptions.builder()
                .addPath("/registration")
                .addBody(json.toByteArray())
                .build()
        Amplify.API.post("mobileAPI", request,
                    {
                        Log.i("GAZ_INFO", "POST succeeded, return Data: ${it.data.asString()}")

                        registrationResponse = Gson().fromJson(it.data.asString(), RegistrationResponse::class.java)
                        if(registrationResponse.id != null) {
                            Log.i("GAZ_INFO", "Registration POST Accepted: ${registrationResponse.id}")
                            liveRegistrationState.postValue(RegistrationState.Registered)
                        }
                        else {
                            Log.i("GAZ_INFO", "Already Registered: ${registrationResponse.already_registered}")
                            liveRegistrationState.postValue(RegistrationState.AlreadyRegistered)
                        }
                    },
                    {
                        Log.e("GAZ_ERROR", "Register POST failed.", it)
                        liveRegistrationState.postValue(RegistrationState.Failed01)
                    })
    }

    fun attemptConfirmation(password: String, confirmationCode: String)
    {
        if(registrationResponse.id != null)
        {
            liveRegistrationState.postValue(RegistrationState.SigningUp)

            Log.i("GAZ_INFO", "confirmation code: $confirmationCode")
            Log.i("GAZ_INFO", "response code: ${registrationResponse.id}")
            Log.i("GAZ_INFO", "email: ${registrationDetails.email}")
            Log.i("GAZ_INFO", "password: $password")

            val attrs = mapOf(
                    AuthUserAttributeKey.email() to registrationDetails.email,
                    AuthUserAttributeKey.custom("custom:requestId") to registrationResponse.id!!,
                    AuthUserAttributeKey.custom("custom:code") to confirmationCode
            )

            val options = AuthSignUpOptions.builder()
                    .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
                    .build()
            Log.i("GAZ_INFO", "attr: ${options.userAttributes}")

            Amplify.Auth.signUp(registrationDetails.email.toLowerCase(), password, options,
                    {
                        Log.i("GAZ_INFO", "Sign up succeeded: $it")
                        liveRegistrationState.postValue(RegistrationState.SignedUp)
                    },
                    {
                        Log.i("GAZ_INFO", "Sign up failed: $it")
                        liveRegistrationState.postValue(RegistrationState.SignUpFailed01)
                    })
        }
    }

    fun resetRegistrationState()
    {
        liveRegistrationState.postValue(RegistrationState.NotRegistered)
    }
}