package com.aluobo.customview.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.aluobo.customview.R

/**
 * @author WillXia
 * @date 2022/2/24.
 */
class MaterialEditText(context: Context, attr: AttributeSet) : AppCompatEditText(context, attr) {

    private val TAG = "MaterialEditText"

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val floatTextSize = dp2px(12f)
    private val textMargin = dp2px(8f)
    private val textTopOffset = dp2px(15f)

    private val textLeftOffset = dp2px(5f)
    private val textMoveOffset = dp2px(5f)

    private val backgroundRect = Rect()


    private var textProgress = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var isFloatTextShowing = false

    var useFloatLabel: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    setPadding(
                        paddingLeft,
                        (backgroundRect.top + floatTextSize + textMargin).toInt(),
                        paddingRight,
                        paddingBottom
                    )
                } else {
                    setPadding(
                        paddingLeft,
                        backgroundRect.top,
                        paddingRight,
                        paddingBottom
                    )
                }

            }

            // requestLayout() // 重新测量
        }

    /**
     * 透明度动画
     */
    private val animator = ObjectAnimator.ofFloat(this@MaterialEditText, "textProgress", 0f, 1f)

    init {

        val typeArray = context.obtainStyledAttributes(attr, R.styleable.MaterialEditText)
        useFloatLabel = typeArray.getBoolean(R.styleable.MaterialEditText_useFloatLabel, true)

        background.getPadding(backgroundRect) // 整个 view 的 padding

        if (useFloatLabel) {
            setPadding(
                paddingLeft,
                (paddingTop + floatTextSize + textMargin).toInt(),
                paddingRight,
                paddingBottom
            )

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isFloatTextShowing && s.isNullOrEmpty()) {
                        // 关闭浮动文本
                        isFloatTextShowing = false
                        animator.reverse()
                    } else if (!isFloatTextShowing && !s.isNullOrEmpty()) {
                        // 显示浮动文本
                        isFloatTextShowing = true
                        animator.start()
                    }

                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        paint.textSize = floatTextSize

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.alpha = (0xff * textProgress).toInt()
        Log.d(TAG, "onDraw: $useFloatLabel ")
        if (useFloatLabel) {
            canvas.drawText(
                hint.toString(),
                textLeftOffset,
                textTopOffset + ((1 - textProgress) * textMoveOffset), // 当进度为 1 时，完全不透明并且位置在上方
                paint
            )
        }
    }

}