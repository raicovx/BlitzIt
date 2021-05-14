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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.ProviderOverview
import au.com.blitzit.roomdata.Provider
import au.com.blitzit.ui.dashboard.DashboardViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyProvidersFragment : Fragment()
{
    companion object{
        fun newInstance() = MyProvidersFragment()
    }

    private lateinit var viewModel: MyProvidersViewModel
    private lateinit var providersContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //viewModel
        viewModel = ViewModelProvider(this).get(MyProvidersViewModel::class.java)

        //View
        val view = inflater.inflate(R.layout.fragment_providers, container, false)

        //Back Button
        val backButton: Button = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(MyProvidersFragmentDirections.actionMyProvidersFragmentToMenuFragment())
        }

        //Container
        providersContainer = view.findViewById(R.id.providers_container)

        //LiveData
        val providerObserver = Observer<List<Provider>>{
            populateProviders(it, layoutInflater, providersContainer)
        }
        viewModel.providersList.observe(viewLifecycleOwner, providerObserver)

        return view
    }

    private fun populateProviders(providers: List<Provider>, inflater: LayoutInflater, container: ViewGroup)
    {
        for(provider: Provider in providers)
        {
            //Create the view and inflate it
            val view = inflater.inflate(R.layout.part_provider_display, container, false)

            //NAME
            view.findViewById<TextView>(R.id.provider_title).text = provider.name
            //ABN
            view.findViewById<TextView>(R.id.provider_abn).text = provider.getABN()
            //ADDRESS - street
            view.findViewById<TextView>(R.id.provider_address_field_street).text = provider.getStreetAddress()
            //ADDRESS - Suburb, State, Postcode
            view.findViewById<TextView>(R.id.provider_address_field_suburb).text = provider.getSuburbStatePostcode()
            //EMAIL
            view.findViewById<TextView>(R.id.provider_contact_email).text = provider.getEmailAddress()
            //PHONE
            view.findViewById<TextView>(R.id.provider_contact_number).text = provider.getContactNumber()

            //Selectable
            val selectableButton: MaterialCardView = view.findViewById(R.id.provider_display_button)
            selectableButton.setOnClickListener {
                this.findNavController().navigate(
                    MyProvidersFragmentDirections.actionMyProvidersFragmentToProviderDetailFragment(
                        provider.provider_id
                    )
                )
            }

            container.addView(view)
        }
    }
}