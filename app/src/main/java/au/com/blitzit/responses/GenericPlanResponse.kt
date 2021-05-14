package au.com.blitzit.responses

import au.com.blitzit.roomdata.Category
import au.com.blitzit.roomdata.Plan
import au.com.blitzit.roomdata.Purpose
import com.google.gson.annotations.SerializedName

data class GenericPlanResponse(
    val status: String,
    @SerializedName("plan_start_date")
    val planStartDate: String,
    @SerializedName("plan_id")
    val planID: String,
    @SerializedName("plan_end_date")
    val planEndDate: String,
    @SerializedName("parts")
    val planParts: Array<GenericPlanPartResponse>)
{
    fun toPlan(ndisNumber: Int): Plan
    {
        return Plan(planID, ndisNumber, status, planStartDate, planEndDate)
    }

    fun getPurposes(): List<Purpose>
    {
        val core = Purpose(planID, "CORE")
        val capacityBuilding = Purpose(planID, "CAPACITY BUILDING")
        val capital = Purpose(planID, "CAPITAL")

        return listOf(core, capacityBuilding, capital)
    }

    fun getCategoriesByString(purpose: Purpose): List<Category>
    {
        var categories = emptyList<Category>()
        for(part: GenericPlanPartResponse in planParts)
        {
            if(part.category == purpose.name)
            {
                val category = Category(
                    planID,
                    purpose.name,
                    part.label,
                    part.category,
                    part.budget,
                    part.balance,
                    part.averageTargetWeek,
                    part.remainTargetWeek,
                    part.averageSpendWeek,
                    part.averageTargetMonth,
                    part.remainTargetMonth,
                    part.averageSpendMonth,
                    part.estimatedExhaustionDate,
                    part.totals)

                categories = categories.plus(category)
            }
        }

        return categories
    }
}
