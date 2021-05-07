package au.com.blitzit.ui.registration

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthRegistration
import au.com.blitzit.auth.RegistrationState

class SignUpConfirmationFragment : Fragment()
{
    companion object{
        fun newInstance() = SignUpConfirmationFragment
    }

    private lateinit var confirmationCodeField: TextView
    private lateinit var submitButton: Button
    private lateinit var progressWheel: ProgressBar
    private lateinit var contentHolder: LinearLayout
    private lateinit var password1Field : TextView
    private lateinit var password2Field : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_signup_confirm_account, container, false)

        confirmationCodeField = view.findViewById(R.id.sign_up_confirmation_code)
        contentHolder = view.findViewById(R.id.sign_up_content)

        progressWheel = view.findViewById(R.id.sign_up_progress)
        progressWheel.isVisible = false

        password1Field = view.findViewById(R.id.sign_up_password_1)
        password2Field = view.findViewById(R.id.sign_up_password_2)

        submitButton = view.findViewById(R.id.sign_up_submit)
        submitButton.setOnClickListener {
            //hide the keyboard
            val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            submitConfirmation()
        }

        val backButton = view.findViewById<Button>(R.id.sign_up_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(SignUpConfirmationFragmentDirections.actionSignUpConfirmationFragmentToSignUpTypeFragment())
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        AuthRegistration.liveRegistrationState.observeForever { (onRegistrationStateChanged(it)) }
    }

    private fun submitConfirmation()
    {
        if(confirmationCodeField.length() == 6)
        {
            if(password1Field.length() > 0 && password2Field.length() > 0)
            {
                if(password1Field.text.toString() == password2Field.text.toString())
                {
                    toggleShowOptions(false)
                    AuthRegistration.attemptConfirmation(password1Field.text.toString(), confirmationCodeField.text.toString())
                }
                else
                    Toast.makeText(context, "Password fields must be the same.", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(context, "Password fields must not be empty.", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(context, "Code must be 6 digits.", Toast.LENGTH_SHORT).show()
    }

    private fun onRegistrationStateChanged(state: RegistrationState)
    {
        when(state){
            RegistrationState.SignUpFailed01 -> {
                toggleShowOptions(true)
                Toast.makeText(context, "Confirmation failed. Please check code and try again.", Toast.LENGTH_SHORT).show()
            }
            RegistrationState.SignedUp -> {
                this.findNavController().navigate(SignUpConfirmationFragmentDirections.actionSignUpConfirmationFragmentToLogin())
            }
        }
    }

    private fun toggleShowOptions(value: Boolean)
    {
        contentHolder.isVisible = value

        progressWheel.isVisible = !value
    }
}