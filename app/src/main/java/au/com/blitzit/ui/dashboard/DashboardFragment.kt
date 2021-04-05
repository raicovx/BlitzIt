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
import com.app.progresviews.ProgressWheel
import java.math.RoundingMode
import java.text.DecimalFormat

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

        val activePlan: UserPlan? = AuthServices.userData.findActivePlan()
        val startDate: TextView = view.findViewById(R.id.dashboard_plan_start)
        val endDate: TextView = view.findViewById(R.id.dashboard_plan_end)
        val sDate = "Start Date: " + activePlan?.planStartDate
        val eDate = "End Date: " + activePlan?.planEndDate
        startDate.text = sDate
        endDate.text = eDate
    }

    private fun createCategories(inflater: LayoutInflater, container: ViewGroup?)
    {
        val categoryList : List<String>? = AuthServices.userData.findActivePlan()?.getPartCategories()

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
        val parts: List<PlanParts>? = AuthServices.userData.findActivePlan()?.getPartListByCategory(category)
        if (parts != null)
        {
            for(part: PlanParts in parts)
            {
                //Fill categories with sub categories here
                val subCategoryView = inflater.inflate(R.layout.part_dashboard_subcategory, container, false)

                val subTitle: TextView = subCategoryView.findViewById(R.id.part_subcategory_title)
                subTitle.text = part.label

                val currencyDF = DecimalFormat("###,###,###.##")
                val startBalance: TextView = subCategoryView.findViewById(R.id.part_subcategory_start_balance)
                val startBalanceStr = "$" + currencyDF.format(part.budget)
                startBalance.text = startBalanceStr
                val balance: TextView = subCategoryView.findViewById(R.id.part_subcategory_balance)
                val balanceStr = "$" + currencyDF.format(part.balance)
                balance.text = balanceStr

                val progress: ProgressWheel = subCategoryView.findViewById(R.id.part_subcategory_progress)
                val percent = part.balance / part.budget * 100
                val radialPercent = (360 * percent) / 100
                val df = DecimalFormat("#")
                df.roundingMode = RoundingMode.CEILING
                progress.setPercentage(df.format(radialPercent).toInt())
                progress.setStepCountText(df.format(percent).toString() + "%")

                //Sets up the view budget button
                val viewButton: Button = subCategoryView.findViewById(R.id.part_subcategory_view_budget_button)
                viewButton.setOnClickListener {
                    this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(part.category))
                }

                container?.addView(subCategoryView)
            }
        }
    }

}