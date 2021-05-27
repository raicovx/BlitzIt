package au.com.blitzit.ui.trackspending

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.ProviderCategorySpending

class TrackSpendingViewModel(app: Application) : AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    lateinit var categories: List<Category>
    val selectedCategory = MutableLiveData<Category>()

    val providerCategorySpendingList = MutableLiveData<List<ProviderCategorySpending>>()

    suspend fun getCategories()
    {
        categories = appDatabase.categoryDAO().getCategoriesByPlan(AuthServices.selectedPlan.plan_id, AuthServices.loggedParticipant.ndisNumber)
    }

    fun getCategoryLabels(): List<String>
    {
        var labels = emptyList<String>()
        for(category: Category in categories)
        {
            labels = if((category.purpose == "CORE" || category.purpose == "Core") && !labels.contains("CORE"))
                labels.plus("CORE")
            else
                labels.plus(category.label)
        }

        return labels
    }

    suspend fun getProviderCategorySpending(purposeName: String, planID: String, label: String)
    {
        if(purposeName == "CORE" || purposeName == "Core")
        {
            var cumulativeList: List<ProviderCategorySpending> = emptyList()
            for(categoryLabel: String in CranstekHelper.getCoreLabels())
            {
                cumulativeList = cumulativeList.plus(appDatabase.providerCategorySpendingDAO().getProviderCategorySpendingByLabel(planID, categoryLabel))
            }
            providerCategorySpendingList.postValue(cumulativeList)
        }
        else
            providerCategorySpendingList.postValue(appDatabase.providerCategorySpendingDAO().getProviderCategorySpendingByLabel(planID, label))
    }

    suspend fun getProviderName(providerID: String): String = appDatabase.providerDAO().getProviderNameById(providerID)
}