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
import kotlin.math.*


class ClockCustomView(context: Context, attrs: AttributeSet? = null): View(context, attrs){

    //Декоративные параметры
    private val strokePaint: Paint
    private val dialBackgroundPaint: Paint
    private val innerDialCirclePaint: Paint
    private val sectionDialPaint: Paint
    private val numbersPaint: Paint
    private val arrowPaint: Paint
    private val middleCirclePaint: Paint

    private val customFont: Int
    private val dialBackgroundColor: Int
    private val borderColor: Int
    private val hourArrowColor: Int
    private val minuteArrowColor: Int
    private val secondArrowColor: Int
    private val numbersColor: Int
    private val centerCircleColor: Int
    private val permanentSecondArrow: Boolean


    //Параметры для отрисовки элементов
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f
    private var numberRadius: Float = 0f
    private var hoursArrowWidth: Float = 0f
    private var numbersSize: Float = 0f
    private var innerRadius: Float = 0f
    private var borderWidth: Float = 0f
    private var numberRadiusCoefficient: Float = 0f

    //Параметры для замены арабских цифр на римские
    private val romanNumbers = arrayOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII")
    private val roman: Boolean

    //Инициализация настраиваемых параметров
    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.ClockCustomView, 0, 0).apply {
            try{
                dialBackgroundColor = getColor(R.styleable.ClockCustomView_backgroundColor, Color.WHITE)
                borderColor = getColor(R.styleable.ClockCustomView_borderColor, Color.BLACK)
                hourArrowColor = getColor(R.styleable.ClockCustomView_hourArrowColor, Color.BLACK)
                minuteArrowColor = getColor(R.styleable.ClockCustomView_minuteArrowColor, Color.BLACK)
                secondArrowColor = getColor(R.styleable.ClockCustomView_secondArrowColor, Color.RED)
                numbersColor = getColor(R.styleable.ClockCustomView_nailColor, Color.BLACK)
                roman = getBoolean(R.styleable.ClockCustomView_romanNumbers, false)
                customFont = getResourceId(R.styleable.ClockCustomView_numberFont, -1)
                numberRadiusCoefficient = getFloat(R.styleable.ClockCustomView_numberRadiusCoef, 0.75f)
                centerCircleColor = getColor(R.styleable.ClockCustomView_centerCircleColor, Color.GRAY)
                permanentSecondArrow = getBoolean(R.styleable.ClockCustomView_permanentSecondArrow, true)
            }finally {
                recycle()
            }
        }

        strokePaint = Paint(0).apply {
            color = borderColor
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        dialBackgroundPaint = Paint(0).apply {
            color = dialBackgroundColor
        }

        innerDialCirclePaint = Paint(0).apply {
            color = numbersColor
            style = Paint.Style.STROKE}

        sectionDialPaint = Paint(0).apply {
            color = numbersColor
        }

        numbersPaint = Paint(0).apply {
            color = numbersColor
            if (customFont != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                typeface = resources.getFont(customFont)
            textAlign = Paint.Align.CENTER
        }

        arrowPaint = Paint(0)

        middleCirclePaint = Paint(0).apply {
            color = centerCircleColor
        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        defineParameters()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 300

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width: Int
        val height: Int

        width = when(widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
            if (widthMode == MeasureSpec.AT_MOST){
                width = heightSize
            }
        } else {
            height = width
        }

        setMeasuredDimension(width, height)
    }

    //Определение размеров элемента
    private fun defineParameters(){
        centerX = (width + paddingLeft - paddingRight) / 2f
        centerY = (height + paddingTop - paddingBottom) / 2f
        val radius1 = (width - paddingLeft - paddingRight) / 2
        val radius2 = (height - paddingTop - paddingBottom) / 2
        radius = min(radius1, radius2) * 0.95f
        numberRadius = radius * numberRadiusCoefficient
        innerRadius = radius * 0.88f
        hoursArrowWidth = radius * 0.1f
        numbersSize = radius * 0.2f
        borderWidth = radius * 0.1f

        strokePaint.strokeWidth = borderWidth
        innerDialCirclePaint.strokeWidth = radius * 0.01f
        numbersPaint.textSize = numbersSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawDial(canvas)
        drawNumbers(canvas)
        drawArrows(canvas)
        invalidate()
    }

    //Метод для отрисовки чисел на циферблате
    private fun drawNumbers(canvas: Canvas){
        canvas.apply {
            for (i in 1..12){
                val angle = (i - 15) * 30 * PI / 180
                val inX = (centerX + cos(angle) * numberRadius).toFloat()
                val inY = (centerY + sin(angle) * numberRadius).toFloat() + numbersSize * 0.4f
                val number = if (roman) romanNumbers[i - 1]
                else "$i"
                drawText(number, inX, inY, numbersPaint)
            }
        }
    }

    //Метод отрисовки заднего фона часов и их границ
    private fun drawDial(canvas: Canvas){
        canvas.apply {
            drawCircle(centerX, centerY, radius, dialBackgroundPaint)
            drawCircle(centerX, centerY, radius, strokePaint)
            drawCircle(centerX, centerY, innerRadius, innerDialCirclePaint)

            for (i in 1..60){
                val angle = i * 6 * PI / 180
                val inX = (centerX + cos(angle) * innerRadius).toFloat()
                val inY = (centerY + sin(angle) * innerRadius).toFloat()
                val outX = (centerX + cos(angle) * (radius - borderWidth / 2)).toFloat()
                val outY = (centerY + sin(angle) * (radius - borderWidth / 2)).toFloat()
                drawLine(inX, inY, outX, outY, sectionDialPaint.apply {
                    strokeWidth = if (i % 5 == 0) radius * 0.05f
                    else radius * 0.01f
                })
            }
        }
    }

    //Вспомогательные методы для расчета конца стрелок
    private fun calculateXArrowCoordinate(timeUnit: Float, degreesPerTick: Int)
    = cos((timeUnit - 15) * degreesPerTick * PI / 180)

    private fun calculateYArrowCoordinate(timeUnit: Float, degreesPerTick: Int)
    = sin((timeUnit - 15) * degreesPerTick * PI / 180)

    //Метод отрисовки одной стрелки
    private fun drawArrow(canvas: Canvas, timeUnit: Float, degreesPerTick: Int, widthCoefficient: Float = 1f,
                          heightCoefficient: Float = 1f, arrowColor: Int = Color.BLACK){
        canvas.drawLine(centerX, centerY,
            (centerX + calculateXArrowCoordinate(timeUnit, degreesPerTick) * numberRadius * heightCoefficient).toFloat(),
            (centerY + calculateYArrowCoordinate(timeUnit, degreesPerTick) * numberRadius * heightCoefficient).toFloat(),
            arrowPaint.apply {
                color = arrowColor
                strokeWidth = hoursArrowWidth * widthCoefficient
            })
    }

    //Метод, в котором определяется текущее время и вызывается метод для отрисовки часовой, минутной и секундной стрелок
    private fun drawArrows(canvas: Canvas){
        val currentTime = getInstance()
        val millis = currentTime[MILLISECOND]
        val seconds = currentTime[SECOND]
        val minutes = currentTime[MINUTE]
        val hours = currentTime[HOUR]
        val secondPart = if (permanentSecondArrow) millis / 1000f
        else 0f

        drawArrow(canvas, hours + minutes / 60f, 30, heightCoefficient = 0.8f, arrowColor = hourArrowColor)
        drawArrow(canvas, minutes + seconds / 60f, 6, widthCoefficient = 0.5f, arrowColor = minuteArrowColor)
        drawArrow(canvas, seconds + secondPart, 6, widthCoefficient = 0.25f, arrowColor = secondArrowColor)

        canvas.drawCircle(centerX, centerY, radius * 0.1f, middleCirclePaint)
    }
}

