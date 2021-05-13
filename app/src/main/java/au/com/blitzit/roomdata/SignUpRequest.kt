package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SignUpRequest(
    @PrimaryKey
    val request_id: String,
    val email: String,
    val password: String,
    val attempts: Int
)