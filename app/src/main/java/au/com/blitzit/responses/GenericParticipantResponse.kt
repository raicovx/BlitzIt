package au.com.blitzit.responses

import au.com.blitzit.data.PrimaryContact
import au.com.blitzit.data.SupportCoordinator
import au.com.blitzit.data.UserPlan
import au.com.blitzit.roomdata.Participant
import com.google.gson.annotations.SerializedName

data class GenericParticipantResponse(
    @SerializedName("ndis_number")
    val ndisNumber: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val mobile: String,
    @SerializedName("address_line")
    val addressLine: String,
    val suburb: String,
    val postcode: Int,
    val state: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("statement_email")
    val statementEmails: List<String>,
    @SerializedName("primary_contacts")
    val primaryContacts: List<GenericPrimaryContactResponse>,
    @SerializedName("support_coordinator")
    val supportCoordinators: List<GenericSupportCoordinatorResponse>,
    var plans: Array<GenericPlanResponse>)
{
    fun toParticipant(uID: Long): Participant
    {
        return Participant(
            uID,
            ndisNumber,
            firstName,
            lastName,
            mobile,
            addressLine,
            suburb,
            postcode,
            state,
            dateOfBirth,
            statementEmails)
    }

    fun getPrimaryContactList(): List<au.com.blitzit.roomdata.PrimaryContact>
    {
        var pContacts = emptyList<au.com.blitzit.roomdata.PrimaryContact>()
        for(contact: GenericPrimaryContactResponse in primaryContacts)
        {
            val newContact = au.com.blitzit.roomdata.PrimaryContact(
                ndisNumber,
                contact.firstName,
                contact.lastName,
                contact.mobile,
                contact.addressLine,
                contact.suburb,
                contact.postcode,
                contact.state,
                contact.email)
            pContacts = pContacts.plus(newContact)
        }

        return pContacts
    }

    fun getSupportCoordinatorList(): List<au.com.blitzit.roomdata.SupportCoordinator>
    {
        var sContacts = emptyList<au.com.blitzit.roomdata.SupportCoordinator>()
        for(contact: GenericSupportCoordinatorResponse in supportCoordinators)
        {
            val newContact = au.com.blitzit.roomdata.SupportCoordinator(
                ndisNumber,
                contact.firstName,
                contact.lastName,
                contact.email)

            sContacts = sContacts.plus(newContact)
        }

        return sContacts
    }
}