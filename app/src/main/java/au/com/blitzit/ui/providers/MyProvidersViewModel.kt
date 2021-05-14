package au.com.blitzit.ui.providers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.Provider

class MyProvidersViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val providersList: LiveData<List<Provider>> = liveData {
        val data = getProviders()
        emit(data)
    }

    private suspend fun getProviders(): List<Provider> = appDatabase.providerDAO().getAllProviders(AuthServices.selectedPlan.plan_id)
}