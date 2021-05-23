package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.lang.StringBuilder

@Entity
data class Participant(
    var user_id: Long,
    @PrimaryKey
    @SerializedName("ndis_number")
    val ndisNumber: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val mobile: String?,
    @SerializedName("address_line")
    val addressLine: String?,
    val suburb: String?,
    val postcode: Int?,
    val state: String?,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("statement_email")
    val statementEmails: List<String>)
{
    fun getFullName(): String = "$firstName $lastName"

    fun handleMobile(): String
    {
        return if(mobile.isNullOrEmpty())
            "N/A"
        else
            mobile
    }

    fun handleAddressLine(): String
    {
        return if(addressLine.isNullOrEmpty())
            "N/A"
        else
            addressLine
    }

    fun handleSuburb(): String
    {
        return if(suburb.isNullOrEmpty())
            "N/A"
        else
            suburb
    }

    fun handlePostCode(): String
    {
        return if(postcode == 0)
            "N/A"
        else
            postcode.toString()
    }

    fun handleState(): String
    {
        return if(state.isNullOrEmpty())
            "N/A"
        else
            state
    }
}