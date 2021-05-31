package au.com.blitzit.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import au.com.blitzit.R
import kotlin.math.roundToInt

class GraphView(context: Context, attributeSet: AttributeSet): View(context, attributeSet)
{
    private val dataSet = mutableListOf<DataPoint>()
    private var averageTargetSpend: Int = 0
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0
    private var graphWidthOffset = 0
    private var graphHeightOffset = 0

    private var amountSteps = 0

    private var graphInThousands = false

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
        textSize = 10 * resources.displayMetrics.density
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
        xMin = newDataSet.minByOrNull { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxByOrNull { it.xVal }?.xVal ?: 0
        yMin = 0

        yMax = newDataSet.maxByOrNull { it.yVal }?.yVal ?: 0
        if(averageTarget > yMax)
            yMax = averageTarget

        if(yMax > 1000) {
            yMax = ((yMax + 999) / 1000.0).roundToInt() * 1000
            amountSteps = yMax / 1000
            graphInThousands = true
        } else {
            yMax = ((yMax + 99) / 100.0).roundToInt() * 100
            amountSteps = yMax / 100
            graphInThousands = false
        }

        dataSet.clear()
        dataSet.addAll(newDataSet)
        averageTargetSpend = averageTarget
        invalidate()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        //Frame lines
        for(i in 0..xMax)
        {
            canvas.drawLine(i.toRealX() + graphWidthOffset, 0f, i.toRealX() + graphWidthOffset, getGraphHeight().toFloat(), axisLinePaint)
        }

        //Draw amounts
        for(i in 1..amountSteps)
        {
            val amount: Int = if(graphInThousands)
                i * 1000
            else
                i * 100

            canvas.drawText(amount.toString(), 0f, amount.toRealY(), amountTextPaint)
        }

        dataSet.forEachIndexed{ index, currentDataPoint ->
            //Draw month
            canvas.drawText(currentDataPoint.month, currentDataPoint.xVal.toRealX() - (getMonthTextWidth() / 2) + graphWidthOffset, height.toFloat() - (graphHeightOffset / 2.5f), monthTextPaint)

            val realX = currentDataPoint.xVal.toRealX() + graphWidthOffset
            val realY = currentDataPoint.yVal.toRealY()
            val fillPaint = if(currentDataPoint.yVal > averageTargetSpend) dataPointFillPaintOver else dataPointFillPaint
            val circlePaint = if(currentDataPoint.yVal > averageTargetSpend) dataPointPaintOver else dataPointPaint

            if(index < dataSet.size - 1)
            {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX() + graphWidthOffset
                val startY = currentDataPoint.yVal.toRealY()
                val endX = nextDataPoint.xVal.toRealX() + graphWidthOffset
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

            if(index < dataSet.size - 1)
            {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX() + graphWidthOffset
                val endX = nextDataPoint.xVal.toRealX() + graphWidthOffset

                canvas.drawLine(startX, averageY, endX, averageY, dataPointLinePaintAverage)
            }
        }
    }

    private fun Int.toRealX() = toFloat() / xMax * getGraphWidth()
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