package au.com.blitzit.data

import au.com.blitzit.helper.CranstekHelper
import java.util.*

data class UserInvoice constructor(
        val invoice_id: String,
        val provider: String,
        val invoice_number: String,
        val invoice_date: String,
        val process_date: String,
        val first_support_date: String,
        val last_support_date: String,
        val payment_date: String?,
        val amount: Double,
        val status: String)
{
}