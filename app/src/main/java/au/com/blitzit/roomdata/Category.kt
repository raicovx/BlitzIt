package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Category(
    @PrimaryKey
    val support_category_number: String,
    val plan_id: String,
    val purpose: String,
    val name: String,
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
    val estimatedExhaustionDate: String
    //val totals: Map<String, Double>,
    //val subLabels: List<String>
)
{
    fun checkMonthlySpendOnTrack(): Boolean
    {
        return averageTargetMonth > averageSpendMonth
    }
}