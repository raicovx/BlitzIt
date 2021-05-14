package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Plan(
    @PrimaryKey
    val plan_id: String,
    val ndis_number: Int,
    val status: String,
    val plan_start_date: String,
    val plan_end_date: String)