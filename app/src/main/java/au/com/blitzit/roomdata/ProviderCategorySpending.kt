package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["provider_id", "label", "spending"])
data class ProviderCategorySpending(
    val plan_id: String,
    val provider_id: String,
    val label: String,
    val spending: Double)