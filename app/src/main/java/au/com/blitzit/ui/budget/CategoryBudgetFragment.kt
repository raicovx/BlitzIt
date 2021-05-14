package au.com.blitzit.ui.budget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.PlanParts
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.ProviderCategorySpending
import au.com.blitzit.ui.dashboard.DashboardViewModel
import com.app.progresviews.ProgressWheel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class CategoryBudgetFragment : Fragment()
{
    private lateinit var backButton : Button

    private val args: CategoryBudgetFragmentArgs by navArgs()
    private lateinit var planPart: PlanParts

    private lateinit var viewModel: CategoryBudgetViewModel

    companion object
    {
        fun newInstance() = CategoryBudgetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //view
        val view = inflater.inflate(R.layout.fragment_category_budget, container, false)

        //Back button
        backButton = view.findViewById(R.id.cat_budget_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToDashboardFragment())
        }

        //view Model
        viewModel = ViewModelProvider(this).get(CategoryBudgetViewModel::class.java)

        //LiveData
        liveDataSubscriptions(view, inflater, view.findViewById(R.id.cat_budget_provider_content_holder))

        return view
    }

    private fun liveDataSubscriptions(view: View, inflater: LayoutInflater, providerContentHolder: LinearLayout)
    {
        //Subscribe to the livedata
        val categoryObserver = Observer<Category>{
            setupViewForSelected(it, view, inflater)
        }
        viewModel.categoryLiveData.observe(viewLifecycleOwner, categoryObserver)
        val providerCategorySpendingObserver = Observer<List<ProviderCategorySpending>> {
            populateProviderCategorySpending(it, inflater, providerContentHolder)
        }
        viewModel.providerCategorySpendingList.observe(viewLifecycleOwner, providerCategorySpendingObserver)

        //Async Gets
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCategory(args.category, args.label)
            viewModel.getProviderCategorySpending(args.purpose, AuthServices.selectedPlan.plan_id, args.label)
        }
    }

    private fun setupViewForSelected(category: Category, view: View, inflater: LayoutInflater)
    {
        val titleText: TextView = view.findViewById(R.id.cat_budget_title)
        titleText.text = category.category
        val subtitleText: TextView = view.findViewById(R.id.cat_budget_subtitle)
        subtitleText.text = category.label

        //View tracking button
        val viewTrackingButton = view.findViewById<Button>(R.id.cat_budget_view_tracking)
        viewTrackingButton.setOnClickListener {
            this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToTrackSpendingFragment(category.label))
        }

        //Overview card
        val startingBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_start_balance)
        startingBalanceTV.text = CranstekHelper.convertToCurrency(category.budget)
        val currentBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_current_balance)
        currentBalanceTV.text = CranstekHelper.convertToCurrency(category.balance)
        val progressWheel: ProgressWheel = view.findViewById(R.id.cat_budget_overview_progress)
        CranstekHelper.setRadialWheel(progressWheel, category.budget, category.balance)
    }

    private fun populateProviderCategorySpending(providerCategorySpendingList: List<ProviderCategorySpending>, inflater: LayoutInflater, container: ViewGroup)
    {
        for(providerSpend: ProviderCategorySpending in providerCategorySpendingList)
        {
            val dividerView = inflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = inflater.inflate(R.layout.part_budget_provider, container, false)


            val providerSelectionButton = view.findViewById<LinearLayout>(R.id.provider_button)
            providerSelectionButton.setOnClickListener {
                this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToProviderDetailFragment(providerSpend.provider_id))
            }

            viewLifecycleOwner.lifecycleScope.launch {
                view.findViewById<TextView>(R.id.provider_id).text = viewModel.getProviderName(providerSpend.provider_id)
            }
            view.findViewById<TextView>(R.id.provider_amount).text = CranstekHelper.convertToCurrency(providerSpend.spending)

            container.addView(view)
        }
    }
}