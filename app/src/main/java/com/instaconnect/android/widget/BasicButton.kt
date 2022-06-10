package com.instaconnect.android.widget

import android.content.Context
import androidx.appcompat.widget.AppCompatButton
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import com.instaconnect.android.R

class BasicButton : AppCompatButton {
    private var typeFace: String? = ""

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs)
        setTypeFace()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
        setTypeFace()
    }

    constructor(context: Context?) : super(context!!) {}

    private fun init(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.BasicButton)
        for (i in 0 until array.indexCount) {
            val attr = array.getIndex(i)
            if (attr == R.styleable.BasicButton_typeFace) typeFace = array.getString(attr)
        }
        array.recycle()
    }

    private fun setTypeFace() {
        if (!isInEditMode) {
            if (!typeFace!!.isEmpty()) {
                val tf = Typeface.createFromAsset(
                    context.assets,
                    "fonts/$typeFace"
                )
                typeface = tf
            }
        }
    }
}