package au.com.blitzit.ui.trackspending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R

class TrackSpendingFragment: Fragment()
{
    companion object
    {
        fun newInstance() = TrackSpendingFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_track_spending, container, false)

        val backButton: Button = view.findViewById(R.id.spending_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(TrackSpendingFragmentDirections.actionTrackSpendingFragmentToMenuFragment())
        }

        setupPage(view)

        return view
    }

    private fun setupPage(view: View)
    {
        setupSpinner(view)
        populateBaseData(view)
    }

    private fun setupSpinner(view: View)
    {
        /*Create the ArrayAdapter using the string resource
        val elements: Array<String> = { "1"; "2" }
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_invoice_filter_item, elements)
        dataAdapter.setDropDownViewResource(R.layout.spinner_invoice_filter_dropdown_item)
        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = this*/
    }

    private fun populateBaseData(view: View)
    {

    }
}