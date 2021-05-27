package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Category

@Dao
interface CategoryDAO
{
    @Query("SELECT * FROM Category WHERE plan_id = :planID AND ndis_number = :ndis")
    suspend fun getCategoriesByPlan(planID: String, ndis: Int): List<Category>

    @Query("SELECT * FROM Category WHERE category = :category AND label = :label AND ndis_number = :ndis LIMIT 1")
    suspend fun getCategoryByCategoryAndLabel(category: String, label: String, ndis: Int): Category

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateCategory(category: Category)

    @Transaction
    suspend fun upsertCategory(category: Category)
    {
        try
        {
            insertCategory(category)
        }
        catch (e: SQLiteConstraintException)
        {
            updateCategory(category)
        }
    }
}