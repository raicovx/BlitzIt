package au.com.blitzit.ui.login

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.User
import com.amazonaws.mobile.auth.core.signin.AuthException
import com.amazonaws.services.cognitoidentity.model.TooManyRequestsException
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.launch
import java.util.*

class LoginViewModel(application: Application) : AndroidViewModel(application)
{
}