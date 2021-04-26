package au.com.blitzit.data

import com.google.gson.annotations.SerializedName

data class PrimaryContact (@SerializedName("first_name")
                           val firstName: String,
                           @SerializedName("last_name")
                           val lastName: String,
                           val mobile: String,
                           @SerializedName("address_line")
                           val addressLine: String,
                           val suburb: String,
                           val postcode: String,
                           val state: String,
                           val email: String)