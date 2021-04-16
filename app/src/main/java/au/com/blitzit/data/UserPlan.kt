package au.com.blitzit.data

import android.util.Log
import au.com.blitzit.auth.AuthServices
import com.amplifyframework.util.Empty
import com.google.gson.annotations.SerializedName

class UserPlan constructor(
        val status: String,
        @SerializedName("plan_start_date")
        val planStartDate: String,
        @SerializedName("plan_id")
        val planID: String,
        @SerializedName("plan_end_date")
        val planEndDate: String,
        @SerializedName("parts")
        val planParts: Array<PlanParts>,
        var planInvoices: Array<UserInvoice>?,
        var planProviderSummary: ProviderSummary?)
{
    fun getSortingOrder(): Int
    {
        when(status)
        {
            "Active" -> return 0
            "Expired" -> return 1
            "Archived" -> return 2
        }

        return 0
    }

    fun getPartCategories(): List<String>
    {
        var categories: List<String> = listOf()
        for(part: PlanParts in planParts)
        {
            if(!categories.contains(part.category)) {
                categories = categories.plus(part.category)
            }
        }

        return categories
    }

    fun getPartListByCategory(category: String): List<PlanParts>
    {
        var parts: List<PlanParts> = listOf()
        for(part: PlanParts in planParts)
        {
            if(part.category == category)
                parts = parts.plus(part)
        }

        return parts
    }

    fun getInvoicesByProvider(): List<ProviderInvoices>
    {
        var providerInvoices = getEmptyProviderInvoiceList()

        //Sorts invoices into respectable providers
        for(providerInvoice: ProviderInvoices in providerInvoices)
        {
            for(invoice: UserInvoice in planInvoices!!)
            {
                if(invoice.provider == providerInvoice.provider)
                    providerInvoice.invoices = providerInvoice.invoices!!.plus(invoice)
            }
        }

        return providerInvoices
    }

    private fun getEmptyProviderInvoiceList(): List<ProviderInvoices>
    {
        var providerInvoices: List<ProviderInvoices> = emptyList()
        var providers: List<String> = emptyList()

        //Create a string list of the providers
        for(invoice: UserInvoice in planInvoices!!)
        {
            if(providers.isNullOrEmpty() || !providers.contains(invoice.provider))
                providers = providers.plus(invoice.provider)
        }

        //create provider invoices so we can store invoices by provider
        for(provider: String in providers)
        {
            val providerInvoice = ProviderInvoices(provider, null)
            providerInvoices = providerInvoices.plus(providerInvoice)
        }

        return providerInvoices
    }

    fun getInvoicesByMostRecent(): List<UserInvoice>
    {
        val sortedInvoices: List<UserInvoice> = planInvoices!!.toList()
        sortedInvoices.sortedByDescending { it.invoice_date }
        Log.i("GAZ_INGO", "sorted invoice: $sortedInvoices")
        return sortedInvoices
    }
}

data class ProviderInvoices(val provider: String, var invoices: List<UserInvoice>?)
{
    init {
        invoices = emptyList()
    }
}