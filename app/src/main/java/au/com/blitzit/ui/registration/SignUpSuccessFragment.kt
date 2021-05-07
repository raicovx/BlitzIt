package au.com.blitzit.ui.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class SignUpSuccessFragment: Fragment()
{
    companion object{
        fun newInstance() = SignUpSuccessFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_signup_success, container, false)

        val proceedButton = view.findViewById<Button>(R.id.sign_up_proceed)
        proceedButton.setOnClickListener {
            this.findNavController().navigate(SignUpSuccessFragmentDirections.actionSignUpSuccessFragmentToSignUpConfirmationFragment())
        }

        return view
    }
}