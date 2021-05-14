package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Purpose
import au.com.blitzit.roomdata.PurposeWithCategories

@Dao
interface PurposeDAO
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPurpose(purpose: Purpose)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updatePurpose(purpose: Purpose)

    @Transaction
    suspend fun upsertPurpose(purpose: Purpose)
    {
        try
        {
            insertPurpose(purpose)
        }
        catch (e: SQLiteConstraintException)
        {
            updatePurpose(purpose)
        }
    }

    @Transaction
    @Query("SELECT * FROM Purpose WHERE plan_id = :planID ORDER BY purpose_position ASC")
    suspend fun getPurposesWithCategories(planID: String): List<PurposeWithCategories>
}