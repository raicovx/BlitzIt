package au.com.blitzit.ui.providers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Invoice
import au.com.blitzit.ui.invoices.InvoicesFragmentDirections

class ProviderDetailFragment: Fragment()
{
    companion object{
        fun newInstance() = ProviderDetailFragment()
    }

    private val args: ProviderDetailFragmentArgs by navArgs()
    private lateinit var viewModel: ProviderDetailViewModel

    private lateinit var loadingBar: ProgressBar
    private lateinit var invoiceContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //view Model
        viewModel = ViewModelProvider(this).get(ProviderDetailViewModel::class.java)
        viewModel.providerID = args.providerId
        viewModel.planID = AuthServices.selectedPlan.plan_id

        //view
        val view = inflater.inflate(R.layout.fragment_providers_detail, container, false)

        //Back Button
        val backButton: Button = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ProviderDetailFragmentDirections.actionProviderDetailFragmentToMyProvidersFragment())
        }

        //Loading Bar
        loadingBar = view.findViewById(R.id.loading_progress)

        //Invoice container
        invoiceContainer = view.findViewById(R.id.provider_detail_invoices_holder)
        invoiceContainer.isVisible = false

        val providerTitle: TextView = view.findViewById(R.id.provider_detail_name)
        CranstekHelper.handleCategoryTextViewColours(requireContext(), providerTitle)

        //Provider Details
        val invoiceObserver = Observer<List<Invoice>> {
            populateInvoices(it, inflater, invoiceContainer)
            loadingBar.isVisible = false
            invoiceContainer.isVisible = true
        }
        val providerObserver = Observer<au.com.blitzit.roomdata.Provider>{
            populateProviderDetails(it, view)
            viewModel.selectedProviderName = it.name

            //observer the next livedata
            viewModel.providerInvoices.observe(viewLifecycleOwner, invoiceObserver)
        }
        viewModel.selectedProvider.observe(viewLifecycleOwner, providerObserver)

        return view
    }

    private fun populateProviderDetails(provider: au.com.blitzit.roomdata.Provider, view: View)
    {
        view.findViewById<TextView>(R.id.provider_detail_name).text = provider.name

        //ABN
        val abnTV: TextView = view.findViewById(R.id.provider_abn_field)
        abnTV.text = provider.getABN()

        view.findViewById<TextView>(R.id.provider_address_field_street).text = provider.getStreetAddress()
        view.findViewById<TextView>(R.id.provider_address_field_suburb).text = provider.getSuburbStatePostcode()

        //Contact Number
        val numberTV: TextView = view.findViewById(R.id.provider_contact_number_field)
        numberTV.text = provider.getContactNumber(false)
    }

    private fun populateInvoices(invoices: List<Invoice>, inflater: LayoutInflater, container: ViewGroup)
    {
        for(invoice: Invoice in invoices)
        {
            val dividerView = inflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = inflater.inflate(R.layout.part_invoice, container, false)
            view.findViewById<TextView>(R.id.invoice_part_amount).text = CranstekHelper.convertToCurrency(invoice.amount)
            view.findViewById<TextView>(R.id.invoice_part_id).text = invoice.invoice_id
            container.addView(view)

            //Invoice detail button
            val invoiceButton: LinearLayout = view.findViewById(R.id.invoice_selector_button)
            invoiceButton.setOnClickListener {
                this.findNavController().navigate(ProviderDetailFragmentDirections.actionProviderDetailFragmentToInvoiceDetailFragment(invoice.invoice_id))
            }
        }
    }
}