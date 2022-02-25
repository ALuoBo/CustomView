package com.aluobo.customview.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * learn from @renWuXian HenCoder
 * 仪表盘 view
 * @author WillXia
 * @date 2022/2/22.
 */
class DashboardView(context: Context, attr: AttributeSet?) : View(context, attr) {
    private val angle = 120f
    private val radius = dp2px(150f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG) //反锯齿
    private val pathEffect: PathDashPathEffect
    private val dash = Path()
    private val pathMeasure = PathMeasure()
    private val dashNum = 10

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dp2px(2f)
        dash.addRect(0f, 0f, dp2px(2f), dp2px(10f), Path.Direction.CW)

        pathMeasure.setPath(Path().apply {
            addArc(
                width / 2 - radius,
                height / 2 - radius,
                width / 2 + radius,
                height / 2 + radius,
                90 + angle / 2,
                360 - angle
            )
        }, false)
        pathEffect = PathDashPathEffect(dash, (pathMeasure.length - dp2px(2f))/dashNum, 0f, PathDashPathEffect.Style.ROTATE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(
            width / 2 - radius,
            height / 2 - radius,
            width / 2 + radius,
            height / 2 + radius,
            90 + angle / 2,
            360 - angle,
            false,
            paint
        )

        paint.pathEffect = pathEffect

        canvas.drawArc(
            width / 2 - radius,
            height / 2 - radius,
            width / 2 + radius,
            height / 2 + radius,
            90 + angle / 2,
            360 - angle,
            false,
            paint
        )

        paint.pathEffect = null

        canvas.drawLine((width/2).toFloat(),
            (height/2).toFloat(),
            (cos(Math.toRadians(getAngelFromMark(1).toDouble())) * 200 + width/2).toFloat(), // cos α = 对边/斜边
            (sin(Math.toRadians(getAngelFromMark(1).toDouble())) * 200 + height/2).toFloat(),// sin α = 邻边/斜边
            paint)
    }

    private fun getAngelFromMark(mark:Int):Int{
        return (90 + angle/2 + (360-angle) /dashNum * mark).toInt()
    }

}