package au.com.blitzit.ui.plans

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.Plan

class MyPlansViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val plans: LiveData<List<Plan>> = liveData {
        val data = getPlans()
        emit(data)
    }

    private suspend fun getPlans(): List<Plan> = appDatabase.planDAO().getPlans(AuthServices.loggedParticipant.ndisNumber)
}