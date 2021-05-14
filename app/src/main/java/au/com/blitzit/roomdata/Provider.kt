package au.com.blitzit.roomdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import au.com.blitzit.helper.CranstekHelper
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["provider_id", "plan_id"])
data class Provider(
    val provider_id: String,
    val plan_id: String,
    val name: String,
    val abn: String,
    @SerializedName("address_line")
    val addressLine: String?,
    val suburb: String?,
    val state: String?,
    val postcode: String?,
    val email: String?,
    val phone: List<String>?)
{
    fun getABN(): String
    {
        return if(abn.isNullOrEmpty())
            "ABN: N/A"
        else
            "ABN: ${CranstekHelper.formatABNText(abn)}"
    }
    fun getStreetAddress(): String
    {
        return if(addressLine.isNullOrEmpty())
            "N/A"
        else
            addressLine
    }
    fun getSuburbStatePostcode(): String
    {
        var str: String = ""
        if(suburb.isNullOrEmpty() && state.isNullOrEmpty() && postcode.isNullOrEmpty())
            str = "N/A"
        else {
            if (!suburb.isNullOrEmpty())
                str += suburb
            if (!state.isNullOrEmpty())
                str += " $state"
            if (!postcode.isNullOrEmpty())
                str += " $postcode"
        }

        return str
    }
    fun getEmailAddress(): String
    {
        return if(email.isNullOrEmpty())
            "Contact Email: N/A"
        else
            "Contact Email: $email"
    }
    fun getContactNumber(): String
    {
        return if(phone.isNullOrEmpty())
            "Contact Number: N/A"
        else
            "Contact Number: ${phone[0]}"
    }
}