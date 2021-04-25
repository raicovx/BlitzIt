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
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.ProviderOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyProvidersFragment : Fragment()
{
    companion object{
        fun newInstance() = MyProvidersFragment()
    }

    private lateinit var providersContainer: LinearLayout
    private lateinit var loadingBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_providers, container, false)

        val backButton: Button = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(MyProvidersFragmentDirections.actionMyProvidersFragmentToMenuFragment())
        }

        providersContainer = view.findViewById(R.id.providers_container)

        for (providerOverview: ProviderOverview in AuthServices.userData.getSelectedPlan().planProviderSummary!!.providerOverview)
        {
            populateProviders(providerOverview, layoutInflater, providersContainer)
        }

        return view
    }

    private fun populateProviders(providerOverview: ProviderOverview, inflater: LayoutInflater, container: ViewGroup)
    {
        val view = inflater.inflate(R.layout.part_provider_display, container, false)

        view.findViewById<TextView>(R.id.provider_title).text = providerOverview.provider.name

        //ABN
        view.findViewById<TextView>(R.id.provider_abn).text = providerOverview.provider.getABN()
        //ADDRESS - street
        view.findViewById<TextView>(R.id.provider_address_field_street).text = providerOverview.provider.getStreetAddress()
        //ADDRESS - Suburb, State, Postcode
        view.findViewById<TextView>(R.id.provider_address_field_suburb).text = providerOverview.provider.getSuburbStatePostcode()
        //EMAIL
        view.findViewById<TextView>(R.id.provider_contact_email).text = providerOverview.provider.getEmailAddress()
        //PHONE
        view.findViewById<TextView>(R.id.provider_contact_number).text = providerOverview.provider.getContactNumber()

        container.addView(view)
    }
}