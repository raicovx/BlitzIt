package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.auth.EditSubmissionState

class ProfileEditSuccessFragment: Fragment()
{
    companion object{
        fun newInstance() = ProfileEditSuccessFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.fragment_profile_edit_success, container, false)

        AuthServices.editSubmissionState.postValue(EditSubmissionState.Awaiting)

        val backButton: Button = view.findViewById(R.id.profile_edit_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ProfileEditSuccessFragmentDirections.actionProfileEditSuccessFragmentToMenuFragment())
        }

        return view
    }
}