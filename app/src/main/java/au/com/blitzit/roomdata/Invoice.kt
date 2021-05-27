package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["ndis_number", "invoice_id"])
data class Invoice(
    val ndis_number: Int,
    val invoice_id: String,
    val plan_id: String,
    val provider: String,
    val invoice_number: String,
    val invoice_date: String,
    val process_date: String,
    val first_support_date: String,
    val last_support_date: String,
    val payment_date: String?,
    val amount: Double,
    val status: String
)