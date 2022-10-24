package com.aluobo.customview.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class PathView(context: Context, attr: AttributeSet) : View(context, attr) {
    private val paint = Paint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        path.moveTo(10f,10f)
        path.lineTo(10f,100f)
        path.lineTo(200f,100f)
        path.close()

        canvas?.drawPath(path,paint)

    }
}