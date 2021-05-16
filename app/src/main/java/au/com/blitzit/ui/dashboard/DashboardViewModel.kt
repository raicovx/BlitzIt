package au.com.blitzit.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.Participant
import au.com.blitzit.roomdata.Plan
import au.com.blitzit.roomdata.PurposeWithCategories

class DashboardViewModel(app: Application) : AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val participant: Participant = AuthServices.loggedParticipant
    val selectedPlan: Plan = AuthServices.selectedPlan

    val purposes: LiveData<List<PurposeWithCategories>> = liveData{
        val data = getPurposesWithCategories()
        emit(data)
    }

    private suspend fun getPurposesWithCategories(): List<PurposeWithCategories>
    {
        return appDatabase.purposeDAO().getPurposesWithCategories(selectedPlan.plan_id)
    }

    /***
     * Resets the selected plan back to the most recent
     * Usage: When an older plan is selected and we hit the back button on the dashboard we go back to the plans page and also reset this
     */
    suspend fun resetSelectedPlan()
    {
        AuthServices.selectedPlan = appDatabase.planDAO().getMostRecentPlan(AuthServices.loggedParticipant.ndisNumber)
    }

    /***
     * Compares the selected plan to the most recent plan
     */
    suspend fun isSelectedPlanMostRecentPlan(): Boolean
    {
        val mostRecent = appDatabase.planDAO().getMostRecentPlan(AuthServices.loggedParticipant.ndisNumber)
        return selectedPlan == mostRecent
    }
}