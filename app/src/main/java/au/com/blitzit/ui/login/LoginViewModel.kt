package au.com.blitzit.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import java.util.*

class LoginViewModel : ViewModel() {

    private val correctUsername = "ronnie.ruse"
    private val correctPassword = "password"

    //Todo: persist these to allow easy reuse when moving from register to login and vice versa
    var username: String = ""
    var password: String = ""


    fun validateCredentials(): Boolean
    {
        if(username.toLowerCase(Locale.ENGLISH).equals(correctUsername.toLowerCase(Locale.ENGLISH)))
        {
            if (password.equals(correctPassword))
            {
                return true
            }
        }

        return false
    }

    fun attemptLogin(): Boolean
    {
        var signedIn: Boolean = false
        Amplify.Auth.signIn("username", "password",
            { result ->
                if (result.isSignInComplete)
                {
                    Log.i("Auth", "Sign in succeeded")
                    signedIn = true
                }
                else
                {
                    Log.i("AuthQ", "Sign in not complete")
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
            }
        )
        return signedIn
    }

}