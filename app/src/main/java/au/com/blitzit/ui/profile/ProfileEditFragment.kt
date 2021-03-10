package au.com.blitzit.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class ProfileEditFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = ProfileEditFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_profile_edit, container, false)

        backButton = view.findViewById(R.id.profile_edit_back_button)
        backButton.setOnClickListener { this.findNavController().navigate(ProfileEditFragmentDirections.actionProfileEditFragmentToProfileFragment()) }

        //TODO("Hook up submit button")

        return view
    }
}