package au.com.blitzit.roomdata

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

data class PurposeWithCategories(
    @Embedded
    val purpose: Purpose,
    @Relation(
        parentColumn = "name",
        entityColumn = "purpose"
    )
    val categories: List<Category>
)