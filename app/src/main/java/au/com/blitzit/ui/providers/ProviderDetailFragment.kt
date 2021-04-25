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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.UserInvoice
import au.com.blitzit.helper.CranstekHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.Provider

class ProviderDetailFragment: Fragment()
{
    companion object{
        fun newInstance() = ProviderDetailFragment()
    }

    init {
        lifecycleScope.launch{
            whenStarted {
                withContext(Dispatchers.IO)
                {
                    providerInvoices = AuthServices.userData.getSelectedPlan().getInvoicesBySpecificProvider(provider.name)
                }

                populateInvoices(providerInvoices, layoutInflater, invoiceContainer)
                loadingBar.isVisible = false
                invoiceContainer.isVisible = true
            }
        }
    }

    private val args: ProviderDetailFragmentArgs by navArgs()
    private lateinit var loadingBar: ProgressBar
    private lateinit var provider: au.com.blitzit.data.Provider
    private var providerInvoices: List<UserInvoice> = emptyList()
    private lateinit var invoiceContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_providers_detail, container, false)

        val backButton: Button = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ProviderDetailFragmentDirections.actionProviderDetailFragmentToMyProvidersFragment())
        }

        loadingBar = view.findViewById(R.id.loading_progress)

        provider = AuthServices.userData.getSelectedPlan().planProviderSummary!!.providerOverview[args.providerOverviewIndex].provider
        invoiceContainer = view.findViewById(R.id.provider_detail_invoices_holder)
        invoiceContainer.isVisible = false

        populateProviderDetails(provider, view)

        return view
    }

    private fun populateProviderDetails(provider: au.com.blitzit.data.Provider, view: View)
    {
        view.findViewById<TextView>(R.id.provider_detail_name).text = provider.name

        //ABN
        val abnTV: TextView = view.findViewById(R.id.provider_abn_field)
        if(provider.abn.isNullOrEmpty())
            abnTV.text = "N/A"
        else
            abnTV.text = provider.abn

        view.findViewById<TextView>(R.id.provider_address_field_street).text = provider.getStreetAddress()
        view.findViewById<TextView>(R.id.provider_address_field_suburb).text = provider.getSuburbStatePostcode()

        //Contact Number
        val numberTV: TextView = view.findViewById(R.id.provider_contact_number_field)
        if(provider.phone.isNullOrEmpty())
            numberTV.text = "N/A"
        else
            numberTV.text = provider.phone[0]
    }

    private fun populateInvoices(invoices: List<UserInvoice>, inflater: LayoutInflater, container: ViewGroup)
    {
        for(invoice: UserInvoice in invoices)
        {
            val dividerView = inflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = inflater.inflate(R.layout.part_invoice, container, false)
            view.findViewById<TextView>(R.id.invoice_part_amount).text = CranstekHelper.convertToCurrency(invoice.amount)
            view.findViewById<TextView>(R.id.invoice_part_id).text = invoice.invoice_id
            container.addView(view)
        }
    }
}