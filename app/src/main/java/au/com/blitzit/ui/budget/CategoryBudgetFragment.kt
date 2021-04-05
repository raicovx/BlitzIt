package au.com.blitzit.ui.budget

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.PlanParts
import au.com.blitzit.data.UserPlan
import com.app.progresviews.ProgressWheel


class CategoryBudgetFragment : Fragment()
{
    private lateinit var backButton : Button

    private val args: CategoryBudgetFragmentArgs by navArgs()
    private lateinit var planPart: PlanParts

    companion object
    {
        fun newInstance() = CategoryBudgetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_category_budget, container, false)

        backButton = view.findViewById(R.id.cat_budget_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToDashboardFragment())
        }

        setupViewForSelected(view)

        return view
    }

    private fun setupViewForSelected(view: View)
    {
        planPart = AuthServices.userData.findActivePlan()!!.getPartListByCategory(args.catBudgetArgs)[0]

        val titleText: TextView = view.findViewById(R.id.cat_budget_title)
        val subtitleText: TextView = view.findViewById(R.id.cat_budget_subtitle)

        //Overview card
        val startingBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_start_balance)
        val currentBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_current_balance)
        val progressWheel: ProgressWheel = view.findViewById(R.id.cat_budget_overview_progress)
    }
}