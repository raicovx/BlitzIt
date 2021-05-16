package au.com.blitzit.helper

import android.util.Log
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

    fun getCoreLabels(): List<String>
    {
        return listOf("Daily Activities", "Consumables", "Social Community and Civic Participation")
    }

    fun formatDate(value: String): String
    {
        val inFormatter = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = inFormatter.parse(value)
        val outFormatter = SimpleDateFormat("dd-MM-yyyy")
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

    fun convertToRegisterDate(value: String): String
    {
        val inFormatter = SimpleDateFormat("dd-MM-yyyy")
        val date: Date = inFormatter.parse(value)
        val outFormatter = SimpleDateFormat("yyyy-MM-dd")
        return outFormatter.format(date)
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

    fun formatMobileNumberText(string: String): String
    {
        return if(string.length == 10) {
            buildString {
                for(i in string.indices) {
                    append(string[i])
                    if(i == 3 || i == 6)
                        append(' ')
                }
            }
        } else
            string
    }

    fun splitLabelsByComma(label: String): List<String>
    {
        val labels: List<String> = label.split(",", "and")
        var returns: List<String> = emptyList()
        for(i in labels.indices)
        {
            var str: String = labels[i]
            if(str.startsWith(" "))
                str = str.trimStart()
            if(str.last().isWhitespace())
                str = str.trimEnd()
            returns = returns.plus(str)
        }

        return returns
    }

    fun getMonthTitleFromMonthNumber(value: Int): String
    {
        var returnString = "NULL"
        when(value){
            1 -> returnString = "JAN"
            2 -> returnString = "FEB"
            3 -> returnString = "MAR"
            4 -> returnString = "APR"
            5 -> returnString = "MAY"
            6 -> returnString = "JUN"
            7 -> returnString = "JUL"
            8 -> returnString = "AUG"
            9 -> returnString = "SEP"
            10 -> returnString = "OCT"
            11 -> returnString = "NOV"
            12 -> returnString = "DEC"
        }
        return returnString
    }

    fun getMonthNumberFromDateString(value: String): Int
    {
        val inFormatter = SimpleDateFormat("yyyyMM")
        val date: Date = inFormatter.parse(value)
        val outFormatter = SimpleDateFormat("MM")

        var returnValue = 0
        when(outFormatter.format(date)){
            "01" -> returnValue = 1
            "02" -> returnValue = 2
            "03" -> returnValue = 3
            "04" -> returnValue = 4
            "05" -> returnValue = 5
            "06" -> returnValue = 6
            "07" -> returnValue = 7
            "08" -> returnValue = 8
            "09" -> returnValue = 9
            "10" -> returnValue = 10
            "11" -> returnValue = 11
            "12" -> returnValue = 12
        }
        return returnValue
    }
}