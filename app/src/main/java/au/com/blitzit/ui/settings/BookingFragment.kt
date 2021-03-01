package au.com.blitzit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class BookingFragment: Fragment()
{
    private lateinit var backButton: Button

    companion object
    {
        fun newInstance() = BookingFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_book_consult, container, false)

        backButton = view.findViewById(R.id.booking_back_button)
        backButton.setOnClickListener { this.findNavController().navigate(BookingFragmentDirections.actionBookingFragmentToContactFragment()) }

        TODO("Spinner for appointment types")
        TODO("Situation selection")
        TODO("Finish fixing warnings in fragment_book_consult.xml")

        return view
    }
}