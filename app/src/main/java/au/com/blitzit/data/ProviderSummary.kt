package au.com.blitzit.data

data class ProviderSummary(val name: String, val categories: List<ProviderSpending> )

data class ProviderSpending(val label: String, val spend: Double)