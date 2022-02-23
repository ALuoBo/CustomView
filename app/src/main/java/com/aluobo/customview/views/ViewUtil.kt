package com.aluobo.customview.views

import android.content.res.Resources
import android.util.TypedValue
import android.view.View

/**
 * @author WillXia
 * @date 2022/2/22.
 */
fun View.dp2px(dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,Resources.getSystem().displayMetrics)
}