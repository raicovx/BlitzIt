package au.com.blitzit.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import au.com.blitzit.R

class RadialProgressBar(context: Context, attrs: AttributeSet): View(context, attrs)
{
    //Sizes
    private var mBarWidth = 24f
    private var mTextSize = 48f

    //Colours
    private var mProgressColour = Color.BLUE
    private var mRimColour = -0x11111112
    private var mTextColour = Color.BLUE

    //Paints
    private var mCirclePaint = Paint()
    private var mBarPaint = Paint()
    private var mTextPaint = TextPaint()

    //String
    private var mPercentageText: String = "100%"

    //Rectangles
    private var mRimBounds = RectF()
    private var mProgressBounds = RectF()

    private var mRadialPercentage = 60

    private var mTextHeight = 0f
    private var mTextWidth = 0f

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RadialProgressBar,
            0,
            0).apply {
                try {
                    mProgressColour = getColor(R.styleable.RadialProgressBar_progressBarColour, Color.BLUE)
                    mTextColour = getColor(R.styleable.RadialProgressBar_progressTextColour, Color.BLUE)
                    mTextSize = getDimension(R.styleable.RadialProgressBar_progressTextSize, 48f)
                    mBarWidth = getDimension(R.styleable.RadialProgressBar_progressBarWidth, 24f)
                    mRimColour = getColor(R.styleable.RadialProgressBar_progressBarRimColour, -0x11111112)
                } finally {
                    recycle()
                }
        }

    }

    private fun setupPaints()
    {
        mCirclePaint.color = mRimColour
        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeWidth = mBarWidth

        mBarPaint.color = mProgressColour
        mBarPaint.isAntiAlias = true
        mBarPaint.style = Paint.Style.STROKE
        mBarPaint.strokeWidth = mBarWidth

        mTextPaint.color = mTextColour
        mTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        mTextPaint.textSize = mTextSize

        mTextWidth = mTextPaint.measureText(mPercentageText)
        mTextHeight = mTextPaint.fontMetrics.bottom
    }

    private fun calculateBounds()
    {
        //Get the smaller value
        val minValue = minOf(width, height)

        //Get the offset value using the smaller of width or height
        val xOffset = width - minValue
        val yOffset = height - minValue

        //Calculate the paddings
        val paddingLeft = this.paddingLeft.toFloat() + xOffset / 2
        val paddingTop = this.paddingTop.toFloat() + yOffset / 2
        val paddingRight = this.paddingRight.toFloat() + xOffset / 2
        val paddingBottom = this.paddingBottom.toFloat() + yOffset / 2

        mRimBounds = RectF(
            paddingLeft + mBarWidth,
            paddingTop + mBarWidth,
            width.toFloat() - paddingRight - mBarWidth,
            height.toFloat() - paddingBottom - mBarWidth)
        mProgressBounds = RectF(
            paddingLeft + mBarWidth,
            paddingTop + mBarWidth,
            width.toFloat() - paddingRight - mBarWidth,
            height.toFloat() - paddingBottom - mBarWidth)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh)

        setupPaints()
        calculateBounds()

        invalidate()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        canvas.drawArc(mRimBounds, 0f, 360f, false, mCirclePaint)
        canvas.drawArc(mProgressBounds, -90f, mRadialPercentage.toFloat(), false, mBarPaint)

        //Draw text at center minus half its width
        canvas.drawText(mPercentageText,
            (width / 2) - (mTextWidth / 2),
            (height / 2) + mTextHeight,
            mTextPaint)
    }

    /***
     * Sets the percentage
     * percent - 0-100 range
     */
    fun setPercentage(percent: Int)
    {
        val value = when{
            percent < 0 -> 0
            percent > 100 -> 100
            else -> percent
        }

        //Calculate radial percentage
        mRadialPercentage = (360 * value) / 100
        val strValue = "${value}%"
        mPercentageText = strValue

        calculateBounds()
        invalidate()
    }

    fun setProgressBarColour(progressBarColour: Int, progressTextColour: Int, resetRimColour: Boolean)
    {
        mProgressColour = progressBarColour
        mTextColour = progressTextColour
        if(resetRimColour)
            mRimColour = -0x11111112

        invalidate()
    }
}