package au.com.blitzit.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import au.com.blitzit.R
import au.com.blitzit.helper.CranstekHelper
import kotlin.math.roundToInt

class GraphView(context: Context, attributeSet: AttributeSet): View(context, attributeSet)
{
    private val dataSet = mutableListOf<DataPoint>()
    private var averageTargetSpend: Int = 0
    private var yMax = 0
    private var graphWidthOffset = 0
    private var graphHeightOffset = 0
    private var graphInThousands = false
    private val maxAmountSteps = 10
    private val maxDateSteps = 8
    private var amountStepsArray = IntArray(0)

    private val blitzItGreen = ContextCompat.getColor(context, R.color.blitz_it_green)
    private val blitzItOrange = ContextCompat.getColor(context, R.color.blitz_it_orange)
    private val blitzItRed = ContextCompat.getColor(context, R.color.blitz_it_red)
    private val textFont = ResourcesCompat.getFont(context, R.font.avenir)

    private var graphRect= Rect()
    private var amountRect = Rect()
    private var monthRect = Rect()

    private var graphContentPadding = 50
    private var graphContentBarPadding = 75

    private val dataPointPaintAverage = Paint().apply{
        color = blitzItOrange
        strokeWidth = 9f
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = blitzItGreen
    }

    private val dataPointFillPaintAverage = Paint().apply {
        color = blitzItOrange
    }

    private val dataPointFillPaintOver = Paint().apply {
        color = blitzItRed
    }

    private val dataPointLinePaintAverage = Paint().apply {
        color = blitzItOrange
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 2f
    }
    private val axisSecondaryLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.grey)
        strokeWidth = 1f
    }

    private val monthTextPaint: TextPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 10 * resources.displayMetrics.density
        typeface = textFont
        color = Color.WHITE
    }

    private val amountTextPaint: TextPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 10 * resources.displayMetrics.density
        typeface = textFont
        color = Color.WHITE
    }

    fun setGraphOffset(widthOffset: Int, heightOffset: Int)
    {
        graphWidthOffset = widthOffset
        graphHeightOffset = heightOffset
    }

    private val displayAmountTextPaint: TextPaint = TextPaint().apply {
        isAntiAlias = true
        textSize = 10 * resources.displayMetrics.density
        typeface = Typeface.create(textFont, Typeface.BOLD)
        color = Color.WHITE

    }

    fun setData(newDataSet: List<DataPoint>, averageTarget: Int)
    {
        //Create the display rectangles
        amountRect = Rect(0, 0 + graphContentPadding, graphWidthOffset, height - graphHeightOffset + graphContentPadding)
        monthRect = Rect(0, height - graphHeightOffset, width, height)
        graphRect = Rect(amountRect.right, amountRect.top, width, monthRect.top)

        //Check that our dataset isn't above our max
        val refinedDataSet: List<DataPoint> = if(newDataSet.size > maxDateSteps) {
            val dropSize = newDataSet.size - maxDateSteps
            newDataSet.drop(dropSize)
        } else
            newDataSet

        //Create our max values
        yMax = refinedDataSet.maxByOrNull { it.yVal }?.yVal ?: 0

        //Ensure our highest graph point is not lower then then average value
        if(averageTarget > yMax)
            yMax = averageTarget

        //Check if our graph is in the thousands and round it up to the nearest whole value
        if(yMax > 1000) {
            yMax = ((yMax + 999) / 1000.0).roundToInt() * 1000
            graphInThousands = true
        } else {
            yMax = ((yMax + 99) / 100.0).roundToInt() * 100
            graphInThousands = false
        }

        //If number is not even
        if(yMax % maxAmountSteps != 0)
        {
            //Add another whole number to make it even
            val additional = if(graphInThousands) 1000 else 100
            yMax += additional
        }

        //Create our amounts array
        val yStep = yMax / maxAmountSteps
        amountStepsArray = IntArray(maxAmountSteps) { yStep * (it + 1) }
        amountStepsArray.sort()

        dataSet.clear()
        dataSet.addAll(refinedDataSet)
        averageTargetSpend = averageTarget
        invalidate()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        //Vertical Line
        canvas.drawLine(graphRect.left.toFloat(), 0f, graphRect.left.toFloat(), graphRect.bottom.toFloat(), axisLinePaint)
        //Horizontal Line
        canvas.drawLine(graphRect.left.toFloat(), graphRect.bottom.toFloat(), graphRect.right.toFloat(), graphRect.bottom.toFloat(), axisLinePaint)

        //Draw amounts
        for(i: Int in amountStepsArray)
        {
            val amountText = "$${i}"
            canvas.drawText(amountText, 0f, i.getY(), amountTextPaint)

            //Draw amount frames
            canvas.drawLine(graphRect.left.toFloat(), i.getY(), graphRect.right.toFloat(), i.getY(), axisSecondaryLinePaint)
        }

        for(i in dataSet.indices)
        {
            //Draw month
            canvas.drawText(dataSet[i].month, i.getX() - (getMonthTextWidth() / 1.5).toFloat(), height.toFloat(), monthTextPaint)

            val x = i.getX()
            val realY = dataSet[i].yVal.getY()
            val fillPaint = if(dataSet[i].yVal > averageTargetSpend) dataPointFillPaintOver else dataPointFillPaint

            canvas.drawRect(-25f + x, realY, x + 25f, graphRect.bottom.toFloat(), fillPaint)

            //Draw amount
            val amountText = CranstekHelper.convertToCurrency(dataSet[i].amount)
            val yPos = if(i % 2 == 0) realY - 10 else realY - 50 //Sets the texts to be higher or lower slightly to avoid overlapping
            canvas.drawText(amountText, i.getX() - (getAmountTextWidth(amountText) / 2).toFloat(), yPos, displayAmountTextPaint)

            //Average
            val averageY = averageTargetSpend.getY()
            canvas.drawCircle(x, averageY, 4f, dataPointPaintAverage)
            canvas.drawCircle(x, averageY, 4f, dataPointFillPaintAverage)

            if(i < dataSet.size - 1)
            {
                val startX = i.getX()
                val endX = (i + 1).getX()

                canvas.drawLine(startX, averageY, endX, averageY, dataPointLinePaintAverage)
            }
            //Draw the start and end lines
            if(i == 0)
            {
                val startX = graphRect.left.toFloat()
                val endX = 1.getX()
                canvas.drawLine(startX, averageY, endX, averageY, dataPointLinePaintAverage)
            }
            else if(i == dataSet.size - 1)
            {
                val startX = i.getX()
                val endX = graphRect.right.toFloat()
                canvas.drawLine(startX, averageY, endX, averageY, dataPointLinePaintAverage)
            }
        }
    }

    private fun Int.getX(): Float {
        val position = toFloat() / (dataSet.size - 1)
        return ((graphRect.width() - (graphContentBarPadding * 2)) * position) + (graphRect.left + graphContentBarPadding)
    }
    private fun Int.getY() = graphRect.height() - (toFloat() / yMax * graphRect.height().toFloat()) + graphContentPadding
    private fun getMonthTextWidth() = monthTextPaint.measureText("###").roundToInt()
    private fun getAmountTextWidth(text: String) = displayAmountTextPaint.measureText(text).roundToInt()
}
