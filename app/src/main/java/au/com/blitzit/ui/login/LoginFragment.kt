package au.com.blitzit.ui.login

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginButton: Button
    private lateinit var usernameField: AppCompatEditText
    private lateinit var passwordField: AppCompatEditText


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        //Assign Views
        loginButton = view.findViewById(R.id.login_button)
        usernameField = view.findViewById(R.id.username_field)
        passwordField = view.findViewById(R.id.password_field)

        //set actions
        usernameField.doOnTextChanged { text, _, _, _ -> viewModel.username = text.toString() }
        passwordField.doOnTextChanged { text, _, _, _ -> viewModel.password = text.toString() }
        loginButton.setOnClickListener {

            if (usernameField.length() > 0 && passwordField.length() > 0)
            {
                //hide the keyboard
                val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                //viewModel.attemptLogin()

                //Check the credentials
                val result = viewModel.attemptLogin()

                //handle result
                if(result){
                    val action = LoginFragmentDirections.actionLoginToDashboardFragment()
                    this.findNavController().navigate(action)
                }else{
                    Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Missing Username or Password", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

}