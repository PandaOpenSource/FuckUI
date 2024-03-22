package com.example.fuckui.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.fuckui.utils.dp

class PixelView : View {

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val colors = listOf(Color.parseColor("#7F000000"))

    // 单位dp
    private var pixel: Int = 16

    init {
        mPaint.color = colors[0]
        mPaint.strokeWidth = 1f
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    fun getPixel(): Int {
        return pixel
    }

    fun setPixel(pixel: Int) {
        this.pixel = pixel
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        drawCow(canvas)
        drawCon(canvas)
    }

    /**
     * 绘制横向
     */
    private fun drawCow(canvas: Canvas) {
        val h = height
        var ch = 0
        var index = 0
        while (ch < h) {
            mPaint.color = colors[index++ % colors.size]
            canvas.drawLine(0f, ch.toFloat(), width.toFloat(), ch.toFloat(), mPaint)
            ch += pixel.dp
        }
    }

    /**
     * 绘制钟祥
     */
    private fun drawCon(canvas: Canvas) {
        val w = width
        var cw = 0
        var index = 0
        while (cw < w) {
            mPaint.color = colors[index++ % colors.size]
            canvas.drawLine(cw.toFloat(), 0f, cw.toFloat(), height.toFloat(), mPaint)
            cw += pixel.dp
        }
    }
}