package au.com.blitzit.ui.login

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.ui.budget.CategoryBudgetViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ForgotPasswordFragment : Fragment()
{
    companion object{
        fun newInstance() = ForgotPasswordFragment
    }

    private lateinit var viewModel: ForgotPasswordViewModel

    private lateinit var progressWheel: ProgressBar

    private lateinit var backButton: Button
    private lateinit var submitButton: Button

    private lateinit var emailField: TextView
    private lateinit var confirmationField: TextView
    private lateinit var passwordField: TextView
    private lateinit var confirmPasswordField: TextView

    private lateinit var signUpContentHolder: LinearLayout
    private lateinit var signUpSuccessHolder: LinearLayout
    private lateinit var signUpConfirmationHolder: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //view Model
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)

        //Main View
        val view = inflater.inflate(R.layout.fragment_forgotpassword, container, false)

        //Back Button
        backButton = view.findViewById(R.id.profile_back_button)
        backButton.setOnClickListener {
            confirmLeavingDialog()
        }

        //Progress Wheel
        progressWheel = view.findViewById(R.id.forgot_password_progress)
        progressWheel.visibility = View.GONE

        //Holders
        signUpContentHolder = view.findViewById(R.id.sign_up_content)
        signUpSuccessHolder = view.findViewById(R.id.sign_up_content_success)
        signUpSuccessHolder.visibility = View.GONE
        signUpConfirmationHolder = view.findViewById(R.id.sign_up_content_confirmation_code)
        signUpConfirmationHolder.visibility = View.GONE

        //Fields
        emailField = view.findViewById(R.id.forgot_password_email)
        confirmationField = view.findViewById(R.id.forgot_password_confirmation_code)
        passwordField = view.findViewById(R.id.forgot_password_field_1)
        confirmPasswordField = view.findViewById(R.id.forgot_password_field_2)

        //Buttons
        submitButton = view.findViewById(R.id.forgot_password_submit)
        submitButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.attemptResetPassword(emailField.text.toString())
            }
        }
        val successButton: Button = view.findViewById(R.id.forgot_password_success_button)
        successButton.setOnClickListener {
            signUpSuccessHolder.visibility = View.GONE
            signUpConfirmationHolder.visibility = View.VISIBLE
        }
        val confirmationButton: Button = view.findViewById(R.id.forgot_password_confirmation_button)
        confirmationButton.setOnClickListener {
            confirmationButtonClick()
        }

        //LiveData
        val resetPasswordStatusObserver = Observer<ResetPasswordStatus>{
            handleResetPasswordStatusChange(it)
        }
        viewModel.resetPasswordStatus.observe(viewLifecycleOwner, resetPasswordStatusObserver)

        return view
    }

    private fun confirmationButtonClick()
    {
        if(passwordField.text.isNotEmpty() && confirmPasswordField.text.isNotEmpty() && confirmationField.text.isNotEmpty())
        {
            if(passwordField.text.toString() == confirmPasswordField.text.toString())
            {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.attemptConfirmResetPassword(passwordField.text.toString(), confirmationField.text.toString())
                }
            }
            else
                Toast.makeText(requireContext(), "Password fields must match", Toast.LENGTH_SHORT).show()
        }
        else
            Toast.makeText(requireContext(), "Fields can not be empty", Toast.LENGTH_SHORT).show()
    }

    private fun handleResetPasswordStatusChange(status: ResetPasswordStatus)
    {
        when(status){
            ResetPasswordStatus.Attempting -> {
                progressWheel.visibility = View.VISIBLE
                signUpContentHolder.visibility = View.GONE
            }
            ResetPasswordStatus.EnterConfirmation -> {
                progressWheel.visibility = View.GONE
                signUpSuccessHolder.visibility = View.VISIBLE
            }
            ResetPasswordStatus.AttemptingConfirmation -> {
                progressWheel.visibility = View.VISIBLE
                signUpSuccessHolder.visibility = View.GONE
                signUpConfirmationHolder.visibility = View.GONE
            }
            ResetPasswordStatus.Success -> {
                this.findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLogin(false, true))
            }
            ResetPasswordStatus.Rejected -> { //email not found (first view)
                progressWheel.visibility = View.GONE
                signUpContentHolder.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Reset password failed, check that your email is correct", Toast.LENGTH_SHORT).show()
                viewModel.resetPasswordStatus.postValue(ResetPasswordStatus.Awaiting)
            }
            ResetPasswordStatus.ConfirmationFailed -> {
                signUpConfirmationHolder.visibility = View.VISIBLE
                progressWheel.visibility = View.GONE
                Toast.makeText(requireContext(), "Confirmation failed, check that the code is correct", Toast.LENGTH_SHORT).show()
                viewModel.resetPasswordStatus.postValue(ResetPasswordStatus.Awaiting)
            }
        }
    }

    private fun confirmLeavingDialog()
    {
        var dialog: AlertDialog

        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Are you sure?")
        builder.setMessage("Leaving this screen will require you to start again.")

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when(which){
                DialogInterface.BUTTON_NEGATIVE -> {
                    this.findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLogin())
                }
            }
        }

        builder.setPositiveButton("No", dialogClickListener)
        builder.setNegativeButton("Yes", dialogClickListener)

        dialog = builder.create()
        dialog.show()
    }
}