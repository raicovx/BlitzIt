package au.com.blitzit.data

import android.util.Log
import au.com.blitzit.helper.CranstekHelper
import com.google.gson.annotations.SerializedName

class UserData constructor(
        @SerializedName("ndis_number")
        val ndisNumber: Int,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("last_name")
        val lastName: String,
        val mobile: String,
        @SerializedName("address_line")
        val addressLine: String,
        val suburb: String,
        val postcode: Int,
        val state: String,
        @SerializedName("date_of_birth")
        val dateOfBirth: String,
        val email: String,
        @SerializedName("statement_email")
        val statementEmail: List<String>,
        @SerializedName("primary_contacts")
        val primaryContacts: List<PrimaryContact>,
        @SerializedName("support_coordinator")
        val supportCoordinators: List<SupportCoordinator>,
        var plans: Array<UserPlan>?)
{
    //Store the current users active plan
    private lateinit var selectedPlan: UserPlan

    fun getFullName(): String
    {
        return "$firstName $lastName"
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