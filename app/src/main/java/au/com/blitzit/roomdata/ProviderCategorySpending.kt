package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProviderCategorySpending(
    @PrimaryKey(autoGenerate = true)
    val providerSpending_id: Long,
    val provider_id: String,
    val label: String,
    val spending: Double)