package au.com.blitzit.helper

import android.util.Patterns
import android.widget.TextView
import au.com.blitzit.R
import com.app.progresviews.ProgressWheel
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object CranstekHelper
{
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun formatDate(value: String): String
    {
        val inFormatter = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inFormatter.parse(value)
        val outFormatter = SimpleDateFormat("yyyy-MM-dd")
        return outFormatter.format(date)
    }

    //Sets the radial wheel up
    fun setRadialWheel(radialWheel: ProgressWheel, maxValue: Double, curValue: Double)
    {
        //calculate percentages
        val percent = curValue / maxValue * 100
        val radialPercent = (360 * percent) / 100
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING

        //Set the progress wheel
        radialWheel.setPercentage(df.format(radialPercent).toInt())
        radialWheel.setStepCountText(df.format(percent).toString() + "%")
    }

    fun convertToCurrency(value: Double): String
    {
        val currencyDF = DecimalFormat("###,###,###.##")
        return "$" + currencyDF.format(value)
    }

    fun isDateAfter(dateString: String, checkTo: String): Boolean
    {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val date1: Date = sdf.parse(dateString)
        val date2: Date = sdf.parse(checkTo)

        return date1.after(date2)
    }

    //Sets the active display of a plan (text view)
    fun setPlanStatusDisplay(tv: TextView, status: String)
    {
        when(status)
        {
            "Active" -> {
                tv.text = "Active"
                tv.setBackgroundResource(R.drawable.active_display)
            }
            "Expired" -> {
                tv.text = "Expired"
                tv.setBackgroundResource(R.drawable.expired_display)
            }
            "Archived" -> {
                tv.text = "Archived"
                tv.setBackgroundResource(R.drawable.archived_display)
            }
        }
    }

    fun formatABNText(abnString: String): String
    {
        return if(abnString.length == 11) {
            buildString {
                for(i in abnString.indices) {
                    append(abnString[i])
                    if(i == 1 || i == 4 || i == 7)
                        append(' ')
                }
            }
        } else
            abnString
    }
}