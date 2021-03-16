package au.com.blitzit.data

import android.util.Log
import com.google.gson.annotations.SerializedName

class UserData constructor(
        val ndis_number: Int,
        val first_name: String,
        val last_name: String,
        val mobile: Int,
        val address_line: String,
        val suburb: String,
        val postcode: Int,
        val state: String,
        val date_of_birth: String,
        val plans: Array<UserPlan>?)
{
    fun getFullName(): String
    {
        return "$first_name $last_name"
    }
}

class UserPlan constructor(
        val status: String,
        @SerializedName("plan_start_date")
        val planStartDate: String,
        @SerializedName("plan_id")
        val planID: String,
        @SerializedName("plan_end_date")
        val planEndDate: String,
        @SerializedName("parts")
        val planCategories: Array<PlanCategory>)
{
    companion object{
        fun findActivePlan(plans: Array<UserPlan>) : UserPlan?
        {
            for(plan: UserPlan in plans)
            {
                if(plan.status == "Active") {
                    Log.i("GAZ_INFO", "Found ${plan.planID}")
                    return plan
                }
            }
            //Nothing found, return null
            Log.i("GAZ_INFO", "Found nothing")
            return null
        }
    }
}

class PlanCategory constructor(
        val balance: Double,
        val budget: Double,
        val label: String,
        val category: String)
{

}