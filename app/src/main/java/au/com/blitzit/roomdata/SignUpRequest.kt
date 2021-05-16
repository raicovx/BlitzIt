package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["request_id", "email"])
data class SignUpRequest(
    val request_id: String,
    val email: String,
    val password: String,
    val attempts: Int
)