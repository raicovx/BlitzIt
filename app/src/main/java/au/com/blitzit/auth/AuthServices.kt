package au.com.blitzit.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.data.*
import au.com.blitzit.responses.*
import au.com.blitzit.roomdata.*
import com.amazonaws.mobile.auth.core.signin.AuthException
import com.amazonaws.services.cognitoidentity.model.TooManyRequestsException
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
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
    private lateinit var appDatabase: AppDatabase
    lateinit var loggedUser: User
        private set
    lateinit var loggedParticipant: Participant
        private set
    private lateinit var genericPlans: Array<GenericPlanResponse>

    val liveSignInState = MutableLiveData<SignInState>()

    lateinit var userData: UserData
        private set //Makes setting userdata private

    private suspend fun getData()
    {
        getParticipantData()
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

    suspend fun attemptSignIn(username: String, password: String, context: Context)
    {
        appDatabase = AppDatabase.getDatabase(context)

        liveSignInState.postValue(SignInState.SigningIn)

        try
        {
            val result = Amplify.Auth.signIn(username, password)
            if (result.isSignInComplete)
            {
                Log.i("AuthQuickstart", "Sign in succeeded")

                handleUserData(username)

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

    suspend fun checkAuthSession(context: Context)
    {
        appDatabase = AppDatabase.getDatabase(context)
        try
        {
            val session = Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession
            val id = session.identityId
            Log.i("AmplifyQuickstart", "Auth session = $session")
            if(id.type == AuthSessionResult.Type.SUCCESS && session.isSignedIn)
            {
                liveSignInState.postValue(SignInState.SigningIn)

                try {
                    val attributes = Amplify.Auth.fetchUserAttributes()
                    Log.i("AuthDemo", "User attributes = $attributes")
                    for(attr: AuthUserAttribute in attributes)
                    {
                        if(attr.key == AuthUserAttributeKey.email())
                            handleUserData(attr.value)
                    }
                }catch (error: AuthException) {
                    Log.e("AuthDemo", "Failed to fetch user attributes", error)
                }

                getData()
            }
            else
            {
                liveSignInState.postValue(SignInState.SignedOut)
            }
        }
        catch (error: AuthException)
        {
            Log.e("AmplifyQuickstart", "Failed to fetch auth session", error)
            liveSignInState.postValue(SignInState.SignedOut)
        }
    }

    private suspend fun handleUserData(email: String)
    {
        if(!appDatabase.userDAO().doesUserExist(email))
        {
            appDatabase.userDAO().insertUser(User(0, email))
        }

        loggedUser = appDatabase.userDAO().findByEmail(email)
        Log.i("GAZ_DB", "Logged User: $loggedUser")
    }

    private suspend fun getParticipantData()
    {
        val request = RestOptions.builder()
                .addPath("/participant")
                .build()

        try
        {
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET participants succeeded: ${response.data.asString()}")

            handleParticipantData(response)

            getPlanData()
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun handleParticipantData(data: RestResponse)
    {
        //Generic Class
        var genericParticipants: Array<GenericParticipantResponse> = Gson().fromJson(data.data.asString(), Array<GenericParticipantResponse>::class.java)

        //Participant
        val participant = genericParticipants[0].toParticipant(loggedUser.user_id)
        Log.i("GAZ_INFO", "participant: $participant")
        appDatabase.participantDAO().upsertParticipant(participant)
        loggedParticipant = appDatabase.participantDAO().getParticipantByUserID(loggedUser.user_id)

        //Primary Contacts
        //TODO("Primary contacts")

        //Support Contacts
        //TODO("Support contacts")
    }

    private suspend fun getPlanData()
    {
        val request = RestOptions.builder()
                .addPath("/participant/${loggedParticipant.ndisNumber}")
                .build()

        try
        {
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Plan Data: ${response.data.asString()}")

            handlePlanData(response)

            getPlanDetails()
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun handlePlanData(response: RestResponse)
    {
        //Generic Class
        val genericParticipant = Gson().fromJson(response.data.asString(), GenericParticipantResponse::class.java)
        genericPlans = genericParticipant.plans

        for(genericPlan: GenericPlanResponse in genericPlans)
        {
            //Plan
            val plan = genericPlan.toPlan(loggedParticipant.ndisNumber)
            appDatabase.planDAO().upsertPlan(plan)

            //Purposes
            val purposes = genericPlan.getPurposes()
            for(purpose: Purpose in purposes)
            {
                appDatabase.purposeDAO().upsertPurpose(purpose)

                //Categories
                val categories = genericPlan.getCategoriesByString(purpose)
                for(category: Category in categories)
                {
                    appDatabase.categoryDAO().upsertCategory(category)
                }
            }
        }
    }

    /**
     * Gets all plans invoices and provider details
     */
    private suspend fun getPlanDetails()
    {
        for(i in genericPlans.indices)
        {
            getPlanInvoices(genericPlans[i])
            getPlanProviderSummaries(genericPlans[i])
        }
    }

    private suspend fun getPlanInvoices(plan: GenericPlanResponse)
    {
        val request = RestOptions.builder()
                .addPath("/participant/${loggedParticipant.ndisNumber}/${plan.planID}/invoices")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Invoice Details: ${response.data.asString()}")

            handleInvoiceData(response, plan.planID)
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun handleInvoiceData(response: RestResponse, planID: String)
    {
        val invoices: Array<GenericInvoiceResponse> = Gson().fromJson(response.data.asString(), Array<GenericInvoiceResponse>::class.java)

        for(invoice: GenericInvoiceResponse in invoices)
            appDatabase.invoiceDAO().upsertInvoice(invoice.toInvoice(planID))
    }

    private suspend fun getPlanProviderSummaries(plan: GenericPlanResponse)
    {
        val request = RestOptions.builder()
                .addPath("/participant/${loggedParticipant.ndisNumber}/${plan.planID}/providerSummary")
                .build()

        try{
            val response = Amplify.API.get(request, "mobileAPI")
            Log.i("GAZ_INFO", "GET succeeded for Plan Provider Summary: ${response.data.asString()}")

            handleProviderSummeryData(response, plan.planID)
        }
        catch (error: ApiException)
        {
            Log.e("GAZ_ERROR", "GET failed.", error)
        }
    }

    private suspend fun handleProviderSummeryData(response: RestResponse, planID: String)
    {
        val genericProvider: GenericProviderOverviewResponse = Gson().fromJson(response.data.asString(), GenericProviderOverviewResponse::class.java)

        for(summary: GenericProviderSummaryResponse in genericProvider.providerOverview)
        {
            //Provider
            appDatabase.providerDAO().upsertProvider(summary.getProvider(planID))

            //Provider Category Spending
            for(spending: ProviderCategorySpending in summary.getProviderCategorySpending())
            {
                appDatabase.providerCategorySpendingDAO().upsertProviderCategorySpending(spending)
            }
        }
    }
}