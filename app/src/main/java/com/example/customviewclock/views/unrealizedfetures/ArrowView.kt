package com.example.customviewclock.views.unrealizedfetures

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Этот класс остался от альтернативного варианта реализации задания, которую я вероятно доделаю, но тогда
 * этого комментария уже не будет.
 */
class ArrowsView(context: Context): View(context){
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var numberRadius: Float = 0f
    private var hoursArrowWidth: Float = 0f
    private var hourArrowColor: Int = Color.BLACK
    private var minuteArrowColor: Int = Color.BLACK
    private var secondArrowColor: Int = Color.BLACK

    fun setColor(hourArrowColor: Int, minuteArrowColor: Int, secondArrowColor: Int = -1){
        this.hourArrowColor = hourArrowColor
        this.minuteArrowColor = minuteArrowColor
        this.secondArrowColor = secondArrowColor
    }


    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawArrows(canvas)
    }

    override fun layout(l: Int, t: Int, r: Int, b: Int) {
        super.layout(l, t, r, b)
        centerX = (l + r - l) / 2f
        centerY = (t + b - t) / 2f
        val radius = min(centerX, centerY) * 0.95f
        numberRadius = radius * 0.75f
        hoursArrowWidth = radius * 0.1f
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
        val currentTime = Calendar.getInstance()
        val millis = currentTime[Calendar.MILLISECOND]
        val seconds = currentTime[Calendar.SECOND]
        val minutes = currentTime[Calendar.MINUTE]
        val hours = currentTime[Calendar.HOUR]

        drawArrow(canvas, hours + minutes / 60f, 30, heightCoefficient = 0.8f, arrowColor = hourArrowColor)
        drawArrow(canvas, minutes + seconds / 60f, 6, widthCoefficient = 0.5f, arrowColor = minuteArrowColor)
        drawArrow(canvas, seconds * 1f, 6, widthCoefficient = 0.25f, arrowColor = secondArrowColor)

    }
}