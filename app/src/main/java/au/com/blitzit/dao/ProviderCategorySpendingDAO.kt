package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.ProviderCategorySpending

@Dao
interface ProviderCategorySpendingDAO
{
    @Query("SELECT * FROM ProviderCategorySpending WHERE plan_id = :planID AND label = :label")
    suspend fun getProviderCategorySpendingByLabel(planID: String, label: String): List<ProviderCategorySpending>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProviderCategorySpending(providerCategorySpending: ProviderCategorySpending)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateProviderCategorySpending(providerCategorySpending: ProviderCategorySpending)

    @Transaction
    suspend fun upsertProviderCategorySpending(providerCategorySpending: ProviderCategorySpending)
    {
        try
        {
            insertProviderCategorySpending(providerCategorySpending)
        }
        catch (e: SQLiteConstraintException)
        {
            updateProviderCategorySpending(providerCategorySpending)
        }
    }
}