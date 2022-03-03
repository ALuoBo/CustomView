package com.aluobo.customview.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import com.aluobo.customview.R

/**
 * @author WillXia
 * @date 2022/3/2.
 */
private val IMAGE_SIZE = 300.dp.toInt()

class ScalableImageView(context: Context, attr: AttributeSet) : View(context, attr) {

    private var originalOffsetX: Float = 0f
    private var originalOffsetY: Float = 0f

    private val bitmap: Bitmap
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var smallScale = 0f
    private var bigScale = 0f
    private var isBig = false

    private var fraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val objectAnimator = ObjectAnimator.ofFloat(this, "fraction", 0f, 1f)

    private val gestureDetectorListener = MGestureDetectorListener()
    private val gestureDetector = GestureDetectorCompat(context, gestureDetectorListener)


    init {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.ScalableImageView)
        bitmap = getImageBitmap(
            resources,
            typeArray.getResourceId(R.styleable.ScalableImageView_src, 0),
            IMAGE_SIZE
        )
        typeArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        originalOffsetX = (width - bitmap.width) / 2f
        originalOffsetY = (height - bitmap.height) / 2f

        // 根据宽高比适应
        if (bitmap.width / bitmap.height.toFloat() > width / height.toFloat()) {
            smallScale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height.toFloat()
        } else {
            smallScale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width.toFloat()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean = gestureDetector.onTouchEvent(event)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progress = smallScale + (bigScale - smallScale) * fraction

        canvas.scale(
            progress,
            progress,
            width / 2f,
            height / 2f
        ) //参数里的 sx sy 是横向和纵向的放缩倍数； px py 是放缩的轴心

        canvas.drawBitmap(
            bitmap,
            originalOffsetX, // 确保绘制在屏幕中心
            originalOffsetY,
            paint
        ) // 绘制图片
    }


    inner class MGestureDetectorListener : GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

        override fun onDown(e: MotionEvent?): Boolean = true

        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {

        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            isBig = if (isBig) {
                objectAnimator.reverse()
                false
            } else {
                objectAnimator.start()
                true
            }
            return false
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return false
        }
    }

}