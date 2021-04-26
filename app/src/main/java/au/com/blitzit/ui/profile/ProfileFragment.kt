package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper


class ProfileFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var editProfileButton: Button

    companion object
    {
        fun newInstance() = ProfileFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        backButton = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMenuFragment()) }

        editProfileButton = view.findViewById(R.id.profile_edit_button)
        editProfileButton.setOnClickListener {
            this.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment())
        }

        populateData(view)

        return view
    }

    private fun populateData(view: View)
    {
        val userData = AuthServices.userData
        view.findViewById<TextView>(R.id.profile_fname).text = userData.firstName
        view.findViewById<TextView>(R.id.profile_lname).text = userData.lastName
        view.findViewById<TextView>(R.id.profile_dob).text = CranstekHelper.formatDate(userData.dateOfBirth)
        view.findViewById<TextView>(R.id.profile_ndis).text = userData.ndisNumber.toString()
        view.findViewById<TextView>(R.id.profile_contactnum).text = CranstekHelper.formatMobileNumberText(userData.mobile)
        view.findViewById<TextView>(R.id.profile_email).text = userData.email
        view.findViewById<TextView>(R.id.profile_statement_email).text = userData.statementEmail[0]
        view.findViewById<TextView>(R.id.profile_primary_email).text = userData.primaryContacts[0].email
        view.findViewById<TextView>(R.id.profile_address_line1).text = userData.addressLine
        view.findViewById<TextView>(R.id.profile_address_line2).text = "..."
        view.findViewById<TextView>(R.id.profile_address_suburb).text = userData.suburb
        view.findViewById<TextView>(R.id.profile_address_state).text = userData.state
        view.findViewById<TextView>(R.id.profile_address_postcode).text = userData.postcode.toString()
        view.findViewById<TextView>(R.id.profile_support_email).text = userData.supportCoordinators[0].email
    }

}