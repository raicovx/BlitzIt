package au.com.blitzit.roomdata

import androidx.room.Embedded
import androidx.room.Relation

data class InvoiceWithLineItems(
    @Embedded
    val invoice: Invoice,
    @Relation(
        parentColumn = "invoice_id",
        entityColumn = "invoice_id"
    )
    val lineItems: List<LineItem>
)