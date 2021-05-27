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
import au.com.blitzit.data.ProviderInvoicesDisplayHandler
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

    private var numberOfInvoicesToDisplay: Int = 20
    private var numberOfInvoicesDisplayingMostRecent: Int = 0
    private var numberOfMostRecentInvoices: Int = 0

    private var providerInvoicesDisplayHandlers: List<ProviderInvoicesDisplayHandler> = emptyList()

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
            numberOfMostRecentInvoices = it.count()
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

            providerHolder.addView(view)

            val invoicesHolder: LinearLayout = view.findViewById(R.id.invoices_invoice_holder)

            val displayHandler = ProviderInvoicesDisplayHandler(providerInvoices, 0, invoicesHolder)
            providerInvoicesDisplayHandlers = providerInvoicesDisplayHandlers.plus(displayHandler)

            //Ensure we don't try to populate more invoices then we have
            val numberToDisplay = if(providerInvoices.invoices.count() < 20) providerInvoices.invoices.count() else numberOfInvoicesToDisplay
            displayHandler.numberOfDisplayingInvoices = numberToDisplay

            //populate invoices
            populateInvoiceRange(0, numberToDisplay - 1, providerInvoices.invoices, invoicesHolder)

            //Draw load more button
            if(numberToDisplay == 20)
            {
                val buttonView = layoutInflater.inflate(R.layout.part_invoice_load_more, invoicesHolder, false)
                val loadMoreButton = buttonView.findViewById<Button>(R.id.invoices_load_more)
                loadMoreButton.setOnClickListener {
                    handleLoadMoreByProvider(displayHandler)
                    invoicesHolder.removeView(buttonView)
                }
                invoicesHolder.addView(buttonView)
            }
        }
    }

    private fun setupMostRecentDisplay(sortedInvoices: List<Invoice>)
    {
        val view = layoutInflater.inflate(R.layout.part_invoice_most_recent, mostRecentContainer, false)

        mostRecentContainer.addView(view)

        //Ensure we don't try to populate more invoices then we have
        val numberToDisplay = if(sortedInvoices.count() < 20) sortedInvoices.count() else numberOfInvoicesToDisplay
        numberOfInvoicesDisplayingMostRecent = numberToDisplay

        populateInvoiceRange(0, numberToDisplay - 1, sortedInvoices, mostRecentContainer)

        //Draw load more button
        if(numberToDisplay == 20)
        {
            val buttonView = layoutInflater.inflate(R.layout.part_invoice_load_more, mostRecentContainer, false)
            val loadMoreButton = buttonView.findViewById<Button>(R.id.invoices_load_more)
            loadMoreButton.setOnClickListener {
                handleLoadMoreMostRecent()
                mostRecentContainer.removeView(buttonView)
            }
            mostRecentContainer.addView(buttonView)
        }
    }

    private fun populateInvoiceRange(rangeStart: Int, rangeEnd: Int, invoices: List<Invoice>, container: ViewGroup)
    {
        for(i in rangeStart..rangeEnd)
        {
            createDivider(container)
            createInvoice(invoices[i], container)
        }
    }

    private fun createDivider(container: ViewGroup)
    {
        val dividerView =
            layoutInflater.inflate(R.layout.part_invoice_divider, container, false)
        container.addView(dividerView)
    }

    private fun createInvoice(invoice: Invoice, container: ViewGroup)
    {
        val view = layoutInflater.inflate(R.layout.part_invoice, container, false)
        view.findViewById<TextView>(R.id.invoice_part_amount).text =
            CranstekHelper.convertToCurrency(invoice.amount)
        view.findViewById<TextView>(R.id.invoice_part_id).text = invoice.invoice_id

        //Invoice detail button
        val invoiceButton: LinearLayout = view.findViewById(R.id.invoice_selector_button)
        invoiceButton.setOnClickListener {
            this.findNavController().navigate(
                InvoicesFragmentDirections.actionInvoicesFragmentToInvoiceDetailFragment(
                    invoice.invoice_id
                )
            )
        }

        container.addView(view)
    }

    private fun handleLoadMoreMostRecent()
    {
        //Figure out how many more to display
        val invoicesToLoad = if((numberOfMostRecentInvoices - numberOfInvoicesDisplayingMostRecent) > 20) 20
        else numberOfMostRecentInvoices - numberOfInvoicesDisplayingMostRecent

        populateInvoiceRange(numberOfInvoicesDisplayingMostRecent + 1, numberOfInvoicesDisplayingMostRecent + invoicesToLoad - 1,
            viewModel.mostRecentInvoiceList.value!!, mostRecentContainer)

        numberOfInvoicesDisplayingMostRecent += invoicesToLoad

        //Draw load more button
        if(invoicesToLoad == 20)
        {
            val buttonView = layoutInflater.inflate(R.layout.part_invoice_load_more, mostRecentContainer, false)
            val loadMoreButton = buttonView.findViewById<Button>(R.id.invoices_load_more)
            loadMoreButton.setOnClickListener {
                handleLoadMoreMostRecent()
                mostRecentContainer.removeView(buttonView)
            }
            mostRecentContainer.addView(buttonView)
        }
    }

    private fun handleLoadMoreByProvider(displayHandler: ProviderInvoicesDisplayHandler)
    {
        //Figure out how many more to display
        val invoicesToLoad = if((displayHandler.providerAndInvoices.invoices.count() - displayHandler.numberOfDisplayingInvoices) > 20) 20
        else numberOfMostRecentInvoices - numberOfInvoicesDisplayingMostRecent

        //Populate the invoices
        populateInvoiceRange(displayHandler.numberOfDisplayingInvoices + 1, displayHandler.numberOfDisplayingInvoices + invoicesToLoad - 1,
            displayHandler.providerAndInvoices.invoices, displayHandler.container)

        //Update the display total
        displayHandler.numberOfDisplayingInvoices += invoicesToLoad

        //Draw load more button
        if(invoicesToLoad == 20)
        {
            val buttonView = layoutInflater.inflate(R.layout.part_invoice_load_more, mostRecentContainer, false)
            val loadMoreButton = buttonView.findViewById<Button>(R.id.invoices_load_more)
            loadMoreButton.setOnClickListener {
                handleLoadMoreMostRecent()
                mostRecentContainer.removeView(buttonView)
            }
            mostRecentContainer.addView(buttonView)
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