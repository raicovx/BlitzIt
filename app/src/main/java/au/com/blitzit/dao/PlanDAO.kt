package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Plan

@Dao
interface PlanDAO
{
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

    @Query("SELECT * FROM `plan` ORDER BY plan_start_date DESC LIMIT 1")
    suspend fun getMostRecentPlan(): Plan
}