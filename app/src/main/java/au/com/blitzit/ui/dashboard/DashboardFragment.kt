package au.com.blitzit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import au.com.blitzit.MainActivity
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.PurposeWithCategories
import au.com.blitzit.views.RadialProgressBar
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
        if(AuthServices.checkPlanStatusExpired())
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
        CranstekHelper.setPlanStatusDisplay(requireContext(), planStatus, viewModel.selectedPlan.status)

        val startDate: TextView = view.findViewById(R.id.dashboard_plan_start)
        val endDate: TextView = view.findViewById(R.id.dashboard_plan_end)
        val sDate = "Start Date: " + CranstekHelper.formatDate(viewModel.selectedPlan.plan_start_date)
        val eDate = "End Date: " + CranstekHelper.formatDate(viewModel.selectedPlan.plan_end_date)
        startDate.text = sDate
        endDate.text = eDate

        //View statements button
        val viewStatements: Button = view.findViewById(R.id.dashboard_view_statements)
        CranstekHelper.handleButtonColours(requireContext(), viewStatements)
        viewStatements.isVisible = false
    }

    private fun populateDashboard(purposes: List<PurposeWithCategories>, inflater: LayoutInflater, container: ViewGroup)
    {
        //Get List of purposes that have categories
        for(purposeWithCategories: PurposeWithCategories in purposes)
        {
            if(hasValidCategories(purposeWithCategories))
            {
                val purposeView = inflater.inflate(R.layout.part_dashboard_category, container, false)

                val titleText: TextView = purposeView.findViewById(R.id.part_category_title)
                titleText.text = purposeWithCategories.purpose.name

                //Add category headers to list (This is to handle colour changes)
                CranstekHelper.handleCategoryTextViewColours(requireContext(), titleText)

                //Add this view to the container
                container.addView(purposeView)

                //Create all category views for this purpose
                val categoryHolder: LinearLayout = purposeView.findViewById(R.id.part_subcategory_filler)
                for(category: Category in purposeWithCategories.categories)
                    if(category.plan_id == purposeWithCategories.purpose.plan_id && category.ndis_number == AuthServices.loggedParticipant.ndisNumber)
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

        val progress: RadialProgressBar = categoryView.findViewById(R.id.part_subcategory_progress)
        CranstekHelper.setRadialWheel(progress, category.budget, category.balance)
        CranstekHelper.handleRadialProgressBarColours(requireContext(), progress)

        //Sets up the view budget button
        val viewButton: Button = categoryView.findViewById(R.id.part_subcategory_view_budget_button)
        viewButton.setOnClickListener {
            this.findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToCategoryBudgetFragment(category.category, category.label, category.purpose))
        }
        CranstekHelper.handleButtonColours(requireContext(), viewButton)

        container.addView(categoryView)
    }

    //Checks if any categories are from the selected plan and user
    private fun hasValidCategories(purposeWithCategories: PurposeWithCategories): Boolean
    {
        for(category in purposeWithCategories.categories)
        {
            if(category.plan_id == AuthServices.selectedPlan.plan_id && category.ndis_number == AuthServices.loggedParticipant.ndisNumber)
                return true
        }
        return false
    }
}