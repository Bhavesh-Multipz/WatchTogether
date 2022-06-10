package com.instaconnect.android.widget

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.instaconnect.android.R
import com.instaconnect.android.widget.circularReveal.widget.RevealFrameLayout

class FrameViewLayout : RevealFrameLayout {
    val CONTENT = "type_content"
    val LOADING = "type_loading"
    val EMPTY = "type_empty"
    val ERROR = "type_error"
    var inflater: LayoutInflater? = null
    var view: View? = null
    var contentViews: MutableList<View> = ArrayList()
    var loadingView: View? = null
    var emptyView: View? = null
    var errorView: View? = null
    private val TAG = FrameViewLayout::class.java.name
    var state = CONTENT
        private set
    private var layoutParams: LayoutParams? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FrameViewLayout)
        val emptyViewLayout: Int
        val progressViewLayout: Int
        val errorViewLayout: Int
        try {
            emptyViewLayout = ta.getResourceId(R.styleable.FrameViewLayout_emptyView, 0)
            progressViewLayout = ta.getResourceId(R.styleable.FrameViewLayout_progressView, 0)
            errorViewLayout = ta.getResourceId(R.styleable.FrameViewLayout_errorView, 0)
            val marginTop = ta.getDimensionPixelSize(R.styleable.FrameViewLayout_marginTop, 0)
            val marginBottom = ta.getDimensionPixelSize(R.styleable.FrameViewLayout_marginBottom, 0)
            val marginLeft = ta.getDimensionPixelSize(R.styleable.FrameViewLayout_marginLeft, 0)
            val marginRight = ta.getDimensionPixelSize(R.styleable.FrameViewLayout_marginRight, 0)
            val margin = intArrayOf(marginLeft, marginTop, marginRight, marginBottom)
            setLoadingView(
                progressViewLayout,
                margin,
                ta.getInt(R.styleable.FrameViewLayout_progressViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.FrameViewLayout_progressViewGravity, Gravity.NO_GRAVITY)
            )
            setEmptyView(
                emptyViewLayout,
                margin,
                ta.getInt(R.styleable.FrameViewLayout_emptyViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.FrameViewLayout_emptyViewGravity, Gravity.NO_GRAVITY)
            )
            setErrorView(
                errorViewLayout,
                margin,
                ta.getInt(R.styleable.FrameViewLayout_errorViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.FrameViewLayout_errorViewGravity, Gravity.NO_GRAVITY)
            )
            showContent()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ta.recycle()
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        if (child.tag == null || (child.tag != TAG_LOADING
                    && child.tag != TAG_EMPTY && child.tag != TAG_ERROR)
        ) {
            contentViews.add(child)
        }
    }

    /**
     * Hide all other states and show content
     */
    fun showContent() {
        val handler = Handler()
        handler.postDelayed({ switchState(CONTENT, emptyList()) }, DELAY)
    }

    /**
     * Hide all other states and show content
     *
     * @param skipIds Ids of views not to show
     */
    fun showContent(skipIds: List<Int>) {
        val handler = Handler()
        handler.postDelayed({ switchState(CONTENT, skipIds) }, DELAY)
    }

    /**
     * Hide content and show the progress bar
     *
     * @param skipIds Ids of views to not hide
     */
    fun showLoading(skipIds: List<Int>) {
        switchState(LOADING, skipIds)
    }

    /**
     * Hide content and show the progress bar
     */
    fun showLoading() {
        switchState(LOADING, emptyList())
    }

    /**
     * Hide content and show the progress bar with progress text
     */
    fun showLoading(progress: String, des: String) {
        switchState(LOADING, progress, des, emptyList())
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param skipIds Ids of views to not hide
     */
    fun showEmpty(skipIds: List<Int>) {
        val handler = Handler()
        handler.postDelayed({ switchState(EMPTY, skipIds) }, DELAY)
    }

    fun showEmpty() {
        val handler = Handler()
        handler.postDelayed({ switchState(EMPTY, emptyList()) }, DELAY)
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param skipIds Ids of views to not hide
     */
    fun showError(skipIds: List<Int>) {
        val handler = Handler()
        handler.postDelayed({ switchState(ERROR, skipIds) }, DELAY)
    }

    fun showError() {
        val handler = Handler()
        handler.postDelayed({ switchState(ERROR, emptyList()) }, DELAY)
    }

    val isContent: Boolean
        get() = state == CONTENT
    val isLoading: Boolean
        get() = state == LOADING
    val isEmpty: Boolean
        get() = state == EMPTY
    val isError: Boolean
        get() = state == ERROR

    private fun switchState(state: String, skipIds: List<Int>) {
        this.state = state
        when (state) {
            CONTENT -> {
                //Hide all state views to display content
                hideLoadingView()
                hideEmptyView()
                hideErrorView()
            }
            LOADING -> {
                hideEmptyView()
                hideErrorView()
                showLoadingView()
            }
            EMPTY -> {
                hideLoadingView()
                hideErrorView()
                showEmptyView()
            }
            ERROR -> {
                hideLoadingView()
                hideEmptyView()
                showErrorView()
            }
        }
    }

    private fun switchState(
        state: String,
        messageTitle: String,
        messageDesc: String,
        skipIds: List<Int>
    ) {
        this.state = state
        when (state) {
            CONTENT -> {
                //Hide all state views to display content
                hideLoadingView()
                hideEmptyView()
                hideErrorView()
            }
            LOADING -> {
                hideEmptyView()
                hideErrorView()
                showLoadingView(messageTitle, messageDesc)
            }
            EMPTY -> {
                hideLoadingView()
                hideErrorView()
                showEmptyView()
            }
            ERROR -> {
                hideLoadingView()
                hideEmptyView()
                showErrorView()
            }
        }
    }

    private fun showLoadingView() {
        if (loadingView != null) {
            loadingView!!.visibility = VISIBLE
            loadingView!!.bringToFront()
            loadingView!!.invalidate()
        }
    }

    private fun showLoadingView(progress: String, des: String) {
        if (loadingView != null) {
            /*if (tvProgressTitle != null)
                tvProgressTitle.setText(progress);

            if (tvProgressDesc != null)
                tvProgressDesc.setText(des);*/
            loadingView!!.visibility = VISIBLE
            loadingView!!.bringToFront()
            loadingView!!.invalidate()
        }
    }

    private fun showEmptyView() {
        if (emptyView != null) {
            emptyView!!.visibility = VISIBLE
            emptyView!!.bringToFront()
            emptyView!!.invalidate()
        }
    }

    private fun showErrorView() {
        if (errorView != null) {
            errorView!!.visibility = VISIBLE
            errorView!!.bringToFront()
            errorView!!.invalidate()
        }
    }

    fun loadingClickListener(onClickListener: OnClickListener?) {
        if (loadingView != null) {
            loadingView!!.setOnClickListener(onClickListener)
        }
    }

    fun emptyViewClickListener(onClickListener: OnClickListener?) {
        if (emptyView != null) {
            emptyView!!.setOnClickListener(onClickListener)
        }
    }

    fun errorViewClickListener(onClickListener: OnClickListener?) {
        if (errorView != null) {
            errorView!!.setOnClickListener(onClickListener)
        }
    }

    private fun setContentVisibility(visible: Boolean, skipIds: List<Int>) {
        for (v in contentViews) {
            if (!skipIds.contains(v.id)) {
                v.visibility = if (visible) VISIBLE else GONE
            }
        }
    }

    private fun hideLoadingView() {
        if (loadingView != null) {
            loadingView!!.visibility = GONE
        }
    }

    private fun hideEmptyView() {
        if (emptyView != null) {
            emptyView!!.visibility = GONE
        }
    }

    private fun hideErrorView() {
        if (errorView != null) {
            errorView!!.visibility = GONE
        }
    }

    fun setLoadingView(id: Int, margin: IntArray?, theme: Int, gravity: Int) {
        try {
            loadingView = inflater!!.inflate(id, null)
            loadingView!!.tag = TAG_LOADING
            loadingView!!.isClickable = true
            loadingView!!.visibility = GONE
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

//            tvProgressTitle = loadingView.findViewById(R.id.progress_title);
//            tvProgressDesc = loadingView.findViewById(R.id.progress_desc);
            addView(loadingView, layoutParams)
            setLoadingViewTheme(theme, gravity)
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    fun setLoadingViewTheme(theme: Int, gravity: Int): FrameViewLayout {
        return if (theme == MATCH_PARENT) {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams!!.gravity = gravity
            loadingView!!.layoutParams = layoutParams
            this
        } else {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams!!.gravity = gravity
            loadingView!!.layoutParams = layoutParams
            this
        }
    }

    fun setErrorViewTheme(theme: Int, gravity: Int): FrameViewLayout {
        return if (theme == MATCH_PARENT) {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams!!.gravity = gravity
            errorView!!.layoutParams = layoutParams
            this
        } else {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams!!.gravity = gravity
            errorView!!.layoutParams = layoutParams
            this
        }
    }

    fun setEmptyViewTheme(theme: Int, gravity: Int): FrameViewLayout {
        return if (theme == MATCH_PARENT) {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams!!.gravity = gravity
            emptyView!!.layoutParams = layoutParams
            this
        } else {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams!!.gravity = gravity
            emptyView!!.layoutParams = layoutParams
            this
        }
    }

    fun setEmptyView(id: Int, margin: IntArray?, theme: Int, gravity: Int) {
        try {
            emptyView = inflater!!.inflate(id, null)
            emptyView!!.tag = TAG_EMPTY
            emptyView!!.isClickable = true
            emptyView!!.visibility = GONE
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(emptyView, layoutParams)
            setEmptyViewTheme(theme, gravity)
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    fun setErrorView(id: Int, margin: IntArray?, theme: Int, gravity: Int) {
        try {
            errorView = inflater!!.inflate(id, null)
            errorView!!.tag = TAG_ERROR
            errorView!!.isClickable = true
            errorView!!.visibility = GONE
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(errorView, layoutParams)
            setErrorViewTheme(theme, gravity)
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    companion object {
        const val MATCH_PARENT = 2
        const val WRAP_CONTENT = 1
        private const val TAG_LOADING = "ProgressView.TAG_LOADING"
        private const val TAG_EMPTY = "ProgressView.TAG_EMPTY"
        private const val TAG_ERROR = "ProgressView.TAG_ERROR"

        //    private TextView tvProgressTitle;
        //    private TextView tvProgressDesc;
        var DELAY: Long = 0
    }
}