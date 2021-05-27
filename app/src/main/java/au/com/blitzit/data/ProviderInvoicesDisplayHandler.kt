package au.com.blitzit.data

import android.view.ViewGroup

data class ProviderInvoicesDisplayHandler(val providerAndInvoices: ProviderAndInvoices,
                                          var numberOfDisplayingInvoices: Int,
                                          val container: ViewGroup)