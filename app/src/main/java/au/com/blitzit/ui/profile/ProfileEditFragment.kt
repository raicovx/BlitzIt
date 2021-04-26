package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import org.w3c.dom.Text

class ProfileEditFragment : Fragment(), AdapterView.OnItemSelectedListener
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = ProfileEditFragment
    }

    private lateinit var contactNumberField: TextView
    private lateinit var userEmailField: TextView
    private lateinit var statementEmailField: TextView
    private lateinit var contactMethodSpinner: Spinner
    private lateinit var notesField: TextView

    private var selectedContactMethod: String = "Email"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        backButton = view.findViewById(R.id.profile_edit_back_button)
        backButton.setOnClickListener { this.findNavController().navigate(ProfileEditFragmentDirections.actionProfileEditFragmentToProfileFragment()) }

        prePopulateDataFields(view)

        //TODO("Hook up submit button")

        return view
    }

    private fun prePopulateDataFields(view: View)
    {
        val userData = AuthServices.userData

        contactNumberField = view.findViewById(R.id.profile_edit_contact)
        contactNumberField.text = CranstekHelper.formatMobileNumberText(userData.mobile)

        userEmailField = view.findViewById(R.id.profile_edit_email)
        userEmailField.text = userData.email

        statementEmailField = view.findViewById(R.id.profile_edit_statement_email)
        statementEmailField.text = userData.statementEmail[0]

        notesField = view.findViewById(R.id.profile_edit_notes)

        //Setup Spinner
        contactMethodSpinner = view.findViewById(R.id.profile_edit_contact_spinner)
        val elements: Array<String> = resources.getStringArray(R.array.contact_methods)
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_profile_contact_item, elements)
        dataAdapter.setDropDownViewResource(R.layout.spinner_profile_contact_dropdown)
        contactMethodSpinner.adapter = dataAdapter
        contactMethodSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        when(position)
        {
            0 -> selectedContactMethod = "Email"
            1 -> selectedContactMethod = "Phone"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}