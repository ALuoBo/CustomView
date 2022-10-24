package com.aluobo.customview.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class CobwebView(context: Context, attr: AttributeSet) : View(context, attr) {
    private val radiaPaint = Paint()
    private val valuePaint = Paint()
    var count = 6
    private val data = floatArrayOf(4f, 3f, 6f, 2f, 4f,1f)
    private val maxValue = 6

    init {
        radiaPaint.apply {
            style = Paint.Style.STROKE
        }
        valuePaint.apply {
            style = Paint.Style.FILL
        }
    }

    private var radius = 0f // 网格半径
    private var centerX = 0f // 中心X
    private var centerY = 0f // 中心Y
    private val angle = Math.PI * 2 / count

    // 在控件大小发生变化时，onSizeChanged 会被调用通知当前控件的大小，所以在此方法中获取最新的控件大小
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = h.coerceAtMost(w) / 2f * 0.9f
        centerX = width / 2f
        centerY = height / 2f
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawPolygon(canvas)
        drawLines(canvas)
        drawRegion(canvas)
    }

    /**
     * 绘制网状线
     */
    private fun drawPolygon(canvas: Canvas) {
        val path = Path()
        val r = radius / count
        for (i in 1..6) {
            val curR = r * i
            path.reset()
            for (j in 0..count) {
                if (j == 0) {
                    path.moveTo(centerX + curR, centerY)
                } else {
                    val x = (centerX + curR * cos(angle * j)).toFloat()
                    val y = (centerY + curR * sin(angle * j)).toFloat()
                    path.lineTo(x, y)
                }
            }
            path.close()
            canvas.drawPath(path, radiaPaint)
        }

    }

    /**
     * 绘制中心向外射线
     */
    private fun drawLines(canvas: Canvas) {
        val path = Path()
        for (i in 0..count) {
            path.reset()
            path.moveTo(centerX, centerY)
            val x = (centerX + radius * cos(angle * i)).toFloat()
            val y = (centerY + radius * sin(angle * i)).toFloat()
            path.lineTo(x, y)
            canvas.drawPath(path, radiaPaint)
        }
    }

    /**
     * 绘制范围
     */
    private fun drawRegion(canvas: Canvas) {
        val path = Path()
        valuePaint.alpha = 127
        for (i in 0 until 6){
            val percent = data[i]/6
            val x = (centerX + radius * cos(angle * i)*percent).toFloat()
            val y = (centerY + radius * sin(angle * i)*percent).toFloat()
            if (i == 0){
                path.moveTo(x,centerY)
            }else{
                path.lineTo(x,y)
            }
            canvas.drawCircle(x,y,10f,valuePaint)
        }
        valuePaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(path,valuePaint)
    }

}