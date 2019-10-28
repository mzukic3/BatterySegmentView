package com.widget.batterylevelview

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt

class BatteryLevelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var contentHeight: Int = 0
    private var contentWidth: Int = 0
    private var segmentWidth = 0

    var numberOfSegments: Int = DEFAULT_NUMBER_OF_SEGMENTS
        @CheckResult
        get
        set(level) {
            field = level
            invalidate()
        }

    var segmentSpacing: Int = DEFAULT_SEGMENT_SPACING
        @CheckResult
        get
        set(level) {
            field = level
            invalidate()
        }
    var batteryLevel: Int = DEFAULT_BATTERY_LEVEL
        @CheckResult
        get() = field
        set(level) {
            field = when {
                level > 100 -> 100
                level < 0 -> 0
                else -> level
            }
            batteryLevelPaint.color = batteryLevelColor
            invalidate()
        }

    // Paints
    private val backgroundSegmentPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val batteryLevelPaint: Paint = Paint(ANTI_ALIAS_FLAG)


    // Colors
    var batteryLevelColor: Int = DEFAULT_BATTERY_LEVEL_COLOR
        set(@ColorInt color) {
            field = color
            batteryLevelPaint.color = color
            invalidate()
        }




    var backgroundSegmentColor: Int = DEFAULT_BACKGROUND_COLOR
        set(@ColorInt color) {
            field = color
            backgroundSegmentPaint.color = color
            invalidate()
        }




    init {
        parseAttr(attrs)
        /*
        * Initialize all properties
         */
        batteryLevelPaint.apply {
            style = Paint.Style.FILL
            color = batteryLevelColor
        }

        backgroundSegmentPaint.apply {
            style = Paint.Style.FILL
            color = backgroundSegmentColor
        }
    }

    private fun parseAttr(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.BatteryLevelView, 0, 0
        )

        batteryLevel = typedArray.getInteger(
            R.styleable.BatteryLevelView_battery_level,
            DEFAULT_BATTERY_LEVEL
        )
        batteryLevelColor = typedArray.getColor(
            R.styleable.BatteryLevelView_blvLevelColor,
            DEFAULT_BATTERY_LEVEL_COLOR
        )
        backgroundSegmentColor = typedArray.getColor(
            R.styleable.BatteryLevelView_blvSegmentBgColor,
            DEFAULT_BACKGROUND_COLOR
        )
        numberOfSegments = typedArray.getInteger(
            R.styleable.BatteryLevelView_blvSegmentsCount,
            DEFAULT_NUMBER_OF_SEGMENTS
        )
        segmentSpacing = typedArray.getDimension(
            R.styleable.BatteryLevelView_blvSegmentSpacing,
            DEFAULT_SEGMENT_SPACING.toFloat()
        ).toInt()
        typedArray.recycle()
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        contentWidth = width - paddingLeft - paddingRight
        contentHeight = height - paddingTop - paddingBottom
        segmentWidth = (width - segmentSpacing * (numberOfSegments - 1)) / numberOfSegments
    }

    override fun onDraw(canvas: Canvas) {

        for (i in 0..numberOfSegments) {
            drawSegment(canvas, i)
        }
        fillSegmentsWithBatteryLevelColor(canvas, batteryLevel.toDouble())
    }

    private fun drawSegment(canvas: Canvas, position: Int) {
        val rect = Rect()
        rect.set(
            position * (segmentWidth + segmentSpacing),
            0,
            position * (segmentWidth + segmentSpacing) + segmentWidth,
            height
        )
        canvas.drawRect(rect, backgroundSegmentPaint)
    }

    private fun fillSegmentWithBatteryLevelColor(canvas: Canvas, position: Int) {
        val rect = Rect()
        rect.set(
            position * (segmentWidth + segmentSpacing),
            0,
            position * (segmentWidth + segmentSpacing) + segmentWidth,
            height
        )
        canvas.drawRect(rect, batteryLevelPaint)
    }

    private fun fillSegmentsWithBatteryLevelColor(canvas: Canvas, percentage: Double) {
        val oneSegmentPercentage = 100 / numberOfSegments.toDouble()
        val numberOfSegmentsToDrow = percentage / oneSegmentPercentage
        var i = 0
        // Fill segments with battery color
        while (i < numberOfSegmentsToDrow.toInt()) {
            fillSegmentWithBatteryLevelColor(canvas, i)
            i++
        }
        // Fill last segment
        val lastSegmentPercentageToDraw =
            (percentage % oneSegmentPercentage) * numberOfSegments / 100
        val rect = Rect()
        rect.set(
            i * (segmentWidth + segmentSpacing),
            0,
            ((i * (segmentWidth + segmentSpacing) + segmentWidth * lastSegmentPercentageToDraw).toInt()),
            height
        )
        canvas.drawRect(rect, batteryLevelPaint)
    }


    companion object {

        private const val DEFAULT_NUMBER_OF_SEGMENTS = 3
        private const val DEFAULT_SEGMENT_SPACING = 30
        private const val DEFAULT_BATTERY_LEVEL = 50
        private const val DEFAULT_BATTERY_LEVEL_COLOR = Color.GREEN
        private const val DEFAULT_BACKGROUND_COLOR = Color.LTGRAY
    }
}