package au.com.blitzit.ui.trackspending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.PlanParts
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.ui.budget.CategoryBudgetFragmentDirections
import au.com.blitzit.views.DataPoint
import au.com.blitzit.views.GraphView
import kotlin.math.roundToInt

class TrackSpendingFragment: Fragment(), AdapterView.OnItemSelectedListener
{
    companion object
    {
        fun newInstance() = TrackSpendingFragment
    }

    private lateinit var mainView: View
    private lateinit var selectedPlanPart: PlanParts
    private lateinit var planPartLabels: List<String>

    private lateinit var weeklySpendTV: TextView
    private lateinit var currentAverageSpendTV: TextView
    private lateinit var planEndDateTV: TextView
    private lateinit var consumptionDateTV: TextView

    private lateinit var graph: GraphView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        mainView = inflater.inflate(R.layout.fragment_track_spending, container, false)

        val backButton: Button = mainView.findViewById(R.id.spending_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(TrackSpendingFragmentDirections.actionTrackSpendingFragmentToMenuFragment())
        }

        setupView()
        setupSpinner()

        return mainView
    }

    private fun setupSpinner()
    {
        val filterSpinner: Spinner = mainView.findViewById(R.id.spending_tracker_spinner)
        planPartLabels = AuthServices.userData.getSelectedPlan().getPartLabels()
        //Create the ArrayAdapter using the string resource
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_spending_item, planPartLabels)
        dataAdapter.setDropDownViewResource(R.layout.spinner_spending_dropdown)
        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = this

        //Sets default selection
        filterSpinner.setSelection(planPartLabels.indexOf("Core"))
    }

    private fun setupView()
    {
        weeklySpendTV = mainView.findViewById(R.id.spending_weekly_spend_data)
        currentAverageSpendTV = mainView.findViewById(R.id.spending_weekly_average_data)
        planEndDateTV = mainView.findViewById(R.id.spending_plan_end_data)
        consumptionDateTV = mainView.findViewById(R.id.spending_consumption_date_data)
    }

    private fun populateGraph()
    {
        graph = mainView.findViewById(R.id.spending_graph)

        var graphData: List<DataPoint> = emptyList()
        var index: Int = 0
        for(total: Map.Entry<String, Double> in selectedPlanPart.totals)
        {
            //val dataPoint = DataPoint(CranstekHelper.getMonthNumberFromDateString(total.key), total.value.roundToInt())
            val month: String = CranstekHelper.getMonthTitleFromMonthNumber(CranstekHelper.getMonthNumberFromDateString(total.key))
            val dataPoint = DataPoint(index, total.value.roundToInt(), month, total.value)
            graphData = graphData.plus(dataPoint)
            index++
        }

        graph.setGraphOffset(100,50)
        graph.setData(graphData)
        Log.i("GAZ_GRAPH", "totals: ${selectedPlanPart.totals}")
        Log.i("GAZ_GRAPH", "graph dataset: $graphData")
    }

    private fun populateBaseData()
    {
        weeklySpendTV.text = CranstekHelper.convertToCurrency(selectedPlanPart.averageTargetWeek)
        currentAverageSpendTV.text = CranstekHelper.convertToCurrency(selectedPlanPart.averageSpendWeek)
        planEndDateTV.text = CranstekHelper.convertToReadableDate(AuthServices.userData.getSelectedPlan().planEndDate)
        consumptionDateTV.text = CranstekHelper.convertToReadableDate(selectedPlanPart.estimatedExhaustionDate)
    }

    private fun populateCategorySpending()
    {
        val container: LinearLayout = mainView.findViewById(R.id.spending_provider_content_holder)

        //Remove all old views
        container.removeAllViews()

        var providerSpending: Map<String, Double> = if(selectedPlanPart.category == "Core" || selectedPlanPart.category == "CORE") {
                AuthServices.userData.getSelectedPlan().getProviderSpendingByCategoryLabels(selectedPlanPart.subLabels)
            } else
                AuthServices.userData.getSelectedPlan().getProviderSpendingByCategoryLabel(selectedPlanPart.label)

        for(providerSpend: Map.Entry<String, Double> in providerSpending)
        {
            val dividerView = layoutInflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = layoutInflater.inflate(R.layout.part_budget_provider, container, false)

            val providerIndex = AuthServices.userData.getSelectedPlan().getProviderSummaryIndexByProviderName(providerSpend.key)
            val providerSelectionButton = view.findViewById<LinearLayout>(R.id.provider_button)
            providerSelectionButton.setOnClickListener {
                this.findNavController().navigate(TrackSpendingFragmentDirections.actionTrackSpendingFragmentToProviderDetailFragment(providerIndex))
            }

            view.findViewById<TextView>(R.id.provider_id).text = providerSpend.key
            view.findViewById<TextView>(R.id.provider_amount).text = CranstekHelper.convertToCurrency(providerSpend.value)

            container.addView(view)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        selectedPlanPart = AuthServices.userData.getSelectedPlan().getPartByLabel(planPartLabels[position])!!
        populateBaseData()
        populateCategorySpending()
        populateGraph()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}