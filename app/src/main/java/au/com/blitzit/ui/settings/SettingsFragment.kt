package au.com.blitzit.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R

class SettingsFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var contactButton: Button
    private lateinit var aboutAppButton: Button
    private lateinit var faqButton: Button
    private lateinit var profileButton: Button

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        setOnClickListeners(view)

        return view
    }

    private fun setOnClickListeners(view: View)
    {
        val navControl = this.findNavController()

        backButton = view.findViewById(R.id.setting_back_button)
        backButton.setOnClickListener {
            navControl.navigate(SettingsFragmentDirections.actionSettingsFragmentToMenuFragment())
        }

        contactButton = view.findViewById(R.id.settings_contact_us)
        contactButton.setOnClickListener {
            navControl.navigate(SettingsFragmentDirections.actionSettingsFragmentToContactFragment())
        }

        aboutAppButton = view.findViewById(R.id.settings_about_app)
        aboutAppButton.setOnClickListener {
            navControl.navigate(SettingsFragmentDirections.actionSettingsFragmentToAboutAppFragment())
        }

        faqButton = view.findViewById(R.id.settings_faq)
        faqButton.setOnClickListener {
            navControl.navigate(SettingsFragmentDirections.actionSettingsFragmentToFAQFragment())
        }

        profileButton = view.findViewById(R.id.settings_edit_profile)
        profileButton.setOnClickListener {
            navControl.navigate(SettingsFragmentDirections.actionSettingsFragmentToProfileEditFragment())
        }
    }
}