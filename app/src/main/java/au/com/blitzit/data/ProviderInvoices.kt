package au.com.blitzit.data

data class ProviderInvoices(val provider: String, var invoices: List<UserInvoice>?)
{
    init {
        invoices = emptyList()
    }
}