package au.com.blitzit.data

import com.amplifyframework.core.model.temporal.Temporal

class UserData constructor(val ndis_number: Int,
                    val first_name: String,
                    val last_name: String,
                    val mobile: Int,
                    val address_line: String,
                    val suburb: String,
                    val postcode: Int,
                    val state: String,
                    val date_of_birth: Temporal.Date)
{

}