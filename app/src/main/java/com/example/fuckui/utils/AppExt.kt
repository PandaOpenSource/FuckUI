package com.example.fuckui.utils

/**
 * dp 转 像素
 */
val Float.dp: Int
    get() = DimensionUtil.dip2px(this)

/**
 * dp 转 像素
 */
val Double.dp: Int
    get() = DimensionUtil.dip2px(this.toFloat())

/**
 * dp 转 像素
 */
val Int.dp: Int
    get() = DimensionUtil.dip2px(this.toFloat())