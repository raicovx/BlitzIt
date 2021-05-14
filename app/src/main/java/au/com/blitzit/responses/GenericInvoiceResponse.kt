package au.com.blitzit.responses

import au.com.blitzit.roomdata.Invoice

data class GenericInvoiceResponse(
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
    fun toInvoice(planID: String): Invoice
    {
        return Invoice(invoice_id,
            planID,
            provider,
            invoice_number,
            invoice_date,
            process_date,
            first_support_date,
            last_support_date,
            payment_date,
            amount,
            status)
    }
}