package au.com.blitzit.data

import android.util.Log
import au.com.blitzit.helper.CranstekHelper

class UserData constructor(
        val ndis_number: Int,
        val first_name: String,
        val last_name: String,
        val mobile: String,
        val address_line: String,
        val suburb: String,
        val postcode: Int,
        val state: String,
        val date_of_birth: String,
        var plans: Array<UserPlan>?)
{
    //Store the current users active plan
    private lateinit var selectedPlan: UserPlan

    fun getFullName(): String
    {
        return "$first_name $last_name"
    }

    fun getSelectedPlan(): UserPlan
    {
        return selectedPlan
    }

    fun setSelectedPlan(plan: UserPlan)
    {
        selectedPlan = plan
    }

    fun getMostRecentPlan(): UserPlan
    {
        selectedPlan = findMostRecentPlan()!!

        return selectedPlan
    }

    fun isSelectedPlanMostRecent(selectedPlan: UserPlan): Boolean
    {
        val mostRecent = findMostRecentPlan()
        return selectedPlan == mostRecent
    }

    private fun findMostRecentPlan(): UserPlan?
    {
        if(!plans.isNullOrEmpty())
        {
            var currentActive: UserPlan = plans!![0]
            for (i in plans!!.indices)
            {
                if(i != 0) //Don't check the first plan
                {
                    if (!CranstekHelper.isDateAfter(
                                    CranstekHelper.formatDate(currentActive.planStartDate),
                                    CranstekHelper.formatDate(plans!![i].planStartDate)))
                    {
                        //if current active is not after the current I plan
                        currentActive = plans!![i]
                    }
                }
            }

            return currentActive
        }
        else
            return null
    }

}