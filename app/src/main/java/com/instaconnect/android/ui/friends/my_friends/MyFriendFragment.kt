package com.instaconnect.android.ui.friends.my_friends

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.databinding.FragmentMyFriendBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.Utils.visible
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch

class MyFriendFragment : BaseFragment<MyFriendsViewModel, FragmentMyFriendBinding, MyFriendsRepository>(),
    MyFriendListAdapter.MyFriendListListener, LazyLoadListener {

    var myFriendList: ArrayList<FriendListModel.User> = ArrayList()
    var myFriendListAdapter: MyFriendListAdapter? = null
    var searchKeyword = ""
    var page = 0
    var unFriendPosition : Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        setAdapter()
        getMyFriendList(1, searchKeyword)
        searchProfile()

        // my friends response handler
        viewModel.myFriendsResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        binding.recyclerMyFriend.isLoading(false)
                        binding.progressBar.visible(false)
                        if (it.value.response!!.code == "200") {
                            if (it.value.response!!.userlist == null || it.value.response!!.userlist!!.isEmpty()) {
                                if (page == 1) {
                                    binding.txtEmpty.visibility = View.VISIBLE
                                }
                            } else {
                                binding.txtEmpty.visibility = View.GONE
                                myFriendList = it.value.response!!.userlist!!
                                myFriendListAdapter!!.addUser(myFriendList)
                            }

                            if (it.value.response!!.isLastPage == 0) {
                                binding.recyclerMyFriend.isLoading(false)
                            } else {
                                binding.recyclerMyFriend.setNextScrollingEnabled(false)
                            }
                        } else {
                            binding.txtEmpty.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    binding.recyclerMyFriend.isLoading(false)
                }
            }
        }

        // my friends response handler
        viewModel.makeUnfriendResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {

                        binding.progressBar.visible(false)
                        if (it.value.response!!.code == "200") {
                            myFriendListAdapter!!.clear()
                            getMyFriendList(1, searchKeyword)

                        } else {
                            binding.txtEmpty.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    binding.recyclerMyFriend.isLoading(false)
                }
            }
        }
    }

    private fun setView() {
        binding.recyclerMyFriend.setLazyLoadListener(this)
    }

    private fun searchProfile() {

    }

    private fun getMyFriendList(page: Int, searchKeyword: String) {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.getMyFriendList(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID).toString(),
                searchKeyword,
                page)
        }
    }

    private fun setAdapter() {
        myFriendListAdapter = MyFriendListAdapter(myFriendList, requireContext(), this)
        binding.recyclerMyFriend.layoutManager = GridLayoutManager(activity, 3)
        binding.recyclerMyFriend.adapter = myFriendListAdapter
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMyFriendBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository() = MyFriendsRepository(MyApi.getInstance())

    override fun getViewModel() = MyFriendsViewModel::class.java

    override fun onMyFriendClick(position: Int, user: FriendListModel.User?, view: View?) {

    }

    override fun onFriendView(position: Int, user: FriendListModel.User?, view: View?) {

    }

    override fun onUnfriendClick(position: Int, user: FriendListModel.User?) {
        dialogAskFriendRequest(user!!.userId, user.username!!, position)
    }

    fun dialogAskFriendRequest(other_user_id: String?, userName: String, position: Int) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_send_friend_request)
        val linearCancel = dialog.findViewById<LinearLayout>(R.id.linearCancel)
        val linearOk = dialog.findViewById<LinearLayout>(R.id.linearOk)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val textMessage = dialog.findViewById<TextView>(R.id.text)
        textMessage.text = "Are you sure you want to unfriend $userName?"
        tvOk.text = "Unfriend"
        val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
        val relMain = dialog.findViewById<View>(R.id.rel_main)
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat()))
            }
        })
        linearCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        linearOk.setOnClickListener { v: View? ->
            dialog.dismiss()
            unfriend(other_user_id, position)
        }
        dialog.show()
    }

    private fun unfriend(otherUserId: String?, position: Int) {

        unFriendPosition = position
        viewModel.viewModelScope.launch {
            viewModel.makeUnfriendUser(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, otherUserId!!)
        }
    }

    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        getMyFriendList(page, searchKeyword)
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {

    }

    override fun onScrollStateChage(upDown: Int) {

    }
}