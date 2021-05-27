package au.com.blitzit.ui.invoices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import au.com.blitzit.AppDatabase
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.roomdata.LineItem

class InvoiceDetailViewModel(app: Application): AndroidViewModel(app)
{
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(app.applicationContext)

    lateinit var selectedInvoiceId: String

    val selectedInvoice = MutableLiveData<Invoice>()
    val selectedInvoiceLineItems = MutableLiveData<List<LineItem>>()

    suspend fun downloadLineItems(selectedInvoiceId: String)
    {
        AuthServices.launchGetInvoiceLineItems(selectedInvoiceId, AuthServices.selectedPlan.plan_id)
    }

    suspend fun getLineItems()
    {
        selectedInvoiceLineItems.postValue(getSelectedInvoiceLineItems())
    }

    suspend fun getSelectedInvoice()
    {
        selectedInvoice.postValue(getSelectedInvoiceFromDB())
    }


    private suspend fun getSelectedInvoiceFromDB(): Invoice = appDatabase.invoiceDAO().getInvoiceById(
        selectedInvoiceId, AuthServices.selectedPlan.plan_id, AuthServices.loggedParticipant.ndisNumber)

    private suspend fun getSelectedInvoiceLineItems(): List<LineItem> = appDatabase.lineItemDAO().getLineItemsByInvoice(selectedInvoiceId)
}