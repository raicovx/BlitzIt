package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Provider

@Dao
interface ProviderDAO
{
    @Query("SELECT name FROM Provider WHERE provider_id = :providerID")
    suspend fun getProviderNameById(providerID: String): String

    @Query("SELECT * FROM Provider WHERE provider_id = :providerID")
    suspend fun getProviderById(providerID: String): Provider

    @Query("SELECT * FROM Provider WHERE plan_id = :planID")
    suspend fun getAllProviders(planID: String): List<Provider>

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