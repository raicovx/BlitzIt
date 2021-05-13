package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class LineItem(
    @PrimaryKey
    val id: String,
    val invoice_id: String,
    @SerializedName("support_code")
    val supportCode: String,
    @SerializedName("unit_price")
    val unitPrice: Double,
    val quantity: Int,
    @SerializedName("support_end_date")
    val supportEndDate: String,
    @SerializedName("support_start_date")
    val supportStartDate: String,
    val total: Double,
    val status: String
)