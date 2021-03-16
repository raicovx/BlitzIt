package au.com.blitzit.data

class UserData constructor(val ndis_number: Int,
                    val first_name: String,
                    val last_name: String,
                    val mobile: Int,
                    val address_line: String,
                    val suburb: String,
                    val postcode: Int,
                    val state: String,
                    val date_of_birth: String)
{
    fun getFullName(): String
    {
        return "$first_name $last_name"
    }
}