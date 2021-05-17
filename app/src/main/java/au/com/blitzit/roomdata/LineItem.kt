package au.com.blitzit.roomdata

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["plan_id", "invoice_id", "supportCode", "total"])
data class LineItem(
    val plan_id: String,
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