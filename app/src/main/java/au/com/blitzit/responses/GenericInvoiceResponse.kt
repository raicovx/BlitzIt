package au.com.blitzit.responses

import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.LineItem

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
    val status: String,
    val items: List<GenericLineItemResponse>)
{
    fun toInvoice(planID: String): Invoice
    {
        return Invoice(
            AuthServices.loggedParticipant.ndisNumber,
            invoice_id,
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

    fun toLineItems(invoice_id: String, planID: String): List<LineItem>
    {
        var lineItems: List<LineItem> = emptyList()

        for(lineItem: GenericLineItemResponse in items)
        {
            lineItems = lineItems.plus(LineItem(
                planID,
                invoice_id,
                lineItem.support_code,
                lineItem.description,
                lineItem.unit_price,
                lineItem.quantity,
                lineItem.support_end_date,
                lineItem.support_start_date,
                lineItem.total,
                lineItem.status)
            )
        }

        return lineItems
    }
}

data class GenericLineItemResponse(
    val support_code: String,
    val description: String,
    val unit_price: Double,
    val quantity: Float,
    val support_end_date: String,
    val support_start_date: String,
    val total: Double,
    val status: String)