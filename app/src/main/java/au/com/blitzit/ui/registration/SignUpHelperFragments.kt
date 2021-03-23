package au.com.blitzit.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class SignUpTypeFragment : Fragment()
{
    companion object{
        fun newInstance() = SignUpTypeFragment
    }

    private lateinit var backButton: Button
    private lateinit var helpButton: Button

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

        val coordSignUpButton: Button = view.findViewById(R.id.sign_up_type_3)
        coordSignUpButton.setOnClickListener {
            this.findNavController().navigate(SignUpTypeFragmentDirections.actionSignUpTypeFragmentToSignUpFragment(SignUpAccountType.COORDINATOR))
        }

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