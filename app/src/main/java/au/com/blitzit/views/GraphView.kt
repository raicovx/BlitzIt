package au.com.blitzit.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import au.com.blitzit.R
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

    private val dataPointPaint = Paint().apply{
        color = blitzItGreen
        strokeWidth = 9f
        style = Paint.Style.STROKE
    }

    private val dataPointPaintOver = Paint().apply{
        color = blitzItRed
        strokeWidth = 9f
        style = Paint.Style.STROKE
    }

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

    private val intervals = FloatArray(2){100f}
    private val dataPointLinePaint = Paint().apply {
        color = blitzItGreen
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
        DashPathEffect(intervals, 0f)
    }
    private val dataPointLinePaintOver = Paint().apply {
        color = blitzItRed
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
        DashPathEffect(intervals, 0f)
    }
    private val dataPointLinePaintAverage = Paint().apply {
        color = blitzItOrange
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
        DashPathEffect(intervals, 0f)
    }

    private val axisLinePaint = Paint().apply {
        color = Color.WHITE
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
        textSize = 9 * resources.displayMetrics.density
        typeface = textFont
        color = Color.WHITE
    }

    fun setGraphOffset(widthOffset: Int, heightOffset: Int)
    {
        graphWidthOffset = widthOffset
        graphHeightOffset = heightOffset
    }

    fun setData(newDataSet: List<DataPoint>, averageTarget: Int)
    {
        //Check that our dataset isn't above our max
        Log.i("GAZ_TEST", "newdataset size: ${newDataSet.size}")
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

        //Frame lines
        for(i in 0 until dataSet.size)
        {
            canvas.drawLine(i.toRealX() + graphWidthOffset, 0f, i.toRealX() + graphWidthOffset, getGraphHeight().toFloat(), axisLinePaint)
        }

        //Draw amounts
        for(i: Int in amountStepsArray)
        {
            val amountText = "$${i}"
            canvas.drawText(amountText, 0f, i.toRealY(), amountTextPaint)
        }

        for(i in dataSet.indices)
        {
            //Draw month
            canvas.drawText(dataSet[i].month, i.toRealX() - (getMonthTextWidth() / 2) + graphWidthOffset, height.toFloat(), monthTextPaint)

            val realX = i.toRealX() + graphWidthOffset
            val realY = dataSet[i].yVal.toRealY()
            val fillPaint = if(dataSet[i].yVal > averageTargetSpend) dataPointFillPaintOver else dataPointFillPaint
            val circlePaint = if(dataSet[i].yVal > averageTargetSpend) dataPointPaintOver else dataPointPaint

            if(i < dataSet.size - 1)
            {
                val nextDataPoint = dataSet[i + 1]
                val startX = i.toRealX() + graphWidthOffset
                val startY = dataSet[i].yVal.toRealY()
                val endX = (i + 1).toRealX() + graphWidthOffset
                val endY = nextDataPoint.yVal.toRealY()

                val nextPaint = if(nextDataPoint.yVal > averageTargetSpend) dataPointLinePaintOver else dataPointLinePaint
                canvas.drawLine(startX, startY, endX, endY, nextPaint)
            }
            canvas.drawCircle(realX, realY, 9f, circlePaint)
            canvas.drawCircle(realX, realY, 9f, fillPaint)

            //Average
            val averageY = averageTargetSpend.toRealY()
            canvas.drawCircle(realX, averageY, 9f, dataPointPaintAverage)
            canvas.drawCircle(realX, averageY, 9f, dataPointFillPaintAverage)

            if(i < dataSet.size - 1)
            {
                val startX = i.toRealX() + graphWidthOffset
                val endX = (i + 1).toRealX() + graphWidthOffset

                canvas.drawLine(startX, averageY, endX, averageY, dataPointLinePaintAverage)
            }
        }
    }

    private fun Int.toRealX() = toFloat() / (dataSet.size - 1) * getGraphWidth()
    private fun Int.toRealY() = getGraphHeight() - (toFloat() / yMax * getGraphHeight()) + 35
    private fun getGraphWidth() = width - graphWidthOffset - 40
    private fun getGraphHeight() = height - graphHeightOffset
    private fun getMonthTextWidth() = monthTextPaint.measureText("###").roundToInt()
    private fun getAmountTextHeight(): Int {
        val bounds = Rect()
        amountTextPaint.getTextBounds(yMax.toString(), 0, yMax.toString().length, bounds)
        return bounds.height()
    }
}