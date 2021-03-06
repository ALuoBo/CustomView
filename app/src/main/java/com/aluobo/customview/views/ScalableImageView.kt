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
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import com.aluobo.customview.R
import kotlin.math.max
import kotlin.math.min

/**
 * @author WillXia
 * @date 2022/3/2.
 */
private val IMAGE_SIZE = 300.dp.toInt()
private val SCAL_OVER_SIZE = 1.2f

class ScalableImageView(context: Context, attr: AttributeSet) : View(context, attr) {

    private var originalOffsetX = 0f
    private var originalOffsetY = 0f

    private var offsetX = 0f
    private var offsetY = 0f

    private val bitmap: Bitmap
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var smallScale = 0f
    private var bigScale = 0f
    private var isBig = false

    private val overScroller: OverScroller
    val mFilingRunnable = FilingRunnable()
    private var scaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val objectAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)

    private val gestureDetectorListener = MGestureDetectorListener()

    // Android 提供便捷实现滑动，点击的辅助工具，提供一些回调方便使用
    private val gestureDetector = GestureDetectorCompat(context, gestureDetectorListener)

    init {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.ScalableImageView)
        bitmap = getImageBitmap(
            resources,
            typeArray.getResourceId(R.styleable.ScalableImageView_src, 0),
            IMAGE_SIZE
        )
        typeArray.recycle()

        overScroller = OverScroller(context)
        //gestureDetector.setIsLongpressEnabled(false) 可以关闭长按事件
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        originalOffsetX = (width - bitmap.width) / 2f
        originalOffsetY = (height - bitmap.height) / 2f

        // 根据宽高比适应
        if (bitmap.width / bitmap.height.toFloat() > width / height.toFloat()) {
            smallScale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height.toFloat() * SCAL_OVER_SIZE
        } else {
            smallScale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width.toFloat() * SCAL_OVER_SIZE
        }

    }

    /**
     * 使用 GestureDetector 实现 OnTouchEvent 接管
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean = gestureDetector.onTouchEvent(event)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progress = smallScale + (bigScale - smallScale) * scaleFraction

        canvas.translate(offsetX, offsetY)

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

        /**
         * 返回 true 接管触摸事件
         * 除 onDown 的返回值有实际价值，其他重写方法的返回值无实际作用
         */
        override fun onDown(e: MotionEvent?): Boolean = true

        /**
         * 支持预按下，事件触发 100ms 之后会调用此方法
         */
        override fun onShowPress(e: MotionEvent?) {

        }

        /**
         * 设置双击监听器 #OnDoubleTapListener 之后此方法会失效，若设置之后也想实现单击效果，在 #onSingleTapConfirmed 中实现即可
         */
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        /**
         *  @param event 最新事件
         *  @param distanceX 旧的位置减去新的位置
         *  @param distanceY 旧的位置减去新的位置
         */
        override fun onScroll(
            down: MotionEvent?,
            event: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (isBig) {

                offsetX -= distanceX
                offsetY -= distanceY
                offsetX = min(offsetX, (bitmap.width * bigScale - width) / 2)
                offsetY = min(offsetY, (bitmap.height * bigScale - height) / 2)
                offsetX = max(offsetX, -(bitmap.width * bigScale - width) / 2)
                offsetY = max(offsetY, -(bitmap.height * bigScale - height) / 2)
            }
            invalidate()
            return false
        }

        override fun onLongPress(e: MotionEvent?) {}

        override fun onFling(
            down: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (isBig) {
                overScroller.fling(
                    offsetX.toInt(),
                    offsetY.toInt(),
                    velocityX.toInt(),
                    velocityY.toInt(),
                    (-(bitmap.width * bigScale - width) / 2).toInt(),
                    ((bitmap.width * bigScale - width) / 2).toInt(),
                    (-(bitmap.height * bigScale - height) / 2).toInt(),
                    ((bitmap.height * bigScale - height) / 2).toInt(),
                    500,500
                )

                ViewCompat.postOnAnimation(this@ScalableImageView, mFilingRunnable)
            }

            return false
        }

        /**
         * 单击回调
         */
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

    fun refresh() {
        overScroller.computeScrollOffset()
        offsetX = overScroller.currX.toFloat()
        offsetY = overScroller.currY.toFloat()
        invalidate()
    }

    inner class FilingRunnable : Runnable {
        override fun run() {
            if (overScroller.computeScrollOffset()) {
                offsetX = overScroller.currX.toFloat()
                offsetY = overScroller.currY.toFloat()
                invalidate()
                ViewCompat.postOnAnimation(this@ScalableImageView, this)
            }
        }
    }

}