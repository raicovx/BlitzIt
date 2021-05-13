package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Purpose(
    @PrimaryKey
    val plan_id: String,
    val name: String
)