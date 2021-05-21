package au.com.blitzit.data

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class EditProfileDataObject(@SerializedName("contact_number")
                                 val contactNumber: String,
                                 @SerializedName("participant_email")
                                 val participantEmail: String,
                                 @SerializedName("statements_email")
                                 val statementEmail: String,
                                 @SerializedName("preferred_contact_method")
                                 val contactMethod: String,
                                 val notes: String)
{
    fun toJsonObject(): String
    {
        val gson = GsonBuilder().setPrettyPrinting().create()
        Log.i("GAZ_PROFILE_EDIT_POST", gson.toJson(this))
        return gson.toJson(this)
    }
}