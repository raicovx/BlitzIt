package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["ndis_number", "firstName", "lastName"])
data class SupportCoordinator (
    val ndis_number: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String)