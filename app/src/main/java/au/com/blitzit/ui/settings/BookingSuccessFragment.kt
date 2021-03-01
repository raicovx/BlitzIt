package au.com.blitzit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class BookingSuccessFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = BookingSuccessFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_consult_success, container, false)

        backButton = view.findViewById(R.id.consult_success_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(BookingSuccessFragmentDirections.actionBookingSuccessFragment2ToContactFragment())
        }

        return view
    }
}