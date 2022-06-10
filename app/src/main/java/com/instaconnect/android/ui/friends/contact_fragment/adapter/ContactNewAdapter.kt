package com.instaconnect.android.ui.friends.contact_fragment.adapter

import android.content.Context
import com.instaconnect.android.utils.helper_classes.GlideHelper.Companion.loadAvatar
import com.instaconnect.android.utils.Prefrences.Companion.getPreferences
import com.instaconnect.android.utils.IntentUtil.Companion.sendSMS
import com.instaconnect.android.utils.helper_classes.GlideHelper.Companion.loadFromPath
import com.instaconnect.android.utils.ViewUtil
import com.instaconnect.android.fileHelper.DataManager
import com.instaconnect.android.utils.dialog_helper.SchedulerProvider
import com.instaconnect.android.utils.dialog_helper.BaseRecyclarAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseViewHolder
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.databinding.RvContactHeaderBinding
import com.instaconnect.android.databinding.RvContactItemNewBinding
import com.instaconnect.android.model.ChatActivityBundle
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.utils.Constants
import java.lang.Exception
import java.util.ArrayList

class ContactNewAdapter(
    private val context: Context,
    private val viewUtil: ViewUtil,
) : BaseRecyclarAdapter() {
    private val contactsList: MutableList<Contacts> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = getDataBinding<RvContactHeaderBinding>(
                inflater,
                R.layout.rv_contact_header,
                parent,
                false
            )
            HeaderViewHolder(binding)
        } else {
            val binding = getDataBinding<RvContactItemNewBinding>(
                inflater,
                R.layout.rv_contact_item_new,
                parent,
                false
            )
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.onBind(position)
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<*>) {
        holder.onDetached()
        super.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: BaseViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    override fun getItemViewType(position: Int): Int {
        /* if (contactsList.get(position).getHeader() != null && !contactsList.get(position).getHeader().isEmpty())
            return 1;
        else*/
        return 2
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return contactsList.size
    }

    fun clear() {
        contactsList.clear()
        notifyDataSetChanged()
    }

    var data: List<Contacts>?
        get() = contactsList
        set(contact) {
            contactsList.addAll(contact!!)
            notifyDataSetChanged()
        }

    fun insertItem(contact1: Contacts) {
        contactsList.add(contact1)
        notifyItemInserted(contactsList.size - 1)
    }

    fun deleteItem(position: Int) {
        contactsList.removeAt(position)
        notifyItemRemoved(position)
    }

    interface ContactAdapterCallback {
        fun onInvite()
    }

    interface ContactAdapterNewBridge {
        fun updateContact(pos: Int, contacts: Contacts)
    }

    inner class ItemViewHolder  //        private ContactAdapterNewViewModel viewModel;
        (binding: RvContactItemNewBinding) : BaseViewHolder<RvContactItemNewBinding?>(binding), View.OnClickListener,
        ContactAdapterNewBridge {
        override fun onBind(position: Int) {
            binding!!.contact = contactsList[position]
            if (contactsList[position].isBlocked) binding.tvItemButton.setText(R.string.unblock)
//            loadAvatar(context, dataManager, contactsList[position].phone, R.drawable.ic_person_glass, binding.imgProfile)

            /*viewModel = new ContactAdapterNewViewModel(dataManager, smackManager, schedulerProvider);
            viewModel.searchSmackUsers(position, contactsList.get(position));
            viewModel.setBridge(this);*/itemView.setOnClickListener(this)
            if (contactsList[position].isStatus) {
                binding.tvItemButton.visibility = View.GONE
            } else {
                binding.tvItemButton.visibility = View.VISIBLE
            }
        }

        override fun onDetached() {}
        override fun onViewRecycled() {}
        override fun onClick(view: View) {
            if (contactsList[bindingAdapterPosition].isBlocked) {
                // bloc user api
//                dataManager.dbHelper().unblockUser(contactsList.get(getBindingAdapterPosition()).getPhone());
            } else if (contactsList[bindingAdapterPosition].isStatus) {
                val threadId = contactsList[bindingAdapterPosition].phone + "_" + getPreferences(
                    context, Constants.PREF_USER_ID
                )
                val chatActivityBundle = ChatActivityBundle()
                chatActivityBundle.chatMemberName = contactsList[adapterPosition].name
                chatActivityBundle.chatMemberPhone = contactsList[adapterPosition].phone
                chatActivityBundle.chatType = Constants.CHAT_TYPE_PRIVATE
                chatActivityBundle.chatMode = Constants.CHAT_MODE_ONE_TO_ONE
                chatActivityBundle.chatMemberAvatar = contactsList[adapterPosition].avatar
                chatActivityBundle.threadId = threadId
                if (context is HomeActivity) {
                    println("CONTEXT YES")
                    //                    ((HomeActivity)context).isONECHAT=true;
                    //((HomeActivity)context).startActivityForResult(new BaseIntent((BaseActivity) context, ChatActivity.class, false).setModel(chatActivityBundle),11);
                } else {
                    //context.startActivity(new BaseIntent((BaseActivity) context, ChatActivity.class, false).setModel(chatActivityBundle));
                }
            } else {
                sendSMS(
                    context, "Hey! " + contactsList[adapterPosition]
                        .name + ". " + context.getString(R.string.invite_sub_new), contactsList[adapterPosition].phone
                )
                /*viewUtil.showShareSheet(context.getString(R.string.app_name),
                        "Hi "+contactsList.get(getAdapterPosition()).getName()+"\n"+context.getString(R.string.invite_sub));*/
            }
        }

        override fun updateContact(position: Int, contacts: Contacts) {
            try {
                if (position >= contactsList.size) return
                contactsList[position].isStatus = true
                contactsList[position].avatar = contacts.avatar
                if (binding!!.contact!!.uid == contactsList[position].uid) {
                    binding.contact = contactsList[position]
                    if (contacts.isBlocked) binding.tvItemButton.setText(R.string.unblock)
                    loadFromPath(context, contactsList[position].avatar, R.drawable.ic_person_glass, binding.imgProfile)
                }
                notifyItemChanged(adapterPosition)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class HeaderViewHolder(binding: RvContactHeaderBinding?) : BaseViewHolder<RvContactHeaderBinding?>(
        binding!!
    ) {
        override fun onBind(position: Int) {
            binding!!.contact = contactsList[position]
        }

        override fun onDetached() {}
        override fun onViewRecycled() {}
    }
}