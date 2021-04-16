package au.com.blitzit.ui.dashboard

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.CompoundButtonCompat
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

    private var displayingOldPlan: Boolean = false
    private lateinit var displayingPlan: UserPlan

    private lateinit var layoutFiller: LinearLayout

    //Colour changing group
    private var buttons: List<Button> = emptyList()
    private var categoryHeaders: List<TextView> = emptyList()
    private var progressWheels: List<ProgressWheel> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val mActivity : MainActivity = activity as MainActivity
        mActivity.ShowFAB()

        getPlanToDisplay()
        setupDashboard(view)

        layoutFiller = view.findViewById(R.id.dashboard_layout_filler)
        createCategories(inflater, layoutFiller)
        if(displayingOldPlan)
            handleOldPlanColours()

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

        val planStatus: TextView = view.findViewById(R.id.dashboard_plan_status)
        CranstekHelper.setPlanStatusDisplay(planStatus, displayingPlan.status)

        val startDate: TextView = view.findViewById(R.id.dashboard_plan_start)
        val endDate: TextView = view.findViewById(R.id.dashboard_plan_end)
        val sDate = "Start Date: " + displayingPlan.planStartDate
        val eDate = "End Date: " + displayingPlan.planEndDate
        startDate.text = sDate
        endDate.text = eDate

        //View statements button
        val viewStatements: Button = view.findViewById(R.id.dashboard_view_statements)
        buttons = buttons + buttons.plus(viewStatements)
        //TODO("Hook up view statements button")
    }

    private fun createCategories(inflater: LayoutInflater, container: ViewGroup?)
    {
        val categoryList : List<String> = displayingPlan.getPartCategories()

        for(category: String in categoryList)
        {
            //Create categories here
            val categoryView = inflater.inflate(R.layout.part_dashboard_category, container, false)
            val titleText: TextView = categoryView.findViewById(R.id.part_category_title)
            titleText.text = category

            //Add category headers to list
            categoryHeaders = categoryHeaders + categoryHeaders.plus(titleText)

            container?.addView(categoryView)

            val subCategoryFiller: LinearLayout = categoryView.findViewById(R.id.part_subcategory_filler)
            createSubCategories(category, inflater, subCategoryFiller)
        }
    }

    private fun createSubCategories(category: String, inflater: LayoutInflater, container: ViewGroup?)
    {
        val parts: List<PlanParts> = displayingPlan.getPartListByCategory(category)
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

            val progress: ProgressWheel = subCategoryView.findViewById(R.id.part_subcategory_progress)
            CranstekHelper.setRadialWheel(progress, parts[i].budget, parts[i].balance)
            progressWheels = progressWheels + progressWheels.plus(progress)

            //Sets up the view budget button
            val viewButton: Button = subCategoryView.findViewById(R.id.part_subcategory_view_budget_button)
            viewButton.setOnClickListener {
                this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(parts[i].category, i))
            }
            buttons = buttons + buttons.plus(viewButton)

            container?.addView(subCategoryView)
        }
    }

    private fun getPlanToDisplay()
    {
        displayingPlan = AuthServices.userData.getSelectedPlan()
        if(!AuthServices.userData.isSelectedPlanMostRecent(displayingPlan))
            displayingOldPlan = true
    }

    private fun handleOldPlanColours()
    {
        if(!buttons.isNullOrEmpty())
        {
            for(button: Button in buttons)
            {
                button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.old_button)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
        if(!categoryHeaders.isNullOrEmpty())
        {
            for(text: TextView in categoryHeaders)
            {
                text.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
            }
        }
        if(!progressWheels.isNullOrEmpty())
        {
            for(progress: ProgressWheel in progressWheels)
            {
                //TODO("Progress wheel colour changes")
            }
        }
    }
}