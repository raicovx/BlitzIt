package au.com.blitzit.data

import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.Provider

data class ProviderAndInvoices(val provider: Provider, var invoices: List<Invoice>)