package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class SupportCoordinator (
    @PrimaryKey(autoGenerate = true)
    val supportCoordinatorID: Long,
    val ndis_number: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String)