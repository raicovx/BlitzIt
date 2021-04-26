package au.com.blitzit.data

import com.google.gson.annotations.SerializedName

data class SupportCoordinator (@SerializedName("first_name")
                               val firstName: String,
                               @SerializedName("last_name")
                               val lastName: String,
                               val email: String)