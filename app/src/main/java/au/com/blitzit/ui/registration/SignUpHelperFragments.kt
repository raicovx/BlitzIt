package au.com.blitzit.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import au.com.blitzit.AppDatabase
import au.com.blitzit.R
import au.com.blitzit.auth.AuthRegistration
import au.com.blitzit.roomdata.SignUpRequest
import kotlinx.coroutines.launch

class SignUpTypeFragment : Fragment()
{
    companion object{
        fun newInstance() = SignUpTypeFragment
    }

    init {
        lifecycleScope.launch {
            whenStarted {
                AuthRegistration.getSignUpRequest()
            }
        }
    }

    private lateinit var backButton: Button
    private lateinit var helpButton: Button

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        AuthRegistration.appDatabase = AppDatabase.getDatabase(requireContext().applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_signup_type, container, false)

        backButton = view.findViewById(R.id.sign_up_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToIntro())
        }

        helpButton = view.findViewById(R.id.sign_up_help_button)
        helpButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpHelpFragment())
        }

        val participantSignUpButton: Button = view.findViewById(R.id.sign_up_type_1)
        participantSignUpButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpFragment(SignUpAccountType.PARTICIPANT))
        }

        val repSignUpButton: Button = view.findViewById(R.id.sign_up_type_2)
        repSignUpButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpFragment(SignUpAccountType.REPRESENTATIVE))
        }
        repSignUpButton.isVisible = false

        val coordinatorSignUpButton: Button = view.findViewById(R.id.sign_up_type_3)
        coordinatorSignUpButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpFragment(SignUpAccountType.COORDINATOR))
        }
        coordinatorSignUpButton.isVisible = false

        val confirmationButton: Button = view.findViewById(R.id.sign_up_confirmation)
        confirmationButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpConfirmationFragment())
        }
        confirmationButton.isVisible = false

        val signUpObserver = Observer<SignUpRequest>{
            if(it != null)
                confirmationButton.isVisible = true
        }
        AuthRegistration.signUpRequest.observe(viewLifecycleOwner, signUpObserver)

        return view
    }
}

class SignUpHelpFragment : Fragment()
{
    companion object{
        fun newInstance() = SignUpHelpFragment
    }

    private lateinit var backButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_signup_help, container, false)

        backButton = view.findViewById(R.id.sign_up_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(SignUpHelpFragmentDirections.actionSignUpHelpFragmentToSignUpTypeFragment())
        }

        return view
    }
}