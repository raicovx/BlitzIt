package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.data.UserData
import au.com.blitzit.data.UserInvoice
import au.com.blitzit.data.UserPlan
import com.amazonaws.mobile.auth.core.signin.AuthException
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.kotlin.core.Amplify
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.lang.Exception

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
        //checkAuthSession()
        //liveSignInState.postValue(SignInState.SignedOut)
    }

    suspend fun attemptSignIn(username: String, password: String)
    {
        liveSignInState.postValue(SignInState.SigningIn)

        try
        {
            val result = Amplify.Auth.signIn("username", "password")
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
            Log.e("AuthQuickstart", "Sign in failed", error)
        }

        /*Amplify.Auth.signIn(username, password,
                { result ->
                    if (result.isSignInComplete)
                    {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        getData()
                        //liveSignInState.postValue(SignInState.SignedIn)
                    }
                    else
                    {
                        Log.i("AuthQuickstart", "Sign in not complete")
                        liveSignInState.postValue(SignInState.SignInFailed)
                    }
                },
                {
                    Log.e("AuthQuickstart", "Failed to sign in", it)
                    liveSignInState.postValue(SignInState.SignInFailed)
                }
        )*/
    }

    suspend fun checkAuthSession()
    {
        try {
            val session = Amplify.Auth.fetchAuthSession()
            Log.i("AmplifyQuickstart", "Auth session = $session")
            if(session.isSignedIn) {
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

        /*Amplify.Auth.fetchAuthSession(
                {
                    Log.i("AmplifyQuickstart", "Auth session = $it")
                    if(it.isSignedIn) {
                        liveSignInState.postValue(SignInState.SigningIn)
                        getData()
                    }
                    else
                    {
                        liveSignInState.postValue(SignInState.SignedOut)
                    }
                },
                {
                    Log.e("AmplifyQuickstart", "Failed to fetch auth session")
                    liveSignInState.postValue(SignInState.SignedOut)
                }
        )*/
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

        /*Amplify.API.get("mobileAPI", request,
                {
                    Log.i("GAZ_INFO", "GET succeeded: ${it.data.asString()}")
                    val user: Array<UserData> = Gson().fromJson(it.data.asString(), Array<UserData>::class.java)
                    userData = user[0]

                    getPlanData()
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )*/
    }

    private suspend fun getPlanData()
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndis_number}")
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
        /*Amplify.API.get("mobileAPI", request,
                { it ->
                    Log.i("GAZ_INFO", "GET succeeded for Plan Data: ${it.data.asString()}")
                    val user: UserData = Gson().fromJson(it.data.asString(), UserData::class.java)
                    userData = user

                    getPlanDetails()
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )*/
    }

    private suspend fun getPlanDetails()
    {
        if (!userData.plans.isNullOrEmpty())
        {
            for(i in userData.plans!!.indices)
            {
                val request = RestOptions.builder()
                        .addPath("/participant/${userData.ndis_number}/${userData.plans!![i].planID}")
                        .build()

                try{
                    val response = Amplify.API.get(request, "mobileAPI")
                    Log.i("GAZ_INFO", "GET succeeded for PlanDetails: ${response.data.asString()}")
                    val userPlan: UserPlan = Gson().fromJson(response.data.asString(), UserPlan::class.java)
                    userData.plans?.set(i, userPlan)

                    getInvoiceDetails(userData.plans!![i])
                } catch (error: ApiException)
                {
                    Log.e("GAZ_ERROR", "GET failed.", error)
                }
                /*Amplify.API.get("mobileAPI", request,
                        {
                            Log.i("GAZ_INFO", "GET succeeded for PlanDetails: ${it.data.asString()}")
                            val userPlan: UserPlan = Gson().fromJson(it.data.asString(), UserPlan::class.java)
                            userData.plans?.set(i, userPlan)

                            getInvoiceDetails(userData.plans!![i])
                        },
                        {
                            Log.e("GAZ_ERROR", "GET failed.", it)
                        }
                )*/
            }
        }
        else
            liveSignInState.postValue(SignInState.SignInFailed)

    }

    private suspend fun getInvoiceDetails(plan: UserPlan)
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndis_number}/${plan.planID}/invoices")
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

        /*Amplify.API.get("mobileAPI", request,
                {
                    Log.i("GAZ_INFO", "GET succeeded for Invoice Details: ${it.data.asString()}")
                    val invoices: Array<UserInvoice> = Gson().fromJson(it.data.asString(), Array<UserInvoice>::class.java)
                    plan.planInvoices = invoices
                    Log.i("GAZ_INFO", "Invoice test: ${plan.planInvoices!![0].provider}")
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )*/
    }

    private fun getDatatest() = runBlocking {
        val job = launch { getUserData() }
        job.join()
        userData.setSelectedPlan(userData.getDefaultPlan())
        liveSignInState.postValue(SignInState.SignedIn)
    }

    private suspend fun getData()
    {
        getUserData()
        userData.setSelectedPlan(userData.getDefaultPlan())
        liveSignInState.postValue(SignInState.SignedIn)
    }


    suspend fun attemptSignOut()
    {
        try {
            Amplify.Auth.signOut()
            Log.i("AuthQuickstart", "Signed out successfully")
        } catch (error: AuthException) {
            Log.e("AuthQuickstart", "Sign out failed", error)
        }
        /*Amplify.Auth.signOut(
                {
                    Log.i("AuthQuickstart", "Signed out successfully")
                    liveSignInState.postValue(SignInState.SignedOut)
                },
                {
                    Log.e("AuthQuickstart", "Sign out failed", it)
                }
        )*/
    }
}