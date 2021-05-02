package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.data.*
import com.amazonaws.mobile.auth.core.signin.AuthException
import com.amazonaws.services.cognitoidentity.model.TooManyRequestsException
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.kotlin.core.Amplify
import com.google.gson.Gson
import kotlinx.coroutines.*

enum class SignInState(val state: String)
{
    SignedOut("Signed Out"),
    SigningIn("Attempting sign in"),
    SignedIn("Signed In"),
    SignInFailed("Unhandled exception"),
    SignInFailedUserNotFound("User not found"),
    SignInFailedTooManyRequests("Too many attempts have been made to login, please wait 15 minutes and try again"),
    SignInFailedUserNotConfirmed("User hasn't confirmed via email. Please confirm registration and try again")
}

object AuthServices
{
    val liveSignInState = MutableLiveData<SignInState>()

    lateinit var userData: UserData
        private set //Makes setting userdata private

    private suspend fun getData()
    {
        getUserData()
        userData.setSelectedPlan(userData.getMostRecentPlan())
        liveSignInState.postValue(SignInState.SignedIn)
    }


    suspend fun attemptSignOut()
    {
        try {
            Amplify.Auth.signOut()
            Log.i("AuthQuickstart", "Signed out successfully")
            resetSignUpState()
        } catch (error: AuthException) {
            Log.e("AuthQuickstart", "Sign out failed", error)
        }
    }

    fun resetSignUpState()
    {
        liveSignInState.postValue(SignInState.SignedOut)
    }

    suspend fun attemptSignIn(username: String, password: String)
    {
        liveSignInState.postValue(SignInState.SigningIn)

        try
        {
            val result = Amplify.Auth.signIn(username, password)
            if (result.isSignInComplete)
            {
                Log.i("AuthQuickstart", "Sign in succeeded")
                getData()
            }
            else
            {
                Log.e("AuthQuickstart", "Sign in not complete")
            }
        }
        catch (error: AuthException)
        {
            Log.i("AuthQuickstart", "Sign in failed", error)
            liveSignInState.postValue(SignInState.SignInFailed)
        }
        catch (error: com.amplifyframework.auth.AuthException.UserNotFoundException)
        {
            Log.i("AuthQuickstart", "User not found", error)
            liveSignInState.postValue(SignInState.SignInFailedUserNotFound)
        }
        catch (error: TooManyRequestsException)
        {
            Log.i("AuthQuickstart", "Too many attempts to login have been made", error)
            liveSignInState.postValue(SignInState.SignInFailedTooManyRequests)
        }
        catch (error: UserNotConfirmedException)
        {
            Log.i("AuthQuickstart", "User hasn't confirmed registration", error)
            liveSignInState.postValue(SignInState.SignInFailedUserNotConfirmed)
        }
    }

    suspend fun checkAuthSession()
    {
        try {
            val session = Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession
            val id = session.identityId
            Log.i("AmplifyQuickstart", "Auth session = $session")
            if(id.type == AuthSessionResult.Type.SUCCESS && session.isSignedIn) {
                liveSignInState.postValue(SignInState.SigningIn)
                getData()
            }
            else
            {
                liveSignInState.postValue(SignInState.SignedOut)
            }
        } catch (error: AuthException) {
            Log.e("AmplifyQuickstart", "Failed to fetch auth session", error)
            liveSignInState.postValue(SignInState.SignedOut)
        }
    }

    private suspend fun getUserData()
    {
        val request = RestOptions.builder()
                .addPath("/participant")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded: ${response.data.asString()}")
            val user: Array<UserData> = Gson().fromJson(response.data.asString(), Array<UserData>::class.java)
            userData = user[0]

            getPlanData()
        } catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun getPlanData()
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndisNumber}")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Plan Data: ${response.data.asString()}")
            val user: UserData = Gson().fromJson(response.data.asString(), UserData::class.java)
            userData = user

            getPlanDetails()
        } catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun getPlanDetails()
    {
        if (!userData.plans.isNullOrEmpty())
        {
            for(i in userData.plans!!.indices)
            {
                val request = RestOptions.builder()
                        .addPath("/participant/${userData.ndisNumber}/${userData.plans!![i].planID}")
                        .build()

                try{
                    val response = Amplify.API.get(request, "mobileAPI")
                    Log.i("GAZ_INFO", "GET succeeded for PlanDetails: ${response.data.asString()}")
                    val userPlan: UserPlan = Gson().fromJson(response.data.asString(), UserPlan::class.java)

                    userData.plans?.set(i, userPlan)

                    getInvoiceDetails(userData.plans!![i])
                    getProviderSummary(userData.plans!![i])
                } catch (error: ApiException)
                {
                    Log.e("GAZ_ERROR", "GET failed.", error)
                }
            }
        }
        else
            liveSignInState.postValue(SignInState.SignInFailed)

    }

    private suspend fun getInvoiceDetails(plan: UserPlan)
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndisNumber}/${plan.planID}/invoices")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Invoice Details: ${response.data.asString()}")
            val invoices: Array<UserInvoice> = Gson().fromJson(response.data.asString(), Array<UserInvoice>::class.java)

            plan.planInvoices = invoices
        } catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun getProviderSummary(plan: UserPlan)
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndisNumber}/${plan.planID}/providerSummary")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Plan Provider Summary: ${response.data.asString()}")
            val providerSummary: ProviderSummary = Gson().fromJson(response.data.asString(), ProviderSummary::class.java)
            plan.planProviderSummary = providerSummary
            Log.i("GAZ_INFO", "Provider Summary: ${plan.planProviderSummary}")
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }
}