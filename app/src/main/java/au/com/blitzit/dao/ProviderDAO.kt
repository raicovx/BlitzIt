package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Provider

@Dao
interface ProviderDAO
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProvider(provider: Provider)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateProvider(provider: Provider)

    @Transaction
    suspend fun upsertProvider(provider: Provider)
    {
        try
        {
            insertProvider(provider)
        }
        catch (e: SQLiteConstraintException)
        {
            updateProvider(provider)
        }
    }
}