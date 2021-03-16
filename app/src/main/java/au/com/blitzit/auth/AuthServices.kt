package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.data.UserData
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

enum class SignInState(val state: String)
{
    SignedOut("Signed Out"),
    SigningIn("Attempting sign in"),
    SignedIn("Signed In"),
    SignInFailed("Incorrect Credentials")
}

object AuthServices
{
    val liveSignInState = MutableLiveData<SignInState>()

    lateinit var userData: UserData
        private set //Makes setting userdata private

    init {
        checkAuthSession()
    }

    fun attemptSignIn(username: String, password: String)
    {
        liveSignInState.postValue(SignInState.SigningIn)
        Amplify.Auth.signIn(username, password,
                { result ->
                    if (result.isSignInComplete)
                    {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        getUserData()
                        liveSignInState.postValue(SignInState.SignedIn)
                    }
                    else
                    {
                        Log.i("AuthQuickstart", "Sign in not complete")
                        liveSignInState.postValue(SignInState.SignInFailed)
                    }
                },
                { Log.e("AuthQuickstart", "Failed to sign in", it) }
        )
    }

    private fun checkAuthSession()
    {
        Amplify.Auth.fetchAuthSession(
                {
                    Log.i("AmplifyQuickstart", "Auth session = $it")
                    getUserData()
                },
                {
                    Log.e("AmplifyQuickstart", "Failed to fetch auth session")
                    liveSignInState.postValue(SignInState.SignedOut)
                }
        )
    }

    private fun getUserData()
    {
        val request = RestOptions.builder()
                .addPath("/participant")
                .build()

        Amplify.API.get("mobileAPI", request,
                {
                    Log.i("GAZ_INFO", "GET succeeded: ${it.data.asString()}")
                    val user: Array<UserData> = Gson().fromJson(it.data.asString(), Array<UserData>::class.java)
                    userData = user[0]
                    liveSignInState.postValue(SignInState.SignedIn)
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )
    }
}