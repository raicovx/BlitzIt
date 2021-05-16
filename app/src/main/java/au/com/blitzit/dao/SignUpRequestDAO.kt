package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.SignUpRequest

@Dao
interface SignUpRequestDAO
{
    @Query("SELECT * FROM SignUpRequest LIMIT 1")
    suspend fun getSignUpRequest(): SignUpRequest

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSignUpRequest(signUpRequest: SignUpRequest)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateSignUpRequest(signUpRequest: SignUpRequest)

    @Transaction
    suspend fun upsertSignUpRequest(signUpRequest: SignUpRequest)
    {
        try
        {
            insertSignUpRequest(signUpRequest)
        }
        catch (e: SQLiteConstraintException)
        {
            updateSignUpRequest(signUpRequest)
        }
    }

    @Query("DELETE FROM SignUpRequest")
    suspend fun nukeTable()
}