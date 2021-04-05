package au.com.blitzit.data

import com.google.gson.annotations.SerializedName

class UserPlan constructor(
        val status: String,
        @SerializedName("plan_start_date")
        val planStartDate: String,
        @SerializedName("plan_id")
        val planID: String,
        @SerializedName("plan_end_date")
        val planEndDate: String,
        @SerializedName("parts")
        val planParts: Array<PlanParts>,
        var planInvoices: Array<UserInvoice>?)
{
    fun getPartCategories(): List<String>
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

    fun getPartListByCategory(category: String): List<PlanParts>
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