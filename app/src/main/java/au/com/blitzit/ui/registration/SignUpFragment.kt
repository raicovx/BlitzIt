package au.com.blitzit.ui.registration

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthRegistration
import au.com.blitzit.auth.RegistrationState
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.helper.CranstekHelper.isValidEmail
import java.text.SimpleDateFormat
import java.util.*

enum class SignUpAccountType{ PARTICIPANT, REPRESENTATIVE, COORDINATOR }

class SignUpFragment : Fragment()
{
    companion object{
        fun newInstance() = SignUpFragment
    }

    private lateinit var signUpAccountType: SignUpAccountType

    private lateinit var contentHolder: LinearLayout
    private lateinit var progressWheel: ProgressBar

    //Participant
    private lateinit var firstNameField: TextView
    private lateinit var lastNameField: TextView
    private lateinit var emailField: TextView
    private lateinit var dobField: TextView
    private lateinit var ndisField: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val backButton: Button = view.findViewById(R.id.sign_up_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpTypeFragment())
        }
        val helpButton: Button = view.findViewById(R.id.sign_up_help_button)
        helpButton.setOnClickListener {
            this.findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpHelpFragment())
        }

        contentHolder = view.findViewById(R.id.sign_up_content)
        progressWheel = view.findViewById(R.id.sign_up_progress)
        progressWheel.isVisible = false

        val args: SignUpFragmentArgs by navArgs()
        signUpAccountType = args.signuptype
        createSubView(inflater, contentHolder)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Add Observers
        AuthRegistration.liveRegistrationState.observeForever{ onRegistrationStateChanged(it) }
    }

    private fun createSubView(inflater: LayoutInflater, container: ViewGroup?)
    {
        val view: View
        when(signUpAccountType){
            SignUpAccountType.PARTICIPANT -> {
                view = inflater.inflate(R.layout.part_signup_participant, container, false)
                createParticipantView(view)
            }
            SignUpAccountType.REPRESENTATIVE -> {
                view = inflater.inflate(R.layout.part_signup_representative, container, false)
                val submitButton: Button = view.findViewById(R.id.sign_up_submit)
                submitButton.setOnClickListener { representativeSignUp() }
            }
            SignUpAccountType.COORDINATOR -> {
                view = inflater.inflate(R.layout.part_signup_coordinator, container, false)
                val submitButton: Button = view.findViewById(R.id.sign_up_submit)
                submitButton.setOnClickListener { coordinatorSignUp() }
            }
        }

        container?.addView(view)
    }

    private fun createParticipantView(view: View)
    {
        firstNameField = view.findViewById(R.id.sign_up_fname)
        lastNameField = view.findViewById(R.id.sign_up_lname)
        emailField = view.findViewById(R.id.sign_up_email)
        dobField = view.findViewById(R.id.sign_up_dob)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            handleDatePicker()
        else
            handleDatePickerLower()
        ndisField = view.findViewById(R.id.sign_up_ndis)

        val submitButton: Button = view.findViewById(R.id.sign_up_submit)
        submitButton.setOnClickListener {
            //hide the keyboard
            val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            participantSignUp()
        }
    }

    private fun participantSignUp()
    {
        if(firstNameField.length() > 0 && lastNameField.length() > 0 && dobField.length() > 0)
        {
            if(emailField.text.isValidEmail()) {
                if (ndisField.length() == 9)
                    AuthRegistration.attemptRegistration(lastNameField.text.toString(), emailField.text.toString(), dobField.text.toString(), ndisField.text.toString(), "participant")
                else
                    Toast.makeText(context, "NDIS number invalid.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(context, "Must use a valid email address.", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(context, "All fields must be filled in.", Toast.LENGTH_SHORT).show()
    }

    private fun representativeSignUp()
    {
        TODO("REP SIGN UP")
    }

    private fun coordinatorSignUp()
    {
        TODO("COORD SIGN UP")
    }

    private fun onRegistrationStateChanged(state: RegistrationState)
    {
        when(state){
            RegistrationState.Registered -> {
                //Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
                this.findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignUpSuccessFragment())
            }
            RegistrationState.Registering -> {
                toggleShowOptions(false)
            }
            RegistrationState.AlreadyRegistered -> {
                toggleShowOptions(true)
                AuthRegistration.resetRegistrationState()
                Toast.makeText(context, "Account is already registered", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleDatePicker()
    {
        dobField.isFocusable = false

        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener {
            view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat)
            dobField.text = sdf.format(cal.time)
        }

        dobField.setOnClickListener{
            DatePickerDialog(requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun handleDatePickerLower()
    {
        //Check dobfield text, attempt to format to useable type
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat)

        //try {
            
        //}
        TODO("This")
    }


    private fun toggleShowOptions(value: Boolean)
    {
        contentHolder.isVisible = value

        progressWheel.isVisible = !value
    }
}