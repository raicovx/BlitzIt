package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.auth.EditSubmissionState
import au.com.blitzit.data.EditProfileDataObject
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.helper.CranstekHelper.isValidEmail
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment(), AdapterView.OnItemSelectedListener
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = ProfileEditFragment
    }

    private lateinit var contentHolder: LinearLayout
    private lateinit var submitButton: Button
    private lateinit var loadingBar: ProgressBar
    private lateinit var descriptionHolder: LinearLayout

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

        //Set default State
        AuthServices.editSubmissionState.postValue(EditSubmissionState.Awaiting)

        //Live Data
        val postStateObserver = Observer<EditSubmissionState>{
            onSubmissionStateChange(it)
        }
        AuthServices.editSubmissionState.observe(viewLifecycleOwner, postStateObserver)

        return view
    }

    private fun prePopulateDataFields(view: View)
    {
        val participantData = AuthServices.loggedParticipant
        val userData = AuthServices.loggedUser

        contactNumberField = view.findViewById(R.id.profile_edit_contact)
        contactNumberField.text = participantData.mobile?.let {
            CranstekHelper.formatMobileNumberText(
                it
            )
        }

        userEmailField = view.findViewById(R.id.profile_edit_email)
        userEmailField.text = userData.email

        statementEmailField = view.findViewById(R.id.profile_edit_statement_email)
        statementEmailField.text = participantData.statementEmails[0]

        notesField = view.findViewById(R.id.profile_edit_notes)

        //Setup Spinner
        contactMethodSpinner = view.findViewById(R.id.profile_edit_contact_spinner)
        val elements: Array<String> = resources.getStringArray(R.array.contact_methods)
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_profile_contact_item, elements)
        dataAdapter.setDropDownViewResource(R.layout.spinner_profile_contact_dropdown)
        contactMethodSpinner.adapter = dataAdapter
        contactMethodSpinner.onItemSelectedListener = this

        //Get Fields
        loadingBar = view.findViewById(R.id.profile_edit_loading_bar)
        descriptionHolder = view.findViewById(R.id.profile_edit_description_holder)
        contentHolder = view.findViewById(R.id.profile_edit_content_holder)
        submitButton = view.findViewById(R.id.profile_edit_submit)
        submitButton.setOnClickListener {
            if(allFieldsCorrect())
                submitData()
            else
                Toast.makeText(requireContext(), "All fields must be filled in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitData()
    {
        //get data object
        val dataObject = EditProfileDataObject(
            contactNumberField.text.toString(),
            userEmailField.text.toString(),
            statementEmailField.text.toString(),
            selectedContactMethod,
            notesField.text.toString()).toJsonObject()

        //Send
        viewLifecycleOwner.lifecycleScope.launch {
            AuthServices.editProfileSubmission(dataObject)
        }
    }

    private fun onSubmissionStateChange(state: EditSubmissionState)
    {
        when(state){
            EditSubmissionState.Submitting -> {
                contentHolder.visibility = View.GONE
                submitButton.visibility = View.GONE
                descriptionHolder.visibility = View.GONE
                loadingBar.visibility = View.VISIBLE
                backButton.visibility = View.GONE
            }
            EditSubmissionState.Success -> {
                this.findNavController().navigate(ProfileEditFragmentDirections.actionProfileEditFragmentToProfileEditSuccessFragment())
            }
            EditSubmissionState.Failure -> {
                contentHolder.visibility = View.VISIBLE
                submitButton.visibility = View.VISIBLE
                descriptionHolder.visibility = View.VISIBLE
                loadingBar.visibility = View.GONE
                backButton.visibility = View.VISIBLE
                AuthServices.editSubmissionState.postValue(EditSubmissionState.Awaiting)
            }
        }
    }

    private fun allFieldsCorrect(): Boolean
    {
        return contactNumberField.text.isNotEmpty() &&
            userEmailField.text.isValidEmail() &&
            statementEmailField.text.isValidEmail() &&
            notesField.text.isNotEmpty()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        when(position)
        {
            0 -> selectedContactMethod = "Email"
            1 -> selectedContactMethod = "Phone"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}