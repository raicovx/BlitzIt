package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.PrimaryContact

@Dao
interface PrimaryContactDAO
{
    @Query("SELECT email FROM PrimaryContact WHERE ndis_number = :ndisNumber LIMIT 1")
    suspend fun getPrimaryContactEmail(ndisNumber: Int): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPrimaryContact(primaryContact: PrimaryContact)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updatePrimaryContact(primaryContact: PrimaryContact)

    @Transaction
    suspend fun upsertPrimaryContact(primaryContact: PrimaryContact)
    {
        try
        {
            insertPrimaryContact(primaryContact)
        }
        catch (e: SQLiteConstraintException)
        {
            updatePrimaryContact(primaryContact)
        }
    }
}