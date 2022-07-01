package com.instaconnect.android.ui.block_user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseViewHolder
import com.instaconnect.android.data.model.MessageDataItem
import com.instaconnect.android.utils.helper_classes.GlideHelper.Companion.loadFromUrl
import com.instaconnect.android.databinding.RvRequestItemBinding
import com.instaconnect.android.utils.dialog_helper.BaseRecyclarAdapter
import com.instaconnect.android.utils.models.User

class BlockedUserAdapter(private val context: Context, private val blockedUserList: ArrayList<User>,
                         private val itemClick: ((item: User, position:Int) -> Unit)? = null
) : BaseRecyclarAdapter() {

    private val appUserNumber: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = getDataBinding<RvRequestItemBinding>(inflater, R.layout.rv_request_item, parent, false)
        return RequestLayoutHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<*>, position: Int) {
        viewHolder.onBind(position)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<*>) {
        holder.onDetached()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return blockedUserList.size
    }

    fun clear() {
        blockedUserList.clear()
        notifyDataSetChanged()
    }

    fun updateList(chatMessages: List<User>?) {
        blockedUserList.clear()
        blockedUserList.addAll(chatMessages!!)
        notifyDataSetChanged()
    }

    val data: List<User>
        get() = blockedUserList

    inner class RequestLayoutHolder(binding: RvRequestItemBinding?) : BaseViewHolder<RvRequestItemBinding?>(
        binding!!), View.OnClickListener {

        override fun onBind(position: Int) {
            binding!!.tvName.text = message(position).name
            loadFromUrl(context, message(position).avatar, R.drawable.ic_person_glass, binding.imgProfile)
            binding.tvFollow.text = "Unblock"
            binding.tvFollow.setOnClickListener(this)

            itemView.setOnClickListener {
                itemClick!!.invoke(message(position), position)
            }

        }

        private fun message(pos: Int): User {
            return blockedUserList[pos]
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.tvFollow -> itemClick!!.invoke(message(position), position)
            }
        }

        override fun onDetached() {}
        override fun onViewRecycled() {}
    }
}