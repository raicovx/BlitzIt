package au.com.blitzit.helper

import android.util.Patterns
import com.app.progresviews.ProgressWheel
import java.math.RoundingMode
import java.text.DecimalFormat
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
}