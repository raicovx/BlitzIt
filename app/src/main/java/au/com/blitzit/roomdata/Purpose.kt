package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["plan_id", "name"])
data class Purpose(
    val purpose_position: Int,
    val plan_id: String,
    val name: String
)