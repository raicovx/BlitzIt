package au.com.blitzit.responses

import com.google.gson.annotations.SerializedName

data class GenericSupportCoordinatorResponse(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String)