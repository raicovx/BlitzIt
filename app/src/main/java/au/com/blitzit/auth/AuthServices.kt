package au.com.blitzit.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.data.UserData
import au.com.blitzit.data.UserInvoice
import au.com.blitzit.data.UserPlan
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.google.gson.Gson
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

    fun attemptSignIn(username: String, password: String)
    {
        liveSignInState.postValue(SignInState.SigningIn)
        Amplify.Auth.signIn(username, password,
                { result ->
                    if (result.isSignInComplete)
                    {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        getUserData()
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

        )
    }

    fun checkAuthSession()
    {
        Amplify.Auth.fetchAuthSession(
                {
                    Log.i("AmplifyQuickstart", "Auth session = $it")
                    if(it.isSignedIn) {
                        liveSignInState.postValue(SignInState.SigningIn)
                        getUserData()
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

                    getPlanData()
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )
    }

    private fun getPlanData()
    {
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndis_number}")
                .build()

        Amplify.API.get("mobileAPI", request,
                { it ->
                    Log.i("GAZ_INFO", "GET succeeded for Plan Data: ${it.data.asString()}")
                    val user: UserData = Gson().fromJson(it.data.asString(), UserData::class.java)
                    userData = user

                    getPlanDetails()
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )
    }

    private fun getPlanDetails()
    {
        val plan : UserPlan = userData.getMostRecentPlan()
        val index: Int? = userData.plans?.indexOf(plan)
        if (index != null) {
            val request = RestOptions.builder()
                    .addPath("/participant/${userData.ndis_number}/${plan.planID}")
                    .build()

            Amplify.API.get("mobileAPI", request,
                    {
                        Log.i("GAZ_INFO", "GET succeeded for PlanDetails: ${it.data.asString()}")
                        val userPlan: UserPlan = Gson().fromJson(it.data.asString(), UserPlan::class.java)
                        userData.plans?.set(index, userPlan)

                        getInvoiceDetails()
                    },
                    {
                        Log.e("GAZ_ERROR", "GET failed.", it)
                    }
            )
        } else
            liveSignInState.postValue(SignInState.SignInFailed)

    }

    private fun getInvoiceDetails()
    {
        val plan : UserPlan = userData.getMostRecentPlan()
        val request = RestOptions.builder()
                .addPath("/participant/${userData.ndis_number}/${plan.planID}/invoices")
                .build()

        Amplify.API.get("mobileAPI", request,
                {
                    Log.i("GAZ_INFO", "GET succeeded for Invoice Details: ${it.data.asString()}")
                    val invoices: Array<UserInvoice> = Gson().fromJson(it.data.asString(), Array<UserInvoice>::class.java)
                    plan.planInvoices = invoices
                    Log.i("GAZ_INFO", "Invoice test: ${plan.planInvoices!![0].provider}")

                    liveSignInState.postValue(SignInState.SignedIn)
                },
                {
                    Log.e("GAZ_ERROR", "GET failed.", it)
                }
        )
    }

    fun attemptSignOut()
    {
        Amplify.Auth.signOut(
                {
                    Log.i("AuthQuickstart", "Signed out successfully")
                    liveSignInState.postValue(SignInState.SignedOut)
                },
                {
                    Log.e("AuthQuickstart", "Sign out failed", it)
                }
        )
    }
}