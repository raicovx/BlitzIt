package au.com.blitzit.ui.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.ProviderInvoices
import au.com.blitzit.data.UserInvoice
import au.com.blitzit.helper.CranstekHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoicesFragment: Fragment(), AdapterView.OnItemSelectedListener
{
    companion object{
        fun newInstance() = InvoicesFragment
    }

    init {
        lifecycleScope.launch {
            whenStarted {
                filterSpinner.isEnabled = false
                withContext(Dispatchers.IO)
                {
                    providerInvoices = AuthServices.userData.getSelectedPlan().getInvoicesByProvider()
                    mostRecentInvoices = AuthServices.userData.getSelectedPlan().getInvoicesByMostRecent()
                }

                if(!providerInvoices.isNullOrEmpty())
                {
                    filterSpinner.isEnabled = true
                    progressWheel.isVisible = false
                    displayByProvider(layoutInflater, providerHolder)
                }
            }
        }
    }

    private lateinit var backButton: Button
    private lateinit var filterSpinner: Spinner
    private lateinit var progressWheel: ProgressBar

    private lateinit var providerHolder: LinearLayout
    private lateinit var mostRecentContainer: LinearLayout

    private var providerInvoices: List<ProviderInvoices> = emptyList()
    private var mostRecentInvoices: List<UserInvoice> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_invoices, container, false)

        progressWheel = view.findViewById(R.id.loading_progress)

        backButton = view.findViewById(R.id.invoices_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(InvoicesFragmentDirections.actionInvoicesFragmentToMenuFragment())
        }

        filterSpinner = view.findViewById(R.id.invoices_filter_spinner)
        setupSpinner()

        providerHolder = view.findViewById(R.id.invoices_providers)
        mostRecentContainer = view.findViewById(R.id.invoices_most_recent)

        return view
    }

    private fun setupSpinner()
    {
        //Create the ArrayAdapter using the string resource
        val elements: Array<String> = resources.getStringArray(R.array.filter_array)
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.filter_spinner_item, elements)
        dataAdapter.setDropDownViewResource(R.layout.filter_spinner_dropdown_item)
        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = this
    }

    private fun displayByProvider(inflater: LayoutInflater, container: ViewGroup)
    {
        container.isVisible = true
        mostRecentContainer.isVisible = false

        //Create a new view for each provider invoice
        for(provider: ProviderInvoices in providerInvoices)
        {
            val view = inflater.inflate(R.layout.part_invoice_provider_holder, container, false)
            //Set the title
            view.findViewById<TextView>(R.id.invoice_provider_title).text = provider.provider

            //populate with invoices
            val invoiceContainer: LinearLayout = view.findViewById(R.id.invoices_invoice_holder)
            populateInvoices(inflater, invoiceContainer, provider.invoices!!)

            container.addView(view)
        }
    }

    private fun populateInvoices(inflater: LayoutInflater, container: ViewGroup, invoices: List<UserInvoice>)
    {
        for(invoice: UserInvoice in invoices)
        {
            val view = inflater.inflate(R.layout.part_invoice, container, false)
            view.findViewById<TextView>(R.id.invoice_part_amount).text = CranstekHelper.convertToCurrency(invoice.amount)
            view.findViewById<TextView>(R.id.invoice_part_id).text = invoice.invoice_id
            container.addView(view)
        }
    }

    private fun displayByMostRecent(inflater: LayoutInflater, container: ViewGroup)
    {
        container.isVisible = true
        providerHolder.isVisible = false

        val view = inflater.inflate(R.layout.part_invoice_most_recent, container, false)

        val invoiceContainer: LinearLayout = view.findViewById(R.id.invoices_most_recent_holder)
        populateInvoices(inflater, invoiceContainer, mostRecentInvoices)

        container.addView(view)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        if(position == 0)
        {
            displayByProvider(layoutInflater, providerHolder)
        }
        if(position == 1)
        {
            displayByMostRecent(layoutInflater, mostRecentContainer)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}