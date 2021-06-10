package au.com.blitzit.ui.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class AboutAppFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = AboutAppFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_about_app, container, false)

        backButton = view.findViewById(R.id.about_app_back_button)
        backButton.setOnClickListener { this.findNavController().navigate(AboutAppFragmentDirections.actionAboutAppFragmentToSettingsFragment()) }

        val pTV = view.findViewById<TextView>(R.id.privacy_link)
        pTV.movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}