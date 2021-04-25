package au.com.blitzit.data

import com.google.gson.annotations.SerializedName

data class ProviderSummary(@SerializedName("provider_overview")
                           val providerOverview: List<ProviderOverview>)