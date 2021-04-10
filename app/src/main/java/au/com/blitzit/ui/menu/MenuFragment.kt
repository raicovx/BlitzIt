package au.com.blitzit.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.ui.settings.SettingsFragmentDirections

class MenuFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var contactButton: Button
    private lateinit var settingsButton: Button
    private lateinit var profileButton: Button
    private lateinit var invoicesButton: Button

    companion object{
        fun newInstance() = MenuFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        setOnClickListeners(view)

        //Hide the FAB
        val mActivity : MainActivity = activity as MainActivity
        mActivity.HideFAB()

        return view
    }

    private fun setOnClickListeners(view: View)
    {
        backButton = view.findViewById(R.id.menu_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToDashboardFragment())
        }

        contactButton = view.findViewById(R.id.menu_contact_button)
        contactButton.setOnClickListener {
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToContactFragment())
        }

        settingsButton = view.findViewById(R.id.menu_settings_button)
        settingsButton.setOnClickListener {
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToSettingsFragment())
        }

        profileButton = view.findViewById(R.id.menu_profile_button)
        profileButton.setOnClickListener {
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToProfileFragment())
        }

        invoicesButton = view.findViewById(R.id.menu_invoices_button)
        invoicesButton.setOnClickListener {
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToInvoicesFragment())
        }

        val signOutButton: Button = view.findViewById(R.id.menu_sign_out_button)
        signOutButton.setOnClickListener {
            AuthServices.attemptSignOut()
            this.findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToIntro())
        }
    }
}