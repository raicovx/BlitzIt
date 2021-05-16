package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Plan

@Dao
interface PlanDAO
{
    @Query("SELECT * FROM `Plan` WHERE ndis_number = :ndisNumber ORDER BY status ASC")
    suspend fun getPlans(ndisNumber: Int): List<Plan>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlan(plan: Plan)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updatePlan(plan: Plan)

    @Transaction
    suspend fun upsertPlan(plan: Plan)
    {
        try
        {
            insertPlan(plan)
        }
        catch (e: SQLiteConstraintException)
        {
            updatePlan(plan)
        }
    }

    @Query("SELECT * FROM `plan` WHERE ndis_number = :ndisNumber ORDER BY plan_start_date DESC LIMIT 1")
    suspend fun getMostRecentPlan(ndisNumber: Int): Plan
}