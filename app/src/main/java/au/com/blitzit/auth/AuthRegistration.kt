package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.api.rest.RestOperation
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.google.gson.Gson

enum class RegistrationState(val state: String)
{
    NotRegistered("Awaiting registration"),
    Registering("Awaiting Registration Results"),
    Registered("Registration received, awaiting email"),
    SigningUp("Unsure"),
    SignedUp("Registered and Signed up"),
    AlreadyRegistered("An account using this email has been found. Please contact Blitzit"),
    Failed01("Registration failed")
}

data class RegistrationDetails(
        val date_of_birth: String,
        val email: String,
        val last_name: String,
        val ndis_number: String,
        val type: String
)

object AuthRegistration
{
    val liveRegistrationState = MutableLiveData<RegistrationState>()
    lateinit var requestResponse: String

    init {
        liveRegistrationState.postValue(RegistrationState.NotRegistered)
    }

    fun attemptRegistration(lName: String, email: String, dob: String, ndis: String, type: String)
    {
        liveRegistrationState.postValue(RegistrationState.Registering)

        val details = RegistrationDetails(dob, email, lName, ndis, type)

        val gson = Gson()
        val json: String = gson.toJson(details)
        Log.i("GAZ_POST_INFO", json)

        val request = RestOptions.builder()
                .addPath("/registration")
                .addBody(json.toByteArray())
                .build()
        Amplify.API.post("mobileAPI", request,
                    {
                        Log.i("GAZ_INFO", "GET succeeded for Plan Data: ${it.data.asString()}")
                        liveRegistrationState.postValue(RegistrationState.Registered)
                    },
                    {
                        Log.e("GAZ_ERROR", "GET failed.", it)
                        liveRegistrationState.postValue(RegistrationState.Failed01)
                    })
    }
}