package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Purpose

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
}