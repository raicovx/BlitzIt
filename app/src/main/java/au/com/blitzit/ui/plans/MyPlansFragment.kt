package au.com.blitzit.ui.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.helper.CranstekHelper
import au.com.blitzit.roomdata.Plan
import com.google.android.material.card.MaterialCardView

class MyPlansFragment: Fragment()
{
    companion object{
        fun newInstance() = MyPlansFragment
    }

    private lateinit var viewModel: MyPlansViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        viewModel = ViewModelProvider(this).get(MyPlansViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_myplans, container, false)

        setupHeader(view)

        val plansObserver = Observer<List<Plan>>{
            populatePlans(it, view, inflater)
        }
        viewModel.plans.observe(viewLifecycleOwner, plansObserver)

        return view
    }

    private fun setupHeader(view: View)
    {
        val backButton: Button = view.findViewById(R.id.my_plan_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(MyPlansFragmentDirections.actionMyPlansFragmentToMenuFragment())
        }

        view.findViewById<TextView>(R.id.my_plan_profile_name).text = AuthServices.loggedParticipant.getFullName()
        val ndis = "NDIS Number: ${AuthServices.loggedParticipant.ndisNumber}"
        view.findViewById<TextView>(R.id.my_plan_ndis_number).text = ndis
    }

    private fun populatePlans(plans: List<Plan>, mainView: View, inflater: LayoutInflater)
    {
        val container: LinearLayout = mainView.findViewById(R.id.my_plans_content_holder)

        if(!plans.isNullOrEmpty())
        {
            var lastPlan: Plan = plans[0]

            for(plan: Plan in plans)
            {
                if(lastPlan != plan && plan.status != "Active" && lastPlan.status == "Active")
                {
                    val view = inflater.inflate(R.layout.part_divider, container, false)
                    container.addView(view)
                }

                val view = inflater.inflate(R.layout.part_plan_display, container, false)
                view.findViewById<TextView>(R.id.plan_start_date).text = plan.plan_start_date
                view.findViewById<TextView>(R.id.plan_end_date).text = plan.plan_end_date
                CranstekHelper.setPlanStatusDisplay(view.findViewById(R.id.plan_status), plan.status)

                //Setup button
                val planSelector: MaterialCardView = view.findViewById(R.id.plan_top_view)
                planSelector.setOnClickListener{
                    AuthServices.selectedPlan = plan
                    this.findNavController().navigate(MyPlansFragmentDirections.actionMyPlansFragmentToDashboardFragment())
                }

                container.addView(view)
                lastPlan = plan
            }
        }
    }
}