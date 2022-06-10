package com.instaconnect.android.widget.circularReveal.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import android.widget.LinearLayout
import com.instaconnect.android.widget.circularReveal.animation.RevealViewGroup
import com.instaconnect.android.widget.circularReveal.animation.ViewRevealManager
import java.lang.NullPointerException

class RevealLinearLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs), RevealViewGroup {
    private var manager: ViewRevealManager
    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        return try {
            canvas.save()
            manager.transform(canvas, child)
            super.drawChild(canvas, child, drawingTime)
        } finally {
            canvas.restore()
        }
    }

    override var viewRevealManager: ViewRevealManager?
        get() = manager
        set(manager) {
            if (manager == null) {
                throw NullPointerException("ViewRevealManager is null")
            }
            this.manager = manager
        }

    init {
        manager = ViewRevealManager()
    }
}