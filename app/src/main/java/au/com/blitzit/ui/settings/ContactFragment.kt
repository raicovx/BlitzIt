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
import org.w3c.dom.Text

class ContactFragment : Fragment()
{
    private lateinit var backButton: Button
    private lateinit var bookButton: Button

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

        bookButton = view.findViewById(R.id.contact_book)
        bookButton.setOnClickListener {
            this.findNavController().navigate(ContactFragmentDirections.actionContactFragmentToBookingFragment())
        }
        bookButton.visibility = View.GONE

        view.findViewById<TextView>(R.id.contact_us_web_link).movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}