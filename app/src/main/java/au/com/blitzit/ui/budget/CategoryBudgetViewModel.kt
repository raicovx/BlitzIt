package au.com.blitzit.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.ProviderCategorySpending

class CategoryBudgetViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val categoryLiveData = MutableLiveData<Category>()
    val providerCategorySpendingList = MutableLiveData<List<ProviderCategorySpending>>()

    suspend fun getCategory(category: String, label: String)
    {
        categoryLiveData.postValue(appDatabase.categoryDAO().getCategoryByCategoryAndLabel(category, label))
    }

    suspend fun getProviderCategorySpending(purposeName: String, planID: String, label: String)
    {
        if(purposeName == "CORE" || purposeName == "Core")
        {
            var cumulativeList: List<ProviderCategorySpending> = emptyList()
            for(label: String in CranstekHelper.getCoreLabels())
            {
                cumulativeList = cumulativeList.plus(appDatabase.providerCategorySpendingDAO().getProviderCategorySpendingByLabel(planID, label))
            }
            providerCategorySpendingList.postValue(cumulativeList)
        }
        else
            providerCategorySpendingList.postValue(appDatabase.providerCategorySpendingDAO().getProviderCategorySpendingByLabel(planID, label))
    }

    suspend fun getProviderName(providerID: String): String = appDatabase.providerDAO().getProviderNameById(providerID)
}