package au.com.blitzit.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.auth.SignInState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    init {
        lifecycleScope.launch {
            whenStarted {
                AuthServices.checkAuthSession()
            }
        }
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginButton: Button
    private lateinit var usernameField: AppCompatEditText
    private lateinit var passwordField: AppCompatEditText
    private lateinit var forgotPassword: Button
    private lateinit var progressWheel: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        //add observers
        AuthServices.liveSignInState.observeForever { onSignInStateChanged(it) }
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
                    AuthServices.attemptSignIn(usernameField.text.toString().toLowerCase(Locale.ENGLISH), passwordField.text.toString())
                }
            }
            //runBlocking { AuthServices.attemptSignIn(usernameField.text.toString().toLowerCase(Locale.ENGLISH), passwordField.text.toString()) }

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
                Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
            }
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