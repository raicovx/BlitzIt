package au.com.blitzit.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.ui.budget.BudgetCategories
import com.amplifyframework.core.Amplify

class DashboardFragment : Fragment() {

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private lateinit var viewModel: DashboardViewModel
    private lateinit var IDLSButton: Button

    private lateinit var titleName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        var mActivity : MainActivity = activity as MainActivity
        mActivity.ShowFAB()

        IDLSButton = view.findViewById(R.id.dashboard_budget_improved_living)
        IDLSButton.setOnClickListener {
            this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(BudgetCategories.ImprovedDailyLivingSkills))
        }

        //Amplify.Auth.fetchUserAttributes()

        titleName = view.findViewById(R.id.dashboard_name)
        titleName.text = AuthServices.userData.getFullName()


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}