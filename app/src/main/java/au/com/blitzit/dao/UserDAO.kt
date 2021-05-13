package au.com.blitzit.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import au.com.blitzit.roomdata.User

@Dao
interface UserDAO
{
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE email LIKE :email LIMIT 1")
    suspend fun findByEmail(email: String): User

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE email = :email)")
    suspend fun doesUserExist(email: String): Boolean
}