package au.com.blitzit.helper

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object CranstekHelper
{
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun formatDateForRegistration(value: String): String
    {
        val inFormatter = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inFormatter.parse(value)
        val outFormatter = SimpleDateFormat("yyyy-MM-dd")
        return outFormatter.format(date)
    }
}