package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.data.UserData
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify

enum class SignInState(val state: String)
{
    signedOut("Signed Out"),
    signingIn("Attempting sign in"),
    signedIn("Signed In"),
    signInFailed("Incorrect Credentials")
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
        liveSignInState.postValue(SignInState.signingIn)
        Amplify.Auth.signIn(username, password,
                { result ->
                    if (result.isSignInComplete)
                    {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        //TODO("Collect userdata here, then post on success of that")
                        liveSignInState.postValue(SignInState.signedIn)
                    }
                    else
                    {
                        Log.i("AuthQuickstart", "Sign in not complete")
                        liveSignInState.postValue(SignInState.signInFailed)
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
                    liveSignInState.postValue(SignInState.signedIn)
                },
                {
                    Log.e("AmplifyQuickstart", "Failed to fetch auth session")
                    liveSignInState.postValue(SignInState.signedOut)
                }
        )
    }

    fun getUserData()
    {
        val request = RestOptions.builder()
                .addPath("https://hftkopqbcj.execute-api.ap-southeast-2.amazonaws.com/dev/participant/")
                .build()

        Amplify.API.get(request,
                { Log.i("MyAmplifyApp", "GET succeeded: $it") },
                { Log.e("MyAmplifyApp", "GET failed.", it) }
        )
    }
}