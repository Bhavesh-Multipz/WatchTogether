package com.instaconnect.android.widget

import android.content.Context
import android.graphics.*
import androidx.appcompat.widget.AppCompatImageView
import android.util.AttributeSet

class HexagonMaskView : AppCompatImageView {
    private var hexagonPath: Path? = null
    private var hexagonBorderPath: Path? = null
    private var mBorderPaint: Paint? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        hexagonPath = Path()
        hexagonBorderPath = Path()
        mBorderPaint = Paint()
        mBorderPaint!!.color = Color.WHITE
        mBorderPaint!!.strokeCap = Paint.Cap.ROUND
        mBorderPaint!!.strokeWidth = 50f
        mBorderPaint!!.style = Paint.Style.STROKE
    }

    fun setRadius(radius: Float) {
        calculatePath(radius)
    }

    fun setBorderColor(color: Int) {
        mBorderPaint!!.color = color
        invalidate()
    }

    private fun calculatePath(radius: Float) {
        val halfRadius = radius / 2f
        val triangleHeight = (Math.sqrt(3.0) * halfRadius).toFloat()
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        hexagonPath!!.reset()
        hexagonPath!!.moveTo(centerX, centerY + radius)
        hexagonPath!!.lineTo(centerX - triangleHeight, centerY + halfRadius)
        hexagonPath!!.lineTo(centerX - triangleHeight, centerY - halfRadius)
        hexagonPath!!.lineTo(centerX, centerY - radius)
        hexagonPath!!.lineTo(centerX + triangleHeight, centerY - halfRadius)
        hexagonPath!!.lineTo(centerX + triangleHeight, centerY + halfRadius)
        hexagonPath!!.close()
        val radiusBorder = radius - 5f
        val halfRadiusBorder = radiusBorder / 2f
        val triangleBorderHeight = (Math.sqrt(3.0) * halfRadiusBorder).toFloat()
        hexagonBorderPath!!.reset()
        hexagonBorderPath!!.moveTo(centerX, centerY + radiusBorder)
        hexagonBorderPath!!.lineTo(centerX - triangleBorderHeight, centerY + halfRadiusBorder)
        hexagonBorderPath!!.lineTo(centerX - triangleBorderHeight, centerY - halfRadiusBorder)
        hexagonBorderPath!!.lineTo(centerX, centerY - radiusBorder)
        hexagonBorderPath!!.lineTo(centerX + triangleBorderHeight, centerY - halfRadiusBorder)
        hexagonBorderPath!!.lineTo(centerX + triangleBorderHeight, centerY + halfRadiusBorder)
        hexagonBorderPath!!.close()
        invalidate()
    }

    public override fun onDraw(c: Canvas) {
        c.drawPath(hexagonBorderPath!!, mBorderPaint!!)
        c.clipPath(hexagonPath!!, Region.Op.INTERSECT)
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        super.onDraw(c)
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculatePath(Math.min(width / 2f, height / 2f) - 10f)
    }
}