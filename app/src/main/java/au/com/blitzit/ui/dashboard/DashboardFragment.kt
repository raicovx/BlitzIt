package au.com.blitzit.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.PlanParts
import au.com.blitzit.data.UserPlan
import au.com.blitzit.helper.CranstekHelper
import com.app.progresviews.ProgressWheel

class DashboardFragment : Fragment() {

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private lateinit var viewModel: DashboardViewModel

    private lateinit var layoutFiller: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        var mActivity : MainActivity = activity as MainActivity
        mActivity.ShowFAB()

        setupDashboard(view)

        layoutFiller = view.findViewById(R.id.dashboard_layout_filler)
        createCategories(inflater, layoutFiller)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
    }

    private fun setupDashboard(view: View)
    {
        val titleName: TextView = view.findViewById(R.id.dashboard_name)
        titleName.text = AuthServices.userData.getFullName()
        val ndisNumber: TextView = view.findViewById(R.id.dashboard_ndis_number)
        val ndis = "NDIS Number: " + AuthServices.userData.ndis_number
        ndisNumber.text = ndis

        //TODO("Set the active, expired or archived plan display")

        val activePlan: UserPlan? = AuthServices.userData.getMostRecentPlan()
        val startDate: TextView = view.findViewById(R.id.dashboard_plan_start)
        val endDate: TextView = view.findViewById(R.id.dashboard_plan_end)
        val sDate = "Start Date: " + activePlan?.planStartDate
        val eDate = "End Date: " + activePlan?.planEndDate
        startDate.text = sDate
        endDate.text = eDate
    }

    private fun createCategories(inflater: LayoutInflater, container: ViewGroup?)
    {
        val categoryList : List<String>? = AuthServices.userData.getMostRecentPlan().getPartCategories()

        if (categoryList != null)
        {
            for(category: String in categoryList)
            {
                //Create categories here
                val categoryView = inflater.inflate(R.layout.part_dashboard_category, container, false)
                val titleText: TextView = categoryView.findViewById(R.id.part_category_title)
                titleText.text = category

                container?.addView(categoryView)

                val subCategoryFiller: LinearLayout = categoryView.findViewById(R.id.part_subcategory_filler)
                createSubCategories(category, inflater, subCategoryFiller)
            }
        }
    }

    private fun createSubCategories(category: String, inflater: LayoutInflater, container: ViewGroup?)
    {
        val parts: List<PlanParts>? = AuthServices.userData.getMostRecentPlan().getPartListByCategory(category)
        if (parts != null)
        {
            for(i in parts.indices)
            {
                //Fill categories with sub categories here
                val subCategoryView = inflater.inflate(R.layout.part_dashboard_subcategory, container, false)

                val subTitle: TextView = subCategoryView.findViewById(R.id.part_subcategory_title)
                subTitle.text = parts[i].label

                val startBalance: TextView = subCategoryView.findViewById(R.id.part_subcategory_start_balance)
                startBalance.text = CranstekHelper.convertToCurrency(parts[i].budget)
                val balance: TextView = subCategoryView.findViewById(R.id.part_subcategory_balance)
                balance.text = CranstekHelper.convertToCurrency(parts[i].balance)

                var progress: ProgressWheel = subCategoryView.findViewById(R.id.part_subcategory_progress)
                CranstekHelper.setRadialWheel(progress, parts[i].budget, parts[i].balance)

                //Sets up the view budget button
                val viewButton: Button = subCategoryView.findViewById(R.id.part_subcategory_view_budget_button)
                viewButton.setOnClickListener {
                    this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(parts[i].category, i))
                }

                container?.addView(subCategoryView)
            }
        }
    }

}