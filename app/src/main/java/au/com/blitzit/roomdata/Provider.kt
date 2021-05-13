package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Provider(
    @PrimaryKey
    val provider_id: String,
    val name: String,
    val abn: String,
    @SerializedName("address_line")
    val addressLine: String,
    val suburb: String,
    val state: String,
    val postcode: String,
    val email: String,
    val phone: String
)