package au.com.blitzit.ui.plans

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.data.UserPlan
import au.com.blitzit.helper.CranstekHelper
import com.google.android.material.card.MaterialCardView

class MyPlansFragment: Fragment()
{
    companion object{
        fun newInstance() = MyPlansFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_myplans, container, false)

        setupHeader(view)
        populatePlans(view, inflater)

        return view
    }

    private fun setupHeader(view: View)
    {
        val backButton: Button = view.findViewById(R.id.my_plan_back_button)
        backButton.setOnClickListener {
            this.findNavController().navigate(MyPlansFragmentDirections.actionMyPlansFragmentToMenuFragment())
        }

        view.findViewById<TextView>(R.id.my_plan_profile_name).text = AuthServices.userData.getFullName()
        val ndis: String = "NDIS Number: " + AuthServices.userData.ndisNumber.toString()
        view.findViewById<TextView>(R.id.my_plan_ndis_number).text = ndis
    }

    private fun populatePlans(mainView: View, inflater: LayoutInflater)
    {
        val container: LinearLayout = mainView.findViewById(R.id.my_plans_content_holder)
        val userPlans: List<UserPlan> = AuthServices.userData.plans!!.toList().sortedBy { it.getSortingOrder() }

        if(!userPlans.isNullOrEmpty())
        {
            var lastPlan: UserPlan = userPlans[0]

            for(plan: UserPlan in userPlans)
            {
                if(lastPlan != plan && plan.status != "Active" && lastPlan.status == "Active")
                {
                    val view = inflater.inflate(R.layout.part_divider, container, false)
                    container.addView(view)
                }

                val view = inflater.inflate(R.layout.part_plan_display, container, false)
                view.findViewById<TextView>(R.id.plan_start_date).text = plan.planStartDate
                view.findViewById<TextView>(R.id.plan_end_date).text = plan.planEndDate
                CranstekHelper.setPlanStatusDisplay(view.findViewById(R.id.plan_status), plan.status)

                //Setup button
                val planSelector: MaterialCardView = view.findViewById(R.id.plan_top_view)
                planSelector.setOnClickListener{
                    AuthServices.userData.setSelectedPlan(plan)
                    this.findNavController().navigate(MyPlansFragmentDirections.actionMyPlansFragmentToDashboardFragment())
                }

                container.addView(view)
                lastPlan = plan
            }
        }
    }
}