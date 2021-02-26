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

class MenuFragment : Fragment()
{
    private lateinit var backButton: Button

    companion object{
        fun newInstance() = MenuFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener { _ ->
            val action = MenuFragmentDirections.actionMenuFragmentToDashboardFragment()
            this.findNavController().navigate(action)
        }

        //Hide the FAB
        var mActivity : MainActivity = activity as MainActivity
        mActivity.HideFAB()

        return view
    }
}