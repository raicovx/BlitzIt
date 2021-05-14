package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["plan_id", "purpose", "label"])
data class Category(
    val plan_id: String,
    val purpose: String,
    val label: String,
    val category: String,
    val budget: Double,
    val balance: Double,
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
{
    fun checkMonthlySpendOnTrack(): Boolean
    {
        return averageTargetMonth > averageSpendMonth
    }
}