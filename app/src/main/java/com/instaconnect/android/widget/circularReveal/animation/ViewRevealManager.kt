package com.instaconnect.android.widget.circularReveal.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.*
import android.os.Build
import android.util.Property
import android.view.View

class ViewRevealManager @JvmOverloads constructor(private val viewTransformation: ViewTransformation = PathTransformation()) {
    private val targets: MutableMap<View, RevealValues> = HashMap()
    private val animators: MutableMap<Animator, RevealValues> = HashMap()
    protected val animatorCallback: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            val values = getValues(animation)
            values!!.clip(true)
        }

        override fun onAnimationCancel(animation: Animator) {
            endAnimation(animation)
        }

        override fun onAnimationEnd(animation: Animator) {
            endAnimation(animation)
        }

        private fun endAnimation(animation: Animator) {
            val values = getValues(animation)
            values!!.clip(false)

            // Clean up after animation is done
            targets.remove(values.target)
            animators.remove(animation)
        }
    }

    fun dispatchCreateAnimator(data: RevealValues): Animator {
        val animator = createAnimator(data)

        // Before animation is started keep them
        targets[data.target()] = data
        animators[animator] = data
        return animator
    }

    /**
     * Create custom animator of circular reveal
     *
     * @param data RevealValues contains information of starting & ending points, animation target and
     * current animation values
     * @return Animator to manage reveal animation
     */
    protected fun createAnimator(data: RevealValues): Animator {
        val animator = ObjectAnimator.ofFloat(data, REVEAL, data.startRadius, data.endRadius)
        animator.addListener(animatorCallback)
        return animator
    }

    /**
     * @return Retruns Animator
     */
    protected fun getValues(animator: Animator): RevealValues? {
        return animators[animator]
    }

    /**
     * @return Map of started animators
     */
    protected fun getValues(view: View): RevealValues? {
        return targets[view]
    }

    /**
     * @return True if you don't want use Android native reveal animator in order to use your own
     * custom one
     */
    protected fun overrideNativeAnimator(): Boolean {
        return false
    }

    /**
     * @return True if animation was started and it is still running, otherwise returns False
     */
    fun isClipped(child: View): Boolean {
        val data = getValues(child)
        return data != null && data.isClipping
    }

    /**
     * Applies path clipping on a canvas before drawing child,
     * you should save canvas state before viewTransformation and
     * restore it afterwards
     *
     * @param canvas Canvas to apply clipping before drawing
     * @param child Reveal animation target
     * @return True if viewTransformation was successfully applied on referenced child, otherwise
     * child be not the target and therefore animation was skipped
     */
    fun transform(canvas: Canvas?, child: View): Boolean {
        val revealData = targets[child]

        // Target doesn't has animation values
        if (revealData == null) {
            return false
        } else check(!(revealData.target !== child)) { "Inconsistency detected, contains incorrect target view" }
        return if (!revealData.isClipping) {
            false
        } else viewTransformation.transform(canvas, child, revealData)
    }

    class RevealValues(// Animation target
        var target: View,
        val centerX: Int,
        val centerY: Int,
        val startRadius: Float,
        val endRadius: Float
    ) {
        companion object {
            private val debugPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            init {
                debugPaint.color = Color.GREEN
                debugPaint.style = Paint.Style.FILL
                debugPaint.strokeWidth = 2f
            }
        }

        /** @return View clip status
         */
        // Flag that indicates whether view is clipping now, mutable
        var isClipping = false

        // Revealed radius
        var radius = 0f
        fun radius(radius: Float) {
            this.radius = radius
        }

        /** @return current clipping radius
         */
        fun radius(): Float {
            return radius
        }

        /** @return Animating view
         */
        fun target(): View {
            return target
        }

        fun clip(clipping: Boolean) {
            isClipping = clipping
        }

    }

    /**
     * Custom View viewTransformation extension used for applying different reveal
     * techniques
     */
    interface ViewTransformation {
        /**
         * Apply view viewTransformation
         *
         * @param canvas Main canvas
         * @param child Target to be clipped & revealed
         * @return True if viewTransformation is applied, otherwise return fAlse
         */
        fun transform(canvas: Canvas?, child: View?, values: RevealValues?): Boolean
    }

    class PathTransformation : ViewTransformation {
        // Android Canvas is tricky, we cannot clip circles directly with Canvas API
        // but it is allowed using Path, therefore we use it :|
        private val path = Path()
        private var op = Region.Op.REPLACE

        /** @see Canvas.clipPath
         */
        fun op(): Region.Op {
            return op
        }

        /** @see Canvas.clipPath
         */
        fun op(op: Region.Op) {
            this.op = op
        }


        override fun transform(canvas: Canvas?, child: View?, values: RevealValues?): Boolean {
            path.reset()
            // trick to applyTransformation animation, when even x & y translations are running
            path.addCircle(
                child!!.x + values!!.centerX, child.y + values.centerY, values.radius,
                Path.Direction.CW
            )
            canvas!!.clipPath(path, op)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                child.invalidateOutline()
            }
            return false
        }
    }

    /**
     * Property animator. For performance improvements better to use
     * directly variable member (but it's little enhancement that always
     * caught as dangerous, let's see)
     */
    class ClipRadiusProperty internal constructor() :
        Property<RevealValues, Float>(Float::class.java, "supportCircularReveal") {
        override fun set(data: RevealValues, value: Float) {
            data.radius = value
            data.target.invalidate()
        }

        override fun get(v: RevealValues): Float {
            return v.radius()
        }
    }

    /**
     * As class name cue's it changes layer type of [View] on animation createAnimator
     * in order to improve animation smooth & performance and returns original value
     * on animation end
     */
    internal class ChangeViewLayerTypeAdapter(
        private val viewData: RevealValues,
        private val featuredLayerType: Int
    ) :
        AnimatorListenerAdapter() {
        private val originalLayerType: Int
        override fun onAnimationStart(animation: Animator) {
            viewData.target().setLayerType(featuredLayerType, null)
        }

        override fun onAnimationCancel(animation: Animator) {
            viewData.target().setLayerType(originalLayerType, null)
        }

        override fun onAnimationEnd(animation: Animator) {
            viewData.target().setLayerType(originalLayerType, null)
        }

        init {
            originalLayerType = viewData.target.layerType
        }
    }

    companion object {
        val REVEAL = ClipRadiusProperty()
    }
}
