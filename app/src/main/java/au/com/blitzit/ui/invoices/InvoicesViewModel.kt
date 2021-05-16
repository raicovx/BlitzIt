package au.com.blitzit.ui.invoices

import android.app.Application
import androidx.lifecycle.*
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.ProviderAndInvoices
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.Provider
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InvoicesViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    val mostRecentInvoiceList = MutableLiveData<List<Invoice>>()

    val providerAndInvoicesList = MutableLiveData<List<ProviderAndInvoices>>()

    private suspend fun getInvoices(): List<Invoice> = appDatabase.invoiceDAO().getInvoiceListByPlan(AuthServices.selectedPlan.plan_id)

    private suspend fun getProviders(): List<Provider> = appDatabase.providerDAO().getAllProviders(AuthServices.selectedPlan.plan_id)

    private suspend fun getInvoicesByProvider(providerName: String): List<Invoice> =
        appDatabase.invoiceDAO().getInvoiceListByProviderName(AuthServices.selectedPlan.plan_id, providerName)

    suspend fun getProvidersAndInvoices()
    {
        //Create a new list of providers and invoices
        var providersAndInvoices: List<ProviderAndInvoices> = emptyList()

        //Get providers
        val providers = getProviders()

        //Get invoices for each provider
        for(provider: Provider in providers)
        {
            val invoices = getInvoicesByProvider(provider.name)

            //Save Data into new list
            val providerAndInvoices = ProviderAndInvoices(provider, invoices)

            //Add it to the list
            providersAndInvoices = providersAndInvoices.plus(providerAndInvoices)
        }
        //Post the data
        providerAndInvoicesList.postValue(providersAndInvoices)
    }

    suspend fun getMostRecentInvoices()
    {
        var invoices: List<Invoice> = getInvoices().sortedByDescending { it.invoice_date }
        mostRecentInvoiceList.postValue(invoices)
    }
}