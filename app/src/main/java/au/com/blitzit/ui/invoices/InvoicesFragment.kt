package au.com.blitzit.ui.invoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.data.ProviderAndInvoices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Invoice
import kotlinx.coroutines.launch

class InvoicesFragment: Fragment(), AdapterView.OnItemSelectedListener
{
    companion object{
        fun newInstance() = InvoicesFragment
    }

    init
    {
        lifecycleScope.launch {
            whenStarted {
                filterSpinner.isEnabled = false
                viewModel.getProvidersAndInvoices()
                viewModel.getMostRecentInvoices()
            }
        }
    }

    private lateinit var viewModel: InvoicesViewModel

    private lateinit var backButton: Button
    private lateinit var filterSpinner: Spinner
    private lateinit var progressWheel: ProgressBar

    private lateinit var providerHolder: LinearLayout
    private lateinit var mostRecentContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //view model
        viewModel = ViewModelProvider(this).get(InvoicesViewModel::class.java)

        //View
        val view = inflater.inflate(R.layout.fragment_invoices, container, false)

        //Un-Hide the FAB
        val mActivity : MainActivity = activity as MainActivity
        mActivity.showFAB()

        //Loading bar
        progressWheel = view.findViewById(R.id.loading_progress)

        //Back Button
        backButton = view.findViewById(R.id.invoices_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(InvoicesFragmentDirections.actionInvoicesFragmentToMenuFragment())
        }

        //Filter Spinner
        filterSpinner = view.findViewById(R.id.invoices_filter_spinner)
        setupSpinner()

        //Holders
        providerHolder = view.findViewById(R.id.invoices_providers)
        providerHolder.isVisible = false
        mostRecentContainer = view.findViewById(R.id.invoices_most_recent)
        mostRecentContainer.isVisible = false

        //Colour changes
        val filterTitle: TextView = view.findViewById(R.id.invoices_filter_title)
        CranstekHelper.handleCategoryTextViewColours(requireContext(), filterTitle)

        //LiveData
        val providerInvoicesObserver = Observer<List<ProviderAndInvoices>> {
            setupProviderDisplay(it)
            filterSpinner.isEnabled = true
            progressWheel.isVisible = false
            providerHolder.isVisible = true
        }
        viewModel.providerAndInvoicesList.observe(viewLifecycleOwner, providerInvoicesObserver)
        val mostRecentInvoiceObserver = Observer<List<Invoice>> {
            setupMostRecentDisplay(it)
        }
        viewModel.mostRecentInvoiceList.observe(viewLifecycleOwner, mostRecentInvoiceObserver)

        return view
    }

    private fun setupProviderDisplay(providerAndInvoices: List<ProviderAndInvoices>)
    {
        //Create a new view for each provider invoice
        for(providerInvoices: ProviderAndInvoices in providerAndInvoices)
        {
            val view = layoutInflater.inflate(R.layout.part_invoice_provider_holder, providerHolder, false)
            //Set the title
            view.findViewById<TextView>(R.id.invoice_provider_title).text = providerInvoices.provider.name

            //handle colour changes
            val header: LinearLayout = view.findViewById(R.id.invoice_provider_title_header)
            CranstekHelper.handleHeaderColours(requireContext(), header)

            //populate with invoices
            val invoiceContainer: LinearLayout = view.findViewById(R.id.invoices_invoice_holder)
            populateInvoices(invoiceContainer, providerInvoices.invoices)

            providerHolder.addView(view)
        }
    }

    private fun setupMostRecentDisplay(sortedInvoices: List<Invoice>)
    {
        val view = layoutInflater.inflate(R.layout.part_invoice_most_recent, mostRecentContainer, false)

        val invoiceContainer: LinearLayout = view.findViewById(R.id.invoices_most_recent_holder)
        populateInvoices(invoiceContainer, sortedInvoices)

        mostRecentContainer.addView(view)
    }

    private fun populateInvoices(container: ViewGroup, invoices: List<Invoice>)
    {
        for(invoice: Invoice in invoices)
        {
            val dividerView = layoutInflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = layoutInflater.inflate(R.layout.part_invoice, container, false)
            view.findViewById<TextView>(R.id.invoice_part_amount).text = CranstekHelper.convertToCurrency(invoice.amount)
            view.findViewById<TextView>(R.id.invoice_part_id).text = invoice.invoice_id

            //Invoice detail button
            val invoiceButton: LinearLayout = view.findViewById(R.id.invoice_selector_button)
            invoiceButton.setOnClickListener {
                this.findNavController().navigate(InvoicesFragmentDirections.actionInvoicesFragmentToInvoiceDetailFragment(invoice.invoice_id))
            }

            container.addView(view)
        }
    }

    private fun setupSpinner()
    {
        //Create the ArrayAdapter using the string resource
        val elements: Array<String> = resources.getStringArray(R.array.filter_array)
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_invoice_filter_item, elements)
        dataAdapter.setDropDownViewResource(R.layout.spinner_invoice_filter_dropdown_item)
        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        if(position == 0)
        {
            providerHolder.isVisible = true
            mostRecentContainer.isVisible = false
        }
        if(position == 1)
        {
            providerHolder.isVisible = false
            mostRecentContainer.isVisible = true
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}