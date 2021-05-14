package au.com.blitzit.responses

import au.com.blitzit.roomdata.Provider
import au.com.blitzit.roomdata.ProviderCategorySpending
import com.google.gson.annotations.SerializedName

data class GenericProviderOverviewResponse(
    @SerializedName("provider_overview")
    val providerOverview: List<GenericProviderSummaryResponse>
)

data class GenericProviderSummaryResponse(
    val provider: GenericProviderResponse,
    val categories: List<GenericProviderCategorySpendingResponse>)
{
    fun getProvider(planId: String): Provider
    {
        return Provider(
            provider.id,
            planId,
            provider.name,
            provider.abn,
            provider.addressLine,
            provider.suburb,
            provider.state,
            provider.postcode,
            provider.email,
            provider.phone)
    }

    fun getProviderCategorySpending(): List<ProviderCategorySpending>
    {
        var pCSpending: List<ProviderCategorySpending> = emptyList()
        for(spend: GenericProviderCategorySpendingResponse in categories)
        {
            pCSpending = pCSpending.plus(ProviderCategorySpending(provider.id, spend.label, spend.spend))
        }

        return pCSpending
    }
}

data class GenericProviderResponse(
    val id: String,
    val abn: String,
    val name: String,
    @SerializedName("address_line")
    val addressLine: String,
    val suburb: String,
    val state: String,
    val postcode: String,
    val email: String,
    val phone: List<String>)

data class GenericProviderCategorySpendingResponse(
    val label: String,
    val spend: Double)