package au.com.blitzit.ui.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class FAQFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = FAQFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        backButton = view.findViewById(R.id.faq_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(FAQFragmentDirections.actionFAQFragmentToSettingsFragment())
        }

        view.findViewById<TextView>(R.id.faq_string_1).movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}