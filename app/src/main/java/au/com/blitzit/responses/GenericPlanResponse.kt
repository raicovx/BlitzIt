package au.com.blitzit.responses

import au.com.blitzit.auth.AuthServices
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
    val planParts: Array<GenericPlanPartResponse>,
    @SerializedName("providers")
    val providerOverviews: Array<GenericProviderSummaryResponse>)
{
    fun toPlan(ndisNumber: Int): Plan
    {
        return Plan(planID, ndisNumber, status, planStartDate, planEndDate)
    }

    fun getPurposes(): List<Purpose>
    {
        val core = Purpose(AuthServices.loggedParticipant.ndisNumber,1, planID, "CORE")
        val capacityBuilding = Purpose(AuthServices.loggedParticipant.ndisNumber, 2, planID, "CAPACITY BUILDING")
        val capital = Purpose(AuthServices.loggedParticipant.ndisNumber, 3, planID, "CAPITAL")

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
                    AuthServices.loggedParticipant.ndisNumber,
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
