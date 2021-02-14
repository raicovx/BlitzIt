package au.com.blitzit.ui.login

import androidx.lifecycle.ViewModel
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

}