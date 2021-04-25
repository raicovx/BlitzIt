package au.com.blitzit.ui.menu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.ui.settings.SettingsFragmentDirections
import kotlinx.coroutines.runBlocking

class MenuFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var contactButton: Button
    private lateinit var settingsButton: Button
    private lateinit var profileButton: Button
    private lateinit var invoicesButton: Button
    private lateinit var myPlansButton: Button
    private lateinit var myProvidersButton: Button

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
        val navController: NavController = this.findNavController()

        backButton = view.findViewById(R.id.menu_back_button)
        backButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToDashboardFragment())
        }

        contactButton = view.findViewById(R.id.menu_contact_button)
        contactButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToContactFragment())
        }

        settingsButton = view.findViewById(R.id.menu_settings_button)
        settingsButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToSettingsFragment())
        }

        profileButton = view.findViewById(R.id.menu_profile_button)
        profileButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToProfileFragment())
        }

        invoicesButton = view.findViewById(R.id.menu_invoices_button)
        invoicesButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToInvoicesFragment())
        }

        myPlansButton = view.findViewById(R.id.menu_plans_button)
        myPlansButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToMyPlansFragment())
        }

        myProvidersButton = view.findViewById(R.id.menu_providers_button)
        myProvidersButton.setOnClickListener {
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToMyProvidersFragment())
        }

        val signOutButton: Button = view.findViewById(R.id.menu_sign_out_button)
        signOutButton.setOnClickListener {
            runBlocking { AuthServices.attemptSignOut() }
            navController.navigate(MenuFragmentDirections.actionMenuFragmentToIntro())
        }
    }
}