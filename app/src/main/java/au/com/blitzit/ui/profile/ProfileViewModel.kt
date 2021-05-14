package au.com.blitzit.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.PrimaryContact
import au.com.blitzit.roomdata.SupportCoordinator

class ProfileViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val primaryContactEmail: LiveData<String> = liveData {
        val data = getPrimaryContactEmail()
        emit(data)
    }

    val supportCoordinatorEmail: LiveData<String> = liveData {
        val data = getSupportCoordinatorEmail()
        emit(data)
    }

    private suspend fun getPrimaryContactEmail(): String = appDatabase.primaryContactDAO()
        .getPrimaryContactEmail(AuthServices.loggedParticipant.ndisNumber)

    private suspend fun getSupportCoordinatorEmail(): String = appDatabase.supportCoordinatorDAO()
        .getSupportCoordinatorEmail(AuthServices.loggedParticipant.ndisNumber)
}