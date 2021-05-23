package au.com.blitzit.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.auth.SignInState
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginButton: Button
    private lateinit var usernameField: AppCompatEditText
    private lateinit var passwordField: AppCompatEditText
    private lateinit var forgotPassword: Button
    private lateinit var progressWheel: ProgressBar

    private val args: LoginFragmentArgs by navArgs()

    init {
        var strings: List<String> = emptyList()
        val empty = Json.encodeToString(strings)
        Log.i("TEST", "Empty: $empty")
        strings = strings.plus("Hello")
        strings = strings.plus("World")
        val notEmpty = Json.encodeToString(strings)
        Log.i("TEST", "Not Empty: $notEmpty")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //add observers
        val signInStateObserver = Observer<SignInState>{
            onSignInStateChanged(it)
        }
        AuthServices.liveSignInState.observe(viewLifecycleOwner, signInStateObserver)

        if(!args.resetPasswordDone && !args.confirmationSuccess) {
            viewLifecycleOwner.lifecycleScope.launch {
                whenStarted {
                    AuthServices.checkAuthSession(requireContext().applicationContext)
                    //AuthServices.resetSignUpState()
                }
            }
        }

        //Assign Views
        loginButton = view.findViewById(R.id.login_button)
        usernameField = view.findViewById(R.id.username_field)
        passwordField = view.findViewById(R.id.password_field)
        forgotPassword = view.findViewById(R.id.forgot_password)
        progressWheel = view.findViewById(R.id.login_progress)
        progressWheel.isVisible = false

        loginButton.setOnClickListener {
            handleLogin(view)
        }
        forgotPassword.setOnClickListener {
            this.findNavController().navigate(LoginFragmentDirections.actionLoginToForgotPasswordFragment())
        }

        toggleShowOptions(false)
        handleArgs()

        return view
    }

    private fun handleLogin(view: View)
    {
        if (usernameField.length() > 0 && passwordField.length() > 0)
        {
            //hide the keyboard
            val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            lifecycleScope.launch {
                whenStarted {
                    AuthServices.attemptSignIn(usernameField.text.toString().toLowerCase(Locale.ENGLISH),
                        passwordField.text.toString(),
                        requireContext().applicationContext)
                }
            }

        }else{
            Toast.makeText(context, "Missing Username or Password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSignInStateChanged(state: SignInState)
    {
        when(state)
        {
            SignInState.SignedOut -> {
                toggleShowOptions(true)
            }
            SignInState.SigningIn -> {
                toggleShowOptions(false)
            }
            SignInState.SignedIn -> {
                this.findNavController().navigate(LoginFragmentDirections.actionLoginToDashboardFragment())
            }
            SignInState.SignInFailed -> {
                toggleShowOptions(true)
                Toast.makeText(context, SignInState.SignInFailed.state, Toast.LENGTH_SHORT).show()
                AuthServices.resetSignUpState()
            }
            SignInState.SignInFailedUserNotConfirmed -> {
                toggleShowOptions(true)
                Toast.makeText(context, SignInState.SignInFailedUserNotConfirmed.state, Toast.LENGTH_SHORT).show()
                AuthServices.resetSignUpState()
            }
            SignInState.SignInFailedUserNotFound -> {
                toggleShowOptions(true)
                Toast.makeText(context, SignInState.SignInFailedUserNotFound.state, Toast.LENGTH_SHORT).show()
                AuthServices.resetSignUpState()
            }
            SignInState.SignInFailedTooManyRequests -> {
                toggleShowOptions(true)
                Toast.makeText(context, SignInState.SignInFailedTooManyRequests.state, Toast.LENGTH_SHORT).show()
                AuthServices.resetSignUpState()
            }
            SignInState.SignInFailedIncorrect -> {
                toggleShowOptions(true)
                Toast.makeText(context, SignInState.SignInFailedIncorrect.state, Toast.LENGTH_SHORT).show()
                AuthServices.resetSignUpState()
            }
        }
    }

    private fun handleArgs()
    {
        if(args.confirmationSuccess)
        {
            Toast.makeText(context, "Account Confirmation Successful", Toast.LENGTH_SHORT).show()
        }
        if(args.resetPasswordDone)
        {
            Toast.makeText(context, "Password Reset Successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleShowOptions(value: Boolean)
    {
        usernameField.isVisible = value
        passwordField.isVisible = value
        forgotPassword.isVisible = value
        loginButton.isVisible = value

        progressWheel.isVisible = !value
    }
}