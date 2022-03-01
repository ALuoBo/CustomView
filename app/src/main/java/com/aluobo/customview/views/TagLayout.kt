package com.aluobo.customview.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

/**
 * @author WillXia
 * @date 2022/2/28.
 */
class TagLayout(context: Context?, attr: AttributeSet?) : ViewGroup(context, attr) {
    private val TAG = this.javaClass.name
    private val childBounds = mutableListOf<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthUsed = 0
        var heightUsed = 0
        var lineMaxHeight = 0
        var lineWidthUse = 0

        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specMode = MeasureSpec.getMode(widthMeasureSpec)

        for ( (index,child)in children.withIndex()) {

            measureChildWithMargins(
                child,
                widthMeasureSpec,
                widthUsed,
                heightMeasureSpec,
                heightUsed
            )

            // 如果已用宽度 + 子 view 宽度 大于 父宽度
            if (specMode != MeasureSpec.UNSPECIFIED
                && lineWidthUse + child.measuredWidth > specWidth
            ) {

                heightUsed += lineMaxHeight
                lineMaxHeight = 0
                lineWidthUse = 0
                measureChildWithMargins(
                    child,
                    widthMeasureSpec,
                    widthUsed,
                    heightMeasureSpec,
                    heightUsed
                )

            }

            if (index >= childBounds.size) {
                childBounds.add(Rect())
            }

            childBounds[index].set(
                lineWidthUse,
                heightUsed,
                lineWidthUse + child.measuredWidth,
                heightUsed + child.measuredHeight
            )

            lineWidthUse += child.measuredWidth
            lineMaxHeight = max(lineMaxHeight, child.measuredHeight)
            widthUsed = max(widthUsed, lineWidthUse)
        }

        var width = widthUsed
        var height = lineMaxHeight + heightUsed

        setMeasuredDimension(width, height) // 测量出自己的尺寸大小
    }

    /* override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
         var widthUsed = 0
         var heightUsed = 0
         var lineWidthUsed = 0
         var lineMaxHeight = 0
         val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
         val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
         for ((index, child) in children.withIndex()) {
             measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)

             if (specWidthMode != MeasureSpec.UNSPECIFIED &&
                 lineWidthUsed + child.measuredWidth > specWidthSize) {
                 lineWidthUsed = 0
                 heightUsed += lineMaxHeight
                 lineMaxHeight = 0
                 measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)
             }

             if (index >= childBounds.size) {
                 childBounds.add(Rect())
             }

             val childBounds = childBounds[index]
             childBounds.set(lineWidthUsed, heightUsed, lineWidthUsed + child.measuredWidth, heightUsed + child.measuredHeight)

             lineWidthUsed += child.measuredWidth
             widthUsed = max(widthUsed, lineWidthUsed)
             lineMaxHeight = max(lineMaxHeight, child.measuredHeight)
         }
         val selfWidth = widthUsed
         val selfHeight = heightUsed + lineMaxHeight
         setMeasuredDimension(selfWidth, selfHeight)
     }*/


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.d(TAG, "left:$l top:$t right:$r bottom:$b childCount:$childCount")
        for ((index, child) in children.withIndex()) {
            val bound = childBounds[index]
            child.layout(bound.left, bound.top, bound.right, bound.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}