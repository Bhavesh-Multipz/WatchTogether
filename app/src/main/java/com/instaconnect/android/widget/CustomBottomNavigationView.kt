package com.instaconnect.android.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.instaconnect.android.R
import com.instaconnect.android.utils.ScreenUtils


class CustomBottomNavigationView : BottomNavigationView {
    private var mPath: Path? = null
    private var mPaint: Paint? = null
    private var mPathCircle: Path? = null
    private var mPaintCircle: Paint? = null
    private var mPathRectRound: Path? = null
    private var mPaintRectRound: Paint? = null
    private val CURVE_CIRCLE_RADIUS: Int = ScreenUtils.dpToPx(8.0f, getContext()) //96 / 2
    private val CORNER_RADIUS: Int = ScreenUtils.dpToPx(20f, getContext())
    private val mFirstCurveStartPoint: Point = Point()
    private val mFirstCurveEndPoint: Point = Point()
    private val mFirstCurveControlPoint1: Point = Point()
    private val mFirstCurveControlPoint2: Point = Point()
    private var mSecondCurveStartPoint: Point = Point()
    private val mSecondCurveEndPoint: Point = Point()
    private val mSecondCurveControlPoint1: Point = Point()
    private val mSecondCurveControlPoint2: Point = Point()
    private var mNavigationBarWidth = 0
    private var mNavigationBarHeight = 0
    private lateinit var corners: FloatArray

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mPath = Path()
        mPaint = Paint()
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        mPaint!!.color = ContextCompat.getColor(context, R.color.colorPrimary)
        mPathCircle = Path()
        mPaintCircle = Paint()
        mPaintCircle!!.setStyle(Paint.Style.FILL_AND_STROKE)
        mPaintCircle!!.setColor(ContextCompat.getColor(getContext(), R.color.white))
        mPathRectRound = Path()
        mPaintRectRound = Paint()
        mPaintRectRound!!.setStyle(Paint.Style.FILL_AND_STROKE)
        mPaintRectRound!!.setColor(ContextCompat.getColor(getContext(), R.color.white))

//        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.home__item_bg));//grey_bg,gray_cloud
        corners = floatArrayOf(
            CORNER_RADIUS.toFloat(), CORNER_RADIUS.toFloat(),  // Top left radius in px
            CORNER_RADIUS.toFloat(), CORNER_RADIUS.toFloat(), 0f, 0f, 0f, 0f
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMenu(1)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(mPathRectRound!!, mPaintRectRound!!)
        canvas.drawPath(mPath!!, mPaint!!)
        canvas.drawPath(mPathCircle!!, mPaintCircle!!)
    }

    fun updateMenu(position: Int) {
        mNavigationBarWidth = getWidth()
        mNavigationBarHeight = getHeight()
        val menuWidth: Int = mNavigationBarWidth / getMenu().size()
        val halfMenuWidth = menuWidth / 2
        val startPos = menuWidth * position + halfMenuWidth //mNavigationBarWidth / 2
        mFirstCurveStartPoint.set(startPos - CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS / 3, 0)
        mFirstCurveEndPoint.set(startPos, CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 12)
        mSecondCurveStartPoint = mFirstCurveEndPoint
        mSecondCurveEndPoint.set(startPos + CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS / 3, 0)
        mFirstCurveControlPoint1.set(
            mFirstCurveStartPoint.x + CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4,
            mFirstCurveStartPoint.y
        )
        mFirstCurveControlPoint2.set(
            mFirstCurveEndPoint.x - CURVE_CIRCLE_RADIUS * 2 + CURVE_CIRCLE_RADIUS,
            mFirstCurveEndPoint.y
        )
        mSecondCurveControlPoint1.set(
            mSecondCurveStartPoint.x + CURVE_CIRCLE_RADIUS * 2 - CURVE_CIRCLE_RADIUS,
            mSecondCurveStartPoint.y
        )
        mSecondCurveControlPoint2.set(
            mSecondCurveEndPoint.x - (CURVE_CIRCLE_RADIUS + CURVE_CIRCLE_RADIUS / 4),
            mSecondCurveEndPoint.y
        )
        mPath!!.reset()

//        mPath.moveTo(0, 0);
//        mPath.lineTo(mFirstCurveStartPoint.x, mFirstCurveStartPoint.y);
        mPath!!.moveTo(mFirstCurveStartPoint.x.toFloat(), mFirstCurveStartPoint.y.toFloat())
        mPath!!.cubicTo(
            mFirstCurveControlPoint1.x.toFloat(), mFirstCurveControlPoint1.y.toFloat(),
            mFirstCurveControlPoint2.x.toFloat(), mFirstCurveControlPoint2.y.toFloat(),
            mFirstCurveEndPoint.x.toFloat(), mFirstCurveEndPoint.y.toFloat()
        )
        mPath!!.cubicTo(
            mSecondCurveControlPoint1.x.toFloat(), mSecondCurveControlPoint1.y.toFloat(),
            mSecondCurveControlPoint2.x.toFloat(), mSecondCurveControlPoint2.y.toFloat(),
            mSecondCurveEndPoint.x.toFloat(), mSecondCurveEndPoint.y.toFloat()
        )

//        mPath.lineTo(mNavigationBarWidth, 0);
//        mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
//        mPath.lineTo(0, mNavigationBarHeight);
        mPath!!.close()
        mPathRectRound!!.reset()
        mPathRectRound!!.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            corners,
            Path.Direction.CW
        )
        mPathCircle!!.reset()
        mPathCircle!!.addCircle(
            startPos.toFloat(),
            ScreenUtils.dpToPx(4f, context).toFloat(),
            ScreenUtils.dpToPx(2.5f, context).toFloat(),
            Path.Direction.CW
        )
    }
}