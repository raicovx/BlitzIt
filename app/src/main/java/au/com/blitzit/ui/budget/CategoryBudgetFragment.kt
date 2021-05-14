package au.com.blitzit.ui.budget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.PlanParts
import au.com.blitzit.helper.CranstekHelper
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

        setupViewForSelected(view, inflater)

        return view
    }

    private fun setupViewForSelected(view: View, inflater: LayoutInflater)
    {
        //planPart = AuthServices.userData.getSelectedPlan().getPartListByCategory(args.catBudgetArgs)[args.planPartNumber]
        planPart = AuthServices.userData.getSelectedPlan().getPartListByCategory(args.catBudgetArgs)[1]

        val titleText: TextView = view.findViewById(R.id.cat_budget_title)
        titleText.text = planPart.category
        val subtitleText: TextView = view.findViewById(R.id.cat_budget_subtitle)
        subtitleText.text = planPart.label

        //View tracking button
        val viewTrackingButton = view.findViewById<Button>(R.id.cat_budget_view_tracking)
        viewTrackingButton.setOnClickListener {
            this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToTrackSpendingFragment(planPart.label))
        }

        //Overview card
        val startingBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_start_balance)
        startingBalanceTV.text = CranstekHelper.convertToCurrency(planPart.budget)
        val currentBalanceTV: TextView = view.findViewById(R.id.cat_budget_overview_current_balance)
        currentBalanceTV.text = CranstekHelper.convertToCurrency(planPart.balance)
        val progressWheel: ProgressWheel = view.findViewById(R.id.cat_budget_overview_progress)
        CranstekHelper.setRadialWheel(progressWheel, planPart.budget, planPart.balance)

        //Provider section population
        val providerContentHolder: LinearLayout = view.findViewById(R.id.cat_budget_provider_content_holder)
        populateProviders(inflater, providerContentHolder)
    }

    private fun populateProviders(inflater: LayoutInflater, container: ViewGroup)
    {
        var providerSpending: Map<String, Double> = if(planPart.category == "Core" || planPart.category == "CORE") {
            AuthServices.userData.getSelectedPlan().getProviderSpendingByCategoryLabels(planPart.subLabels)
        } else
            AuthServices.userData.getSelectedPlan().getProviderSpendingByCategoryLabel(planPart.label)

        for(providerSpend: Map.Entry<String, Double> in providerSpending)
        {
            val dividerView = inflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = inflater.inflate(R.layout.part_budget_provider, container, false)

            val providerIndex = AuthServices.userData.getSelectedPlan().getProviderSummaryIndexByProviderName(providerSpend.key)
            val providerSelectionButton = view.findViewById<LinearLayout>(R.id.provider_button)
            providerSelectionButton.setOnClickListener {
                this.findNavController().navigate(CategoryBudgetFragmentDirections.actionCategoryBudgetFragmentToProviderDetailFragment(providerIndex))
            }

            view.findViewById<TextView>(R.id.provider_id).text = providerSpend.key
            view.findViewById<TextView>(R.id.provider_amount).text = CranstekHelper.convertToCurrency(providerSpend.value)

            container.addView(view)
        }
    }
}