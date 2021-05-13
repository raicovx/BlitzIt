package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class PrimaryContact(
    @PrimaryKey(autoGenerate = true)
    val primaryContactID: Long,
    val ndis_number: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val mobile: String,
    @SerializedName("address_line")
    val addressLine: String,
    val suburb: String,
    val postcode: String,
    val state: String,
    val email: String)