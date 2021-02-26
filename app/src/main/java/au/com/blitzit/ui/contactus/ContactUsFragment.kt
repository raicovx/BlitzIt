package au.com.blitzit.ui.contactus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import au.com.blitzit.R

class ContactUsFragment : Fragment()
{
    companion object {
        fun newInstance() = ContactUsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_contactus, container, false)
    }
}