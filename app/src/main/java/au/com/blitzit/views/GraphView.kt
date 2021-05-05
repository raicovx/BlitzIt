package au.com.blitzit.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import au.com.blitzit.R

class GraphView(context: Context, attributeSet: AttributeSet): View(context, attributeSet)
{
    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0
    private var graphWidthOffset = 0
    private var graphHeightOffset = 0

    private val blitzItBlue = ContextCompat.getColor(context, R.color.blitz_it_medium_blue)

    private val dataPointPaint = Paint().apply{
        color = blitzItBlue
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = blitzItBlue
    }

    private val intervals = FloatArray(2){1f}
    private val dataPointLinePaint = Paint().apply {
        color = blitzItBlue
        strokeWidth = 2f
        style = Paint.Style.STROKE
        isAntiAlias = true
        DashPathEffect(intervals, 0f)
    }

    private val axisLinePaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 3f
    }

    fun setGraphOffset(widthOffset: Int, heightOffset: Int)
    {
        graphWidthOffset = widthOffset
        graphHeightOffset = heightOffset
    }

    fun setData(newDataSet: List<DataPoint>)
    {
        xMin = newDataSet.minByOrNull { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxByOrNull { it.xVal }?.xVal ?: 0
        yMin = newDataSet.minByOrNull { it.yVal }?.yVal ?: 0
        yMax = newDataSet.maxByOrNull { it.yVal }?.yVal ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)
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

        dataSet.forEachIndexed{ index, currentDataPoint ->
            val realX = currentDataPoint.xVal.toRealX() + graphWidthOffset
            val realY = currentDataPoint.yVal.toRealY()

            if(index < dataSet.size - 1)
            {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX() + graphWidthOffset
                val startY = currentDataPoint.yVal.toRealY()
                val endX = nextDataPoint.xVal.toRealX() + graphWidthOffset
                val endY = nextDataPoint.yVal.toRealY()

                canvas.drawLine(startX, startY, endX, endY, dataPointLinePaint)
            }

            canvas.drawCircle(realX, realY, 7f, dataPointFillPaint)
            canvas.drawCircle(realX, realY, 7f, dataPointPaint)
        }

        //canvas.drawLine(0f, 0f, 0f, height.toFloat(), axisLinePaint)
        //canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), axisLinePaint)
    }

    private fun Int.toRealX() = toFloat() / xMax * getGraphWidth()
    private fun Int.toRealY() = toFloat() / yMax * getGraphHeight()
    private fun getGraphWidth() = width - graphWidthOffset - 20
    private fun getGraphHeight() = height - graphHeightOffset
}