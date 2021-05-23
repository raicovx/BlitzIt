package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.ui.budget.CategoryBudgetViewModel


class ProfileFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var editProfileButton: Button

    private lateinit var viewModel: ProfileViewModel

    companion object
    {
        fun newInstance() = ProfileFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //view Model
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        backButton = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMenuFragment()) }

        editProfileButton = view.findViewById(R.id.profile_edit_button)
        editProfileButton.setOnClickListener {
            this.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment())
        }

        populateData(view)

        //LiveData
        val primaryContactEmailObserver = Observer<String>{
            if(!it.isNullOrEmpty())
                populatePrimaryContactData(it, view)
        }
        viewModel.primaryContactEmail.observe(viewLifecycleOwner, primaryContactEmailObserver)

        val supportCoordinatorEmailObserver = Observer<String>{
            if(!it.isNullOrEmpty())
                populateSupportCoordinatorData(it, view)
        }
        viewModel.supportCoordinatorEmail.observe(viewLifecycleOwner, supportCoordinatorEmailObserver)

        return view
    }

    private fun populateData(view: View)
    {
        val participantData = AuthServices.loggedParticipant
        val userData = AuthServices.loggedUser
        view.findViewById<TextView>(R.id.profile_fname).text = participantData.firstName
        view.findViewById<TextView>(R.id.profile_lname).text = participantData.lastName
        view.findViewById<TextView>(R.id.profile_dob).text = CranstekHelper.formatDate(participantData.dateOfBirth)
        view.findViewById<TextView>(R.id.profile_ndis).text = participantData.ndisNumber.toString()
        view.findViewById<TextView>(R.id.profile_contactnum).text = participantData.mobile?.let {
            CranstekHelper.formatMobileNumberText(
                it
            )
        }
        view.findViewById<TextView>(R.id.profile_email).text = userData.email
        view.findViewById<TextView>(R.id.profile_statement_email).text = participantData.statementEmails[0]
        view.findViewById<TextView>(R.id.profile_address_line1).text = participantData.addressLine
        view.findViewById<TextView>(R.id.profile_address_suburb).text = participantData.suburb
        view.findViewById<TextView>(R.id.profile_address_state).text = participantData.state
        view.findViewById<TextView>(R.id.profile_address_postcode).text = participantData.postcode.toString()
    }

    private fun populatePrimaryContactData(email: String, view: View)
    {
        view.findViewById<TextView>(R.id.profile_primary_email).text = email
    }

    private fun populateSupportCoordinatorData(email: String, view: View)
    {
        view.findViewById<TextView>(R.id.profile_support_email).text = email
    }

}