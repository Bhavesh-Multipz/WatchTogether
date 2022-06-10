package com.instaconnect.android.widget

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.*
import com.instaconnect.android.R
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.Shader.TileMode
import android.util.AttributeSet

class RoundImageView : AppCompatImageView {
    private var border: Border? = null
    private var canvas: Canvas? = Canvas()
    private val paint = Paint()
    private var srcBitmap: Bitmap? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, `as`: AttributeSet?) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.isAntiAlias = true
        if (`as` != null) {
            val ta = context.obtainStyledAttributes(`as`, R.styleable.RoundImageView)
            try {
                //int borderType = ta.getInt(R.styleable.RoundedImageView_type, 0);
                val cornerRadius =
                    ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerRadius, 0)
                border = if (cornerRadius > 0) {
                    RectBorder(cornerRadius)
                } else {
                    CircularBorder()
                }
                border!!.shadowDx = ta.getDimensionPixelSize(R.styleable.RoundImageView_shadowDx, 0)
                border!!.shadowDy = ta.getDimensionPixelSize(R.styleable.RoundImageView_shadowDy, 0)
                border!!.shadowRadius =
                    ta.getDimensionPixelSize(R.styleable.RoundImageView_shadowRadius, 0)
                border!!.shadowColor =
                    ta.getColor(R.styleable.RoundImageView_shadowColor, Color.BLACK)
                border!!.width =
                    ta.getDimensionPixelSize(R.styleable.RoundImageView_outerBorderWidth, 0)
                border!!.color =
                    ta.getColor(R.styleable.RoundImageView_outerBorderColor, Color.BLACK)
            } finally {
                ta.recycle()
            }
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        calculateMetrics()
        return changed
    }

    override fun isOpaque(): Boolean {
        var isOpaque = super.isOpaque()
        if (border != null) {
            isOpaque = isOpaque and border!!.isImageOpaque
        }
        return isOpaque
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        calculateMetrics()
    }

    private fun calculateMetrics() {
        if (border != null) {
            border!!.calculateMetrics(this)
        }
        val width = measuredWidth - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom
        srcBitmap = resizeBitmap(srcBitmap, width, height)
        if (canvas == null) {
            canvas = Canvas()
        }
        canvas!!.setBitmap(srcBitmap)
    }

    override fun draw(canvas: Canvas) {
        //border
        if (border != null) {
            border!!.drawOuter(canvas, paint)
        }

        //background
        val backgroundDrawable = background
        if (backgroundDrawable != null) {
            backgroundDrawable.alpha = Math.round(255 * alpha)
            paint.style = Paint.Style.FILL_AND_STROKE
            if (backgroundDrawable is ColorDrawable) {
                paint.color = backgroundDrawable.color
            } else if (backgroundDrawable is BitmapDrawable) {
                paint.shader =
                    BitmapShader(backgroundDrawable.bitmap, TileMode.CLAMP, TileMode.CLAMP)
            }
            border!!.drawInner(canvas, paint)
        }
        if (srcBitmap != null) {
            srcBitmap!!.eraseColor(Color.TRANSPARENT)
            super.draw(this.canvas)
            paint.shader = BitmapShader(srcBitmap!!, TileMode.CLAMP, TileMode.CLAMP)
            paint.style = Paint.Style.FILL_AND_STROKE
            border!!.drawInner(canvas, paint)
        }
    }

    private abstract class Border {
        var shadowDx = 0
        var shadowDy = 0
        var shadowRadius = 0
        var shadowColor = 0
        var color = 0
        var width = 0
        fun drawOuter(canvas: Canvas, paint: Paint) {
            paint.shader = null
            if (width > 0) {
                paint.color = color
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = width.toFloat()
                paint.setShadowLayer(
                    shadowRadius.toFloat(),
                    shadowDx.toFloat(),
                    shadowDy.toFloat(),
                    shadowColor
                )
                drawOuterImpl(canvas, paint)
            }
        }

        fun drawInner(canvas: Canvas, paint: Paint) {
            if (width > 0) {
                paint.clearShadowLayer()
            } else {
                paint.setShadowLayer(
                    shadowRadius.toFloat(),
                    shadowDx.toFloat(),
                    shadowDy.toFloat(),
                    shadowColor
                )
            }
            drawInnerImpl(canvas, paint)
        }

        abstract fun calculateMetrics(roundImageView: RoundImageView)
        protected abstract fun drawOuterImpl(canvas: Canvas, paint: Paint?)
        protected abstract fun drawInnerImpl(canvas: Canvas, paint: Paint?)
        abstract val isImageOpaque: Boolean
    }

    private class CircularBorder : Border() {
        var center: PointF
        var outerRadius = 0
        var innerRadius = 0
        override fun calculateMetrics(civ: RoundImageView) {
            val paddedWidth = civ.measuredWidth - civ.paddingLeft - civ.paddingRight
            val paddedHeight = civ.measuredHeight - civ.paddingTop - civ.paddingBottom
            outerRadius = Math.min(paddedWidth, paddedHeight) / 2 - width
            center.x = (paddedWidth / 2 + civ.paddingLeft).toFloat()
            center.y = (paddedHeight / 2 + civ.paddingTop).toFloat()
            val shadowDxAbsolute = Math.abs(shadowDx)
            val shadowDyAbsolute = Math.abs(shadowDy)
            outerRadius -= (if (shadowDxAbsolute > shadowDyAbsolute) shadowDxAbsolute else shadowDyAbsolute) + shadowRadius
            innerRadius = outerRadius - width
        }

        override fun drawOuterImpl(canvas: Canvas, paint: Paint?) {
            canvas.drawCircle(center.x, center.y, outerRadius.toFloat(), paint!!)
        }

        override fun drawInnerImpl(canvas: Canvas, paint: Paint?) {
            canvas.drawCircle(center.x, center.y, innerRadius.toFloat(), paint!!)
        }

        override val isImageOpaque: Boolean
            get() = false

        init {
            center = PointF()
        }
    }

    private class RectBorder(var cornerRadius: Int) : Border() {
        private val outerBounds = RectF()
        private val innerBounds = RectF()
        override fun calculateMetrics(civ: RoundImageView) {
            val paddedWidth = civ.measuredWidth - civ.paddingLeft - civ.paddingRight
            val paddedHeight = civ.measuredHeight - civ.paddingTop - civ.paddingBottom
            val originX = civ.paddingLeft
            val originY = civ.paddingTop
            outerBounds[originX.toFloat(), originY.toFloat(), (originX + paddedWidth).toFloat()] =
                (originY + paddedHeight).toFloat()
            outerBounds.inset(
                (shadowRadius + Math.abs(shadowDx) + width).toFloat(),
                (shadowRadius + Math.abs(shadowDy) + width).toFloat()
            )
            innerBounds.set(outerBounds)
            //=1px to hide shadow in the inner corners
            val innerBoundsInset = Math.max(width - 1, 0)
            innerBounds.inset(innerBoundsInset.toFloat(), innerBoundsInset.toFloat())
        }

        override fun drawOuterImpl(canvas: Canvas, paint: Paint?) {
            canvas.drawRoundRect(
                outerBounds,
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                paint!!
            )
        }

        override fun drawInnerImpl(canvas: Canvas, paint: Paint?) {
            val innerCornerRadius = Math.max(cornerRadius - width, 1)
            canvas.drawRoundRect(
                innerBounds,
                innerCornerRadius.toFloat(),
                innerCornerRadius.toFloat(),
                paint!!
            )
        }

        override val isImageOpaque: Boolean
            get() = cornerRadius == 0
    }

    companion object {
        private val DEFAULT_BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private fun resizeBitmap(bitmap: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
            var bitmap = bitmap
            if (newWidth > 0 && newHeight > 0) {
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(newWidth, newHeight, DEFAULT_BITMAP_CONFIG)
                } else {
                    val oldWidth = bitmap.width
                    val oldHeight = bitmap.height
                    val changed = newWidth != oldWidth || newHeight != oldHeight
                    if (changed) {
                        bitmap.recycle()
                        bitmap = Bitmap.createBitmap(newWidth, newHeight, DEFAULT_BITMAP_CONFIG)
                    }
                }
            }
            return bitmap
        }
    }
}