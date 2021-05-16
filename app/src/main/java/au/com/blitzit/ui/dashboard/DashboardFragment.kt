package au.com.blitzit.ui.dashboard

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.PurposeWithCategories
import com.app.progresviews.ProgressWheel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private lateinit var viewModel: DashboardViewModel

    private lateinit var layoutFiller: LinearLayout

    //BackButton
    private lateinit var backButton: Button
    private lateinit var backButtonImage: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //Show the FAB
        val mActivity : MainActivity = activity as MainActivity
        mActivity.showFAB()

        //Back button
        setupBackButton(view)

        //Dashboard Header
        setupDashboardHeader(view)

        layoutFiller = view.findViewById(R.id.dashboard_layout_filler)

        val purposeObserver = Observer<List<PurposeWithCategories>>{
            populateDashboard(it, inflater, layoutFiller)
        }
        viewModel.purposes.observe(viewLifecycleOwner, purposeObserver)

        return view
    }

    private fun setupBackButton(view: View)
    {
        //Back button - ONLY VISIBLE WHEN VIEWING OLDER PLANS
        backButton = view.findViewById(R.id.dashboard_back_button)
        backButton.setOnClickListener {
            //Reset selected plan
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.resetSelectedPlan()
            }
            this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToMyPlansFragment())
        }
        backButtonImage = view.findViewById(R.id.dashboard_back_button_image)
        if(checkPlanStatusExpired())
        {
            backButtonImage.isVisible = true
            backButton.isVisible = true
        }
        else
        {
            backButton.isVisible = false
            backButtonImage.isVisible = false
        }
    }

    private fun setupDashboardHeader(view: View)
    {
        val titleName: TextView = view.findViewById(R.id.dashboard_name)
        titleName.text = viewModel.participant.getFullName()
        val ndisNumber: TextView = view.findViewById(R.id.dashboard_ndis_number)
        val ndis = "NDIS Number: ${viewModel.participant.ndisNumber}"
        ndisNumber.text = ndis

        val planStatus: TextView = view.findViewById(R.id.dashboard_plan_status)
        CranstekHelper.setPlanStatusDisplay(planStatus, viewModel.selectedPlan.status)

        val startDate: TextView = view.findViewById(R.id.dashboard_plan_start)
        val endDate: TextView = view.findViewById(R.id.dashboard_plan_end)
        val sDate = "Start Date: " + CranstekHelper.formatDate(viewModel.selectedPlan.plan_start_date)
        val eDate = "End Date: " + CranstekHelper.formatDate(viewModel.selectedPlan.plan_end_date)
        startDate.text = sDate
        endDate.text = eDate

        //View statements button
        val viewStatements: Button = view.findViewById(R.id.dashboard_view_statements)
        handleButtonColours(viewStatements)
        viewStatements.isVisible = false
    }

    private fun populateDashboard(purposes: List<PurposeWithCategories>, inflater: LayoutInflater, container: ViewGroup)
    {
        //Get List of purposes that have categories
        for(purposeWithCategories: PurposeWithCategories in purposes)
        {
            if(purposeWithCategories.categories.isNotEmpty())
            {
                val purposeView = inflater.inflate(R.layout.part_dashboard_category, container, false)

                val titleText: TextView = purposeView.findViewById(R.id.part_category_title)
                titleText.text = purposeWithCategories.purpose.name

                //Add category headers to list (This is to handle colour changes)
                handleCategoryHeaderColours(titleText)

                //Add this view to the container
                container.addView(purposeView)

                //Create all category views for this purpose
                val categoryHolder: LinearLayout = purposeView.findViewById(R.id.part_subcategory_filler)
                for(category: Category in purposeWithCategories.categories)
                    if(category.plan_id == purposeWithCategories.purpose.plan_id)
                        createCategoryView(category, inflater, categoryHolder)
            }
        }
    }

    private fun createCategoryView(category: Category, inflater: LayoutInflater, container: ViewGroup)
    {
        val categoryView = inflater.inflate(R.layout.part_dashboard_subcategory, container, false)

        categoryView.findViewById<TextView>(R.id.part_subcategory_title).text = category.label

        val startBalance: TextView = categoryView.findViewById(R.id.part_subcategory_start_balance)
        startBalance.text = CranstekHelper.convertToCurrency(category.budget)
        val balance: TextView = categoryView.findViewById(R.id.part_subcategory_balance)
        balance.text = CranstekHelper.convertToCurrency(category.balance)

        val progress: ProgressWheel = categoryView.findViewById(R.id.part_subcategory_progress)
        CranstekHelper.setRadialWheel(progress, category.budget, category.balance)
        //progressWheels = progressWheels + progressWheels.plus(progress)

        //Sets up the view budget button
        val viewButton: Button = categoryView.findViewById(R.id.part_subcategory_view_budget_button)
        viewButton.setOnClickListener {
            this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(category.category, category.label, category.purpose))
        }
        handleButtonColours(viewButton)

        container.addView(categoryView)
    }

    private fun checkPlanStatusExpired(): Boolean
    {
        return if(AuthServices.selectedPlan.status == "Expired" || AuthServices.selectedPlan.status == "EXPIRED")
        {
            backButtonImage.isVisible = true
            backButton.isVisible = true
            true
        }
        else
            false
    }

    private fun handleButtonColours(button: Button)
    {
        if(checkPlanStatusExpired())
        {
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.old_button)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun handleCategoryHeaderColours(text: TextView)
    {
        if(checkPlanStatusExpired())
            text.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
    }

    /*private fun unUsedFunction()
    {
        if(!progressWheels.isNullOrEmpty())
        {
            for(progress: ProgressWheel in progressWheels)
            {
                //TODO("Progress wheel colour changes")
            }
        }
    }*/
}