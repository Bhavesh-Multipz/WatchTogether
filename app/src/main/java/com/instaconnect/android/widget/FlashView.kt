package com.instaconnect.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.AttrRes
import com.instaconnect.android.R
import com.instaconnect.android.widget.FlashView.FlashListener

/**
 * Created by Bhavesh on 21/06/2022.
 */
class FlashView : FrameLayout, View.OnClickListener {
    private var ivFlash: ImageView? = null
    private var mode = 1
    private var listener: FlashListener? = null

    constructor(context: Context) : super(context) {
        inflate()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflate()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflate()
    }

    private fun inflate() {
        LayoutInflater.from(context).inflate(R.layout.widget_flash_view, this)
        findViews()
    }

    private fun findViews() {
        ivFlash = findViewById<View>(R.id.iv_flash) as ImageView
        setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (mode == 1) {
            ivFlash!!.setImageResource(R.drawable.flash_on)
            mode = 2
            if (listener != null) listener!!.flashOn()
        } else if (mode == 2) {
            ivFlash!!.setImageResource(R.drawable.flash_off)
            mode = 3
            if (listener != null) listener!!.flashOff()
        } else {
            ivFlash!!.setImageResource(R.drawable.flash_auto)
            mode = 1
            if (listener != null) listener!!.flashAuto()
        }
    }

    fun setFlashListener(listener: FlashListener?) {
        this.listener = listener
    }

    interface FlashListener {
        fun flashAuto()
        fun flashOff()
        fun flashOn()
    }
}