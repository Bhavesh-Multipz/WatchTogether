package com.instaconnect.android.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.utils.Model

abstract class BaseViewHolder<T : ViewDataBinding?>(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val binding: T = binding as T
    var model: Model? = null
    abstract fun onBind(position: Int)
    abstract fun onDetached()
    abstract fun onViewRecycled()

}