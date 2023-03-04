package com.example.customviewclock.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.example.customviewclock.R
import java.util.Calendar.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class ClockCustomView(context: Context, attrs: AttributeSet? = null): View(context, attrs){

    private val strokePaint: Paint
    private val circlesInsteadofNumbersPaint: Paint
    private val customFont: Int
    private val nailBackgroundColor: Int
    private val borderColor: Int
    private val hourArrowColor: Int
    private val minuteArrowColor: Int
    private val secondArrowColor: Int
    private val numbersColor: Int
    private val centerCircleColor: Int

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f
    private var numberRadius: Float = 0f
    private var hoursArrowWidth: Float = 0f
    private var numbersSize: Float = 0f
    private var innerRadius: Float = 0f
    private var borderWidth: Float = 0f
    private var numberRadiusCoef: Float = 0f

    private var firstSecond: Int = -1
    private var switchedSecond: Boolean = false

    private val romanNumbers = arrayOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
    private val roman: Boolean


    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ClockCustomView, 0, 0).apply {
            try{
                nailBackgroundColor = getColor(R.styleable.ClockCustomView_backgroundColor, Color.WHITE)
                borderColor = getColor(R.styleable.ClockCustomView_borderColor, Color.BLACK)
                hourArrowColor = getColor(R.styleable.ClockCustomView_hourArrowColor, Color.BLACK)
                minuteArrowColor = getColor(R.styleable.ClockCustomView_minuteArrowColor, Color.BLACK)
                secondArrowColor = getColor(R.styleable.ClockCustomView_secondArrowColor, Color.RED)
                numbersColor = getColor(R.styleable.ClockCustomView_nailColor, Color.BLACK)
                roman = getBoolean(R.styleable.ClockCustomView_romanNumbers, false)
                customFont = getResourceId(R.styleable.ClockCustomView_numberFont, -1)
                numberRadiusCoef = getFloat(R.styleable.ClockCustomView_numberRadiusCoef, 0.75f)
                centerCircleColor = getColor(R.styleable.ClockCustomView_centerCircleColor, Color.GRAY)
            }finally {
                recycle()
            }
        }

        strokePaint = Paint(0).apply {
            color = borderColor
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        circlesInsteadofNumbersPaint = Paint(0).apply {
            color = Color.GRAY
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }
    }

//    override fun layout(l: Int, t: Int, r: Int, b: Int) {
//        super.layout(l, t, r, b)
//        defineParametersOfClock()
//    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
//        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
//
//        setMeasuredDimension(
//            measureDimension(desiredWidth, widthMeasureSpec),
//            measureDimension(desiredHeight, heightMeasureSpec)
//        )
//    }
//
//    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
//        var result: Int
//        val specMode = MeasureSpec.getMode(measureSpec)
//        val specSize = MeasureSpec.getSize(measureSpec)
//        if (specMode == MeasureSpec.EXACTLY) {
//            result = specSize
//        } else {
//            result = desiredSize
//            if (specMode == MeasureSpec.AT_MOST) {
//                result = Math.min(result, specSize)
//            }
//        }
//
//        return result
//    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        defineParametersOfClock()
    }

    private fun defineParametersOfClock(){
        centerX = width / 2f
        centerY = height / 2f
        radius = min(centerX, centerY) * 0.95f
        numberRadius = radius * numberRadiusCoef
        innerRadius = radius * 0.88f
        hoursArrowWidth = radius * 0.1f
        numbersSize = radius * 0.2f
        borderWidth = radius * 0.1f
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawDial(canvas)
        drawNumbers(canvas)
        drawArrows(canvas)
        //postInvalidateDelayed(100)
        invalidate()
    }

    private fun drawNumbers(canvas: Canvas?){
        canvas?.apply {
            for (i in 1..12){
                val angle = (i - 15) * 30 * PI / 180
                val inX = (centerX + cos(angle) * numberRadius).toFloat()
                val inY = (centerY + sin(angle) * numberRadius).toFloat() + numbersSize * 0.4f
                val number = if (roman) romanNumbers[i - 1]
                else "$i"
                drawText(number, inX, inY, Paint().apply {
                    color = numbersColor
                    if (customFont != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        typeface = resources.getFont(customFont)
                    textSize = numbersSize
                    textAlign = Paint.Align.CENTER
                    rotation = angle.toFloat()
                })
            }
        }
    }

    private fun drawDial(canvas: Canvas?){
        canvas?.apply {
            drawCircle(centerX, centerY, radius, Paint().apply { color = nailBackgroundColor })
            drawCircle(centerX, centerY, radius, strokePaint.apply { strokeWidth = borderWidth})
            drawCircle(centerX, centerY, innerRadius, Paint().apply {
                strokeWidth = radius * 0.01f
                color = numbersColor
                style = Paint.Style.STROKE})
            for (i in 1..60){
                val angle = i * 6 * PI / 180
                val inX = (centerX + cos(angle) * innerRadius).toFloat()
                val inY = (centerY + sin(angle) * innerRadius).toFloat()
                val outX = (centerX + cos(angle) * (radius - borderWidth / 2)).toFloat()
                val outY = (centerY + sin(angle) * (radius - borderWidth / 2)).toFloat()
                drawLine(inX, inY, outX, outY, Paint().apply {
                    strokeWidth = if (i % 5 == 0) radius * 0.05f
                    else radius * 0.01f
                    color = numbersColor
                })
            }
        }
    }

    private fun calculateXArrowCoordinate(timeUnit: Float, degreesPerTick: Int)
    = cos((timeUnit - 15) * degreesPerTick * PI / 180)

    private fun calculateYArrowCoordinate(timeUnit: Float, degreesPerTick: Int)
    = sin((timeUnit - 15) * degreesPerTick * PI / 180)

    private fun drawArrow(canvas: Canvas?, timeUnit: Float, degreesPerTick: Int, widthCoefficient: Float = 1f,
                          heightCoefficient: Float = 1f, arrowColor: Int = Color.BLACK){
        canvas?.drawLine(centerX, centerY,
            (centerX + calculateXArrowCoordinate(timeUnit, degreesPerTick) * numberRadius * heightCoefficient).toFloat(),
            (centerY + calculateYArrowCoordinate(timeUnit, degreesPerTick) * numberRadius * heightCoefficient).toFloat(),
            Paint().apply {
                color = arrowColor
                strokeWidth = hoursArrowWidth * widthCoefficient
            })
    }

    private fun drawArrows(canvas: Canvas?){
        val currentTime = getInstance()
        val millis = currentTime[MILLISECOND]
        val seconds = currentTime[SECOND]
        val minutes = currentTime[MINUTE]
        val hours = currentTime[HOUR]

        if (firstSecond == -1){
            firstSecond = seconds
        }
        if (seconds != firstSecond){
            switchedSecond = true
        }

        drawArrow(canvas, hours + minutes / 60f, 30, heightCoefficient = 0.8f, arrowColor = hourArrowColor)
        drawArrow(canvas, minutes + seconds / 60f, 6, widthCoefficient = 0.5f, arrowColor = minuteArrowColor)
        drawArrow(canvas, seconds + millis / 1000f, 6, widthCoefficient = 0.25f, arrowColor = secondArrowColor)

        canvas?.drawCircle(centerX, centerY, radius * 0.1f, Paint().apply { color = centerCircleColor })
    }
}

