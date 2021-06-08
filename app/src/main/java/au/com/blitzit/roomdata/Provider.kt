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
    val abn: String?,
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
            "N/A"
        else
            CranstekHelper.formatABNText(abn)
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
            "N/A"
        else
            email
    }
    fun getContactNumber(withPadding: Boolean): String
    {
        return if(withPadding) {
            if (phone.isNullOrEmpty())
                "Contact Number: N/A"
            else
                "Contact Number: ${phone[0]}"
        } else {
            if (phone.isNullOrEmpty())
                "N/A"
            else
                phone[0]
        }
    }
}