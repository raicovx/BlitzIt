package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.SupportCoordinator

@Dao
interface SupportCoordinatorDAO
{
    @Query("SELECT email FROM SupportCoordinator WHERE ndis_number = :ndisNumber LIMIT 1")
    suspend fun getSupportCoordinatorEmail(ndisNumber: Int): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSupportCoordinator(supportCoordinator: SupportCoordinator)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateSupportCoordinator(supportCoordinator: SupportCoordinator)

    @Transaction
    suspend fun upsertSupportCoordinator(supportCoordinator: SupportCoordinator)
    {
        try
        {
            insertSupportCoordinator(supportCoordinator)
        }
        catch (e: SQLiteConstraintException)
        {
            updateSupportCoordinator(supportCoordinator)
        }
    }
}