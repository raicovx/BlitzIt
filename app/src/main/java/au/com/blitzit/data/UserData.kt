package au.com.blitzit.data

import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlin.math.log

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
        var plans: Array<UserPlan>?)
{
    fun getFullName(): String
    {
        return "$first_name $last_name"
    }

    fun findActivePlan() : UserPlan?
    {
        if(!plans.isNullOrEmpty()) {
            for (i in plans!!.indices) {
                if (plans!![i].status == "Active") {
                    Log.i("GAZ_INFO", "Found active plan: ${plans!![i].planID}")
                    return plans!![i]
                }
            }
        }
        //Nothing found, return null
        Log.i("GAZ_INFO", "Found nothing")
        return null
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
        val planParts: Array<PlanParts>)
{
    public fun getPartCategories(): List<String>
    {
        var categories: List<String> = listOf()
        for(part: PlanParts in planParts)
        {
            if(!categories.contains(part.category)) {
                categories = categories.plus(part.category)
            }
        }

        return categories
    }

    public fun getPartByCategory(category: String): List<PlanParts>
    {
        var parts: List<PlanParts> = listOf()
        for(part: PlanParts in planParts)
        {
            if(part.category == category)
                parts = parts.plus(part)
        }

        return parts
    }
}

class PlanParts constructor(
        val balance: Double,
        val budget: Double,
        val label: String,
        val category: String)
{

}