package au.com.blitzit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class ContactFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = ContactFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_contactus, container, false)

        backButton = view.findViewById(R.id.contact_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(ContactFragmentDirections.actionContactFragmentToSettingsFragment())
        }

        return view
    }
}