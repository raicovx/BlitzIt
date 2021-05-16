package au.com.blitzit.ui.providers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.Provider

class ProviderDetailViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    lateinit var providerID: String
    val selectedProvider: LiveData<Provider> = liveData {
        val data = getSelectedProvider()
        emit(data)
    }

    lateinit var planID: String
    lateinit var selectedProviderName: String
    val providerInvoices: LiveData<List<Invoice>> = liveData {
        val data = getInvoicesByProvider(planID, selectedProviderName)
        emit(data)
    }

    private suspend fun getSelectedProvider(): Provider = appDatabase.providerDAO().getProviderById(providerID)

    private suspend fun getInvoicesByProvider(planId: String, providerName: String): List<Invoice> =
        appDatabase.invoiceDAO().getInvoiceListByProviderName(planId, providerName)
}