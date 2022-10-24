package com.aluobo.customview.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ArcPathView(context: Context, attr: AttributeSet) : View(context, attr) {
    private val paint = Paint()
    private val path = Path()
    private val rectF = RectF(100f,10f,200f,100f)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint.apply {
            style = Paint.Style.STROKE
            strokeWidth =5f
        }

        path.moveTo(10f,10f)
        path.arcTo(rectF,0f,90f,true)
        canvas?.drawPath(path, paint)

    }

}