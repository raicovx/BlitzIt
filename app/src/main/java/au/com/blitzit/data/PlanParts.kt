package au.com.blitzit.data

import com.google.gson.annotations.SerializedName

class PlanParts constructor(
        val balance: Double,
        val budget: Double,
        val label: String,
        val category: String,
        @SerializedName("average_target_week")
        val averageTargetWeek: Double,
        @SerializedName("remain_target_week")
        val remainTargetWeek: Double,
        @SerializedName("avg_spend_week")
        val averageSpendWeek: Double,
        @SerializedName("average_target_month")
        val averageTargetMonth: Double,
        @SerializedName("remain_target_month")
        val remainTargetMonth: Double,
        @SerializedName("avg_spend_month")
        val averageSpendMonth: Double,
        @SerializedName("estimated_exhaustion_date")
        val estimatedExhaustionDate: String,
        val totals: Map<String, Double>)