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

    private fun populateBaseData()
    {
        weeklySpendTV.text = CranstekHelper.convertToCurrency(selectedPlanPart.averageTargetWeek)
        currentAverageSpendTV.text = CranstekHelper.convertToCurrency(selectedPlanPart.averageSpendWeek)
        planEndDateTV.text = CranstekHelper.convertToReadableDate(AuthServices.userData.getSelectedPlan().planEndDate)
        consumptionDateTV.text = CranstekHelper.convertToReadableDate(selectedPlanPart.estimatedExhaustionDate)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        selectedPlanPart = AuthServices.userData.getSelectedPlan().getPartByLabel(planPartLabels[position])!!
        populateBaseData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}