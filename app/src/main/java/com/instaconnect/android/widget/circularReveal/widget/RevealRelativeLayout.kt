package com.instaconnect.android.widget.circularReveal.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.jvm.JvmOverloads
import android.widget.RelativeLayout
import com.instaconnect.android.widget.circularReveal.animation.RevealViewGroup
import com.instaconnect.android.widget.circularReveal.animation.ViewRevealManager
import java.lang.NullPointerException

open class RevealRelativeLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle), RevealViewGroup {
    private var manager: ViewRevealManager
    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        return try {
            canvas.save()
            (manager.transform(canvas, child)
                    and super.drawChild(canvas, child, drawingTime))
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