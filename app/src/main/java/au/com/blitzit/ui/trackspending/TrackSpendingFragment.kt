package au.com.blitzit.ui.trackspending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.ProviderCategorySpending
import au.com.blitzit.ui.budget.CategoryBudgetFragmentDirections
import au.com.blitzit.views.DataPoint
import au.com.blitzit.views.GraphView
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TrackSpendingFragment: Fragment(), AdapterView.OnItemSelectedListener
{
    companion object
    {
        fun newInstance() = TrackSpendingFragment
    }

    private lateinit var viewModel: TrackSpendingViewModel

    private lateinit var mainView: View

    private lateinit var weeklySpendTV: TextView
    private lateinit var currentAverageSpendTV: TextView
    private lateinit var planEndDateTV: TextView
    private lateinit var consumptionDateTV: TextView
    private lateinit var onTrackIcon: TextView

    private lateinit var graph: GraphView

    private val args: TrackSpendingFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        //view Model
        viewModel = ViewModelProvider(this).get(TrackSpendingViewModel::class.java)

        //View
        mainView = inflater.inflate(R.layout.fragment_track_spending, container, false)

        //Back Button
        val backButton: Button = mainView.findViewById(R.id.spending_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(TrackSpendingFragmentDirections.actionTrackSpendingFragmentToMenuFragment())
        }

        setupView()

        //Get categories
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCategories()
            setupSpinner()
        }

        //Live data
        val selectedCategoryObserver = Observer<Category> {
            onSelectedCategoryChange(it)
        }
        viewModel.selectedCategory.observe(viewLifecycleOwner, selectedCategoryObserver)
        val providerSpendingObserver = Observer<List<ProviderCategorySpending>> {
            onProviderCategorySpendingListChange(it)
        }
        viewModel.providerCategorySpendingList.observe(viewLifecycleOwner, providerSpendingObserver)

        return mainView
    }

    private fun setupSpinner()
    {
        val filterSpinner: Spinner = mainView.findViewById(R.id.spending_tracker_spinner)
        val categoryLabels = viewModel.getCategoryLabels()

        //Create the ArrayAdapter using the string resource
        val dataAdapter = ArrayAdapter(requireContext(), R.layout.spinner_spending_item, categoryLabels)
        dataAdapter.setDropDownViewResource(R.layout.spinner_spending_dropdown)

        filterSpinner.adapter = dataAdapter
        filterSpinner.onItemSelectedListener = this

        if(args.selectedPlanPartLabel.isNullOrBlank()) {
            //Sets default selection
            filterSpinner.setSelection(categoryLabels.indexOf("CORE"))
        } else {
            filterSpinner.setSelection(categoryLabels.indexOf(args.selectedPlanPartLabel))
        }
    }

    private fun setupView()
    {
        weeklySpendTV = mainView.findViewById(R.id.spending_weekly_spend_data)
        currentAverageSpendTV = mainView.findViewById(R.id.spending_weekly_average_data)
        planEndDateTV = mainView.findViewById(R.id.spending_plan_end_data)
        consumptionDateTV = mainView.findViewById(R.id.spending_consumption_date_data)
        onTrackIcon = mainView.findViewById(R.id.spending_on_track_icon)
    }

    private fun populateGraph(selectedCategory: Category)
    {
        graph = mainView.findViewById(R.id.spending_graph)

        if(selectedCategory.totals.isNullOrEmpty())
            graph.isVisible = false
        else
        {
            graph.isVisible = true
            var graphData: List<DataPoint> = emptyList()
            var index = 0
            for (total: Map.Entry<String, Double> in selectedCategory.totals)
            {
                val monthNumber: Int = CranstekHelper.getMonthNumberFromDateString(total.key)
                val month: String = CranstekHelper.getMonthTitleFromMonthNumber(monthNumber)
                val dataPoint = DataPoint(index, total.value.roundToInt(), month, total.value)
                graphData = graphData.plus(dataPoint)

                //Tack some months on the end if there is only 1 point
                if(selectedCategory.totals.size == 1)
                {
                    for(i in 1..3)
                    {
                        val additionalIndex: Int = index + i
                        val additionalMonthNumber: Int = monthNumber + i
                        val additionalMonth: String = CranstekHelper.getMonthTitleFromMonthNumber(additionalMonthNumber)
                        val additionalDataPoint = DataPoint(additionalIndex, 25, additionalMonth, 0.0)

                        graphData = graphData.plus(additionalDataPoint)
                    }
                }

                index++
            }

            graph.setGraphOffset(100, 50)
            graph.setData(graphData)
        }
    }

    private fun populateBaseData(selectedCategory: Category)
    {
        weeklySpendTV.text = CranstekHelper.convertToCurrency(selectedCategory.averageTargetWeek)
        currentAverageSpendTV.text = CranstekHelper.convertToCurrency(selectedCategory.averageSpendWeek)
        planEndDateTV.text = CranstekHelper.formatDate(AuthServices.selectedPlan.plan_end_date)
        consumptionDateTV.text = CranstekHelper.formatDateNoFancyOnInput(selectedCategory.estimatedExhaustionDate)

        //On track display
        if(selectedCategory.checkMonthlySpendOnTrack()) {
            onTrackIcon.text = getString(R.string.on_track)
            onTrackIcon.setBackgroundResource(R.drawable.active_display)
            onTrackIcon.setPadding(28, 4, 28, 4)
        } else {
            onTrackIcon.text = getString(R.string.over_spending)
            onTrackIcon.setBackgroundResource(R.drawable.expired_display)
            onTrackIcon.setPadding(28, 4, 28, 4)
        }
    }

    private fun populateProviderCategorySpending(providerCategorySpendingList: List<ProviderCategorySpending>)
    {
        val container: LinearLayout = mainView.findViewById(R.id.spending_provider_content_holder)

        //Remove all old views
        container.removeAllViews()

        for(providerSpend: ProviderCategorySpending in providerCategorySpendingList)
        {
            val dividerView = layoutInflater.inflate(R.layout.part_invoice_divider, container, false)
            container.addView(dividerView)

            val view = layoutInflater.inflate(R.layout.part_budget_provider, container, false)

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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        viewModel.selectedCategory.postValue(viewModel.categories[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun onSelectedCategoryChange(category: Category)
    {
        populateBaseData(category)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getProviderCategorySpending(category.purpose, AuthServices.selectedPlan.plan_id, category.label)
        }
        populateGraph(category)
    }
    private fun onProviderCategorySpendingListChange(changedList: List<ProviderCategorySpending>)
    {
        populateProviderCategorySpending(changedList)
    }
}