package com.instaconnect.android.widget

import android.content.Context
import com.instaconnect.android.widget.circularReveal.widget.RevealRelativeLayout
import android.view.LayoutInflater
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import com.instaconnect.android.R
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import java.lang.Exception
import java.util.ArrayList

class RelativeViewLayout : RevealRelativeLayout {
    val CONTENT = "type_content"
    val LOADING = "type_loading"
    val EMPTY = "type_empty"
    val ERROR = "type_error"
    var inflater: LayoutInflater? = null
    var contentViews: MutableList<View> = ArrayList()
    var loadingView: View? = null
    var emptyView: View? = null
    var errorView: View? = null

    //    private TextView tvProgressTitle;
    //    private TextView tvProgressDesc;
    private val TAG = RelativeViewLayout::class.java.name
    var state = CONTENT
        private set
    private var layoutParams: LayoutParams? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
        showContent()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
        showContent()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RelativeViewLayout)
        val emptyViewLayout: Int
        val progressViewLayout: Int
        val errorViewLayout: Int
        try {
            emptyViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_emptyView, 0)
            progressViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_progressView, 0)
            errorViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_errorView, 0)
            val marginTop = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginTop, 0)
            val marginBottom =
                ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginBottom, 0)
            val marginLeft = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginLeft, 0)
            val marginRight =
                ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginRight, 0)
            val margin = intArrayOf(marginLeft, marginTop, marginRight, marginBottom)
            setLoadingView(
                progressViewLayout,
                margin,
                ta.getInt(R.styleable.RelativeViewLayout_progressViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.RelativeViewLayout_progressViewAlign, Gravity.NO_GRAVITY)
            )
            setEmptyView(
                emptyViewLayout,
                margin,
                ta.getInt(R.styleable.RelativeViewLayout_emptyViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.RelativeViewLayout_emptyViewAlign, Gravity.NO_GRAVITY)
            )
            setErrorView(
                errorViewLayout,
                margin,
                ta.getInt(R.styleable.RelativeViewLayout_errorViewTheme, MATCH_PARENT),
                ta.getInt(R.styleable.RelativeViewLayout_errorViewAlign, Gravity.NO_GRAVITY)
            )
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
        handler.postDelayed({ switchState(CONTENT, emptyList()) }, DELAY.toLong())
    }

    /**
     * Hide all other states and show content
     *
     * @param skipIds Ids of views not to show
     */
    fun showContent(skipIds: List<Int>) {
        val handler = Handler()
        handler.postDelayed({ switchState(CONTENT, skipIds) }, DELAY.toLong())
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
        handler.postDelayed({ switchState(EMPTY, skipIds) }, DELAY.toLong())
    }

    fun showEmpty() {
        val handler = Handler()
        handler.postDelayed({ switchState(EMPTY, emptyList()) }, DELAY.toLong())
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param skipIds Ids of views to not hide
     */
    fun showError(skipIds: List<Int>) {
        val handler = Handler()
        handler.postDelayed({ switchState(ERROR, skipIds) }, DELAY.toLong())
    }

    fun showError() {
        val handler = Handler()
        handler.postDelayed({ switchState(ERROR, emptyList()) }, DELAY.toLong())
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
            if (margin != null) layoutParams!!.setMargins(
                margin[0],
                margin[1],
                margin[2],
                margin[3]
            )

//            tvProgressTitle = loadingView.findViewById(R.id.progress_title);
//            tvProgressDesc = loadingView.findViewById(R.id.progress_desc);
            addView(loadingView, layoutParams)
            setLoadingViewTheme(theme, gravity)
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    fun setLoadingViewTheme(theme: Int, gravity: Int): RelativeViewLayout {
        try {
            if (theme == MATCH_PARENT) {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                layoutParams!!.addRule(gravity)
                loadingView!!.layoutParams = layoutParams
            } else {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams!!.addRule(gravity)
                loadingView!!.layoutParams = layoutParams
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
        return this
    }

    fun setErrorViewTheme(theme: Int, gravity: Int): RelativeViewLayout {
        try {
            if (theme == MATCH_PARENT) {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                layoutParams!!.addRule(gravity)
                errorView!!.layoutParams = layoutParams
            } else {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams!!.addRule(gravity)
                errorView!!.layoutParams = layoutParams
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
        return this
    }

    fun setEmptyViewTheme(theme: Int, gravity: Int): RelativeViewLayout {
        try {
            if (theme == MATCH_PARENT) {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                layoutParams!!.addRule(gravity)
                emptyView!!.layoutParams = layoutParams
            } else {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                layoutParams!!.addRule(gravity)
                emptyView!!.layoutParams = layoutParams
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
        return this
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
            if (margin != null) layoutParams!!.setMargins(
                margin[0],
                margin[1],
                margin[2],
                margin[3]
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
            errorView!!.setTag(TAG_ERROR)
            errorView!!.setClickable(true)
            errorView!!.setVisibility(GONE)
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            if (margin != null) layoutParams!!.setMargins(
                margin[0],
                margin[1],
                margin[2],
                margin[3]
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
        private const val DELAY = 0
    }
}