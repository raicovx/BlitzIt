package au.com.blitzit.dao

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import au.com.blitzit.roomdata.Participant

@Dao
interface ParticipantDAO
{
    @Query("SELECT * FROM Participant WHERE user_id = :id LIMIT 1")
    suspend fun getParticipantByUserID(id: Long): Participant

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParticipant(participant: Participant)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateParticipant(participant: Participant)

    @Transaction
    suspend fun upsertParticipant(participant: Participant)
    {
        try
        {
            insertParticipant(participant)
        }
        catch (e: SQLiteConstraintException)
        {
            updateParticipant(participant)
        }
    }
}