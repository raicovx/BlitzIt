package au.com.blitzit.helper

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import au.com.blitzit.R
import au.com.blitzit.auth.AuthServices
import au.com.blitzit.views.RadialProgressBar
import com.google.gson.Gson
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object CranstekHelper
{
    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun getCoreLabels(): List<String>
    {
        return listOf("Daily Activities", "Consumables", "Social Community and Civic Participation")
    }

    fun formatDate(value: String): String
    {
        val inFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date: Date = inFormatter.parse(value)!!
        val outFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return outFormatter.format(date)
    }

    fun formatDateNoFancyOnInput(value: String): String
    {
        val inFormatter = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        val date: Date = inFormatter.parse(value)!!
        val outFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return outFormatter.format(date)
    }

    //Sets the radial wheel up
    fun setRadialWheel(radialWheel: RadialProgressBar, maxValue: Double, curValue: Double)
    {
        //calculate percentages
        val percent = curValue / maxValue * 100

        //Set the progress wheel
        radialWheel.setPercentage(percent.roundToInt())
    }

    fun convertToCurrency(value: Double): String
    {
        val currencyDF = DecimalFormat("###,###,###.00")
        return "$" + currencyDF.format(value)
    }

    fun convertToRegisterDate(value: String): String
    {
        val inFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val date: Date = inFormatter.parse(value)!!
        val outFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return outFormatter.format(date)
    }

    //Sets the active display of a plan (text view)
    fun setPlanStatusDisplay(context: Context, tv: TextView, status: String)
    {
        when(status)
        {
            "Active" -> {
                tv.text = context.getString(R.string.active)
                tv.setBackgroundResource(R.drawable.active_display)
            }
            "Expired" -> {
                tv.text = context.getString(R.string.expired)
                tv.setBackgroundResource(R.drawable.expired_display)
            }
            "Archived" -> {
                tv.text = context.getString(R.string.archived)
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
        val inFormatter = SimpleDateFormat("yyyyMM", Locale.ENGLISH)
        val date: Date = inFormatter.parse(value)!!
        val outFormatter = SimpleDateFormat("MM", Locale.ENGLISH)

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

    fun handleButtonColours(context: Context, button: Button)
    {
        if(AuthServices.checkPlanStatusExpired())
        {
            button.backgroundTintList = ContextCompat.getColorStateList(context, R.color.old_button)
            button.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun handleCategoryTextViewColours(context: Context, text: TextView)
    {
        if(AuthServices.checkPlanStatusExpired())
            text.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey))
    }

    fun handleHeaderColours(context: Context, linearLayout: LinearLayout)
    {
        if(AuthServices.checkPlanStatusExpired())
            linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey))
    }

    fun handleRadialProgressBarColours(context: Context, bar: RadialProgressBar)
    {
        val colour: Int = ContextCompat.getColor(context, R.color.dark_grey)
        if(AuthServices.checkPlanStatusExpired())
            bar.setProgressBarColour(colour, colour, true)
    }

    fun longInfo(str: String)
    {
        if(str.length > 4000)
        {
            Log.i("Gaz_LONG_STR", str.substring(0,4000))
            longInfo(str.substring(4000))
        } else
        {
            Log.i("Gaz_LONG_STR", str)
        }
    }
}