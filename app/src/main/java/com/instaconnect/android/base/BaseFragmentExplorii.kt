package com.instaconnect.android.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.instaconnect.android.data.model.Event
import com.instaconnect.android.rxBus.RxBus
import com.instaconnect.android.utils.Extras
import dagger.android.support.AndroidSupportInjection
import io.reactivex.functions.Consumer


abstract class BaseFragmentExplorii<T : ViewDataBinding?, V : BaseViewModelExplorii<Any>?> : Fragment() {
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mRootView: View? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        performDependencyInjection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Subscribe for RxBus REFRESH event.
        //see {@link rana.jatin.core.model.Event#REFRESH}
        RxBus.instance!!.subscribe(Event.REFRESH.name, this, String::class.java, refreshConsumer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return viewDataBinding!!.root
    }

    fun performDependencyInjection() {
        AndroidSupportInjection.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = viewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
        mViewModel!!.onViewCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.instance!!.unregister(this)
    }

    /*
   * return arguments passed to the fragment using
   * {@link rana.jatin.core.util.FragmentUtil#setModel(Serializable object) setModel}
   * and {@link rana.jatin.core.util.FragmentUtil#setModel(Model object) setModel} methods.
   */
    fun <T> getModel(): T? {
        val args = arguments ?: return null
        return args.getSerializable(Extras.MODEL.name) as T
    }

    private val refreshConsumer: Consumer<String> =
        Consumer { classToRefresh -> onRefresh(classToRefresh) }

    // @param classToRefresh will be #getClass().getName()
    abstract fun onRefresh(classToRefresh: String?)
    abstract fun onBackPress(fragmentCount: Int): Boolean
    override fun onDestroyView() {
        mViewModel!!.onDestroyView()
        super.onDestroyView()
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int
}