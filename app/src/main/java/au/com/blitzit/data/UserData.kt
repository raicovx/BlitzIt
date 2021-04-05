package au.com.blitzit.data

import android.util.Log

class UserData constructor(
        val ndis_number: Int,
        val first_name: String,
        val last_name: String,
        val mobile: String,
        val address_line: String,
        val suburb: String,
        val postcode: Int,
        val state: String,
        val date_of_birth: String,
        var plans: Array<UserPlan>?)
{
    fun getFullName(): String
    {
        return "$first_name $last_name"
    }

    fun findActivePlan() : UserPlan?
    {
        if(!plans.isNullOrEmpty()) {
            for (i in plans!!.indices) {
                if (plans!![i].status == "Active") {
                    //Log.i("GAZ_INFO", "Found active plan: ${plans!![i].planID}")
                    return plans!![i]
                }
            }
        }
        //Nothing found, return null
        //Log.i("GAZ_INFO", "Found nothing")
        return null
    }
}