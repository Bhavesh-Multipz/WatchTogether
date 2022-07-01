package com.instaconnect.android.ui.friends.add_friend_fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.databinding.FragmentAddFriendBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.watch_together_room.AddFriendToVideoListAdapter
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch

class AddFriendFragment : BaseFragment<AddFriendFragmentViewModel, FragmentAddFriendBinding, AddFriendRepository>(),
    SwipeRefreshLayout.OnRefreshListener, AddFriendToVideoListAdapter.AddFriendListListener, LazyLoadListener,
    AddFriendListAdapter.AddFriendListListener {


    private lateinit var removeAddFriendIcon: View
    var myFriendListAdapter: AddFriendListAdapter? = null
    var myFriendList: ArrayList<FriendListModel.User> = ArrayList()
    var page = 1
    var searchKeyword = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListeners()
        setAdapter()
        getAddFriendList(1, searchKeyword)
        searchProfile()

        // get friend list response handler
        viewModel.friendListResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (it.value.response!!.code.equals("200")) {
                        if (it.value.response!!.userlist == null || it.value.response!!.userlist!!.isEmpty()
                        ) {
                            if (page == 1) {
                                //txtEmpty.setVisibility(View.VISIBLE);
                                //txtEmpty.setText(it.value.response.getMessage());
                            }
                        } else {
                            // txtEmpty.setVisibility(View.GONE);
                            myFriendList = it.value.response!!.userlist!!
                            myFriendListAdapter!!.addUser(myFriendList)
                        }
                        if (it.value.response!!.isLastPage == 0) {
                            binding.recyclerMyFriend.isLoading(false)
                        } else {
                            binding.recyclerMyFriend.isNestedScrollingEnabled = false
                        }
                    } else if (it.value.response!!.code.equals("301")) {
                        if (page == 1) {
                            // txtEmpty.setVisibility(View.VISIBLE);
                            // txtEmpty.setText(it.value.response.getMessage());
                        } else {
                            //txtEmpty.setVisibility(View.GONE);
                        }
                    }
                }
                is Resource.Loading -> {
                    binding.swipeRefresh.isRefreshing = true
                    binding.recyclerMyFriend.isLoading(true)
                }
                is Resource.Failure -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.recyclerMyFriend.isLoading(false)
                }
            }
        }

        // send friend request response handler
        viewModel.sendFriendRequestResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response!!.code.equals("200")) {
                        removeAddFriendIcon.visibility = View.GONE
                        Toast.makeText(requireContext(), "Friend request sent successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), it.value.response!!.message, Toast.LENGTH_SHORT).show()
                    }
                    binding.swipeRefresh.isRefreshing = false
                }
                is Resource.Loading -> {
                    binding.recyclerMyFriend.isLoading(true)
                }
                is Resource.Failure -> {
                    binding.recyclerMyFriend.isLoading(false)
                }
            }
        }
    }


    private fun setEventListeners() {
        binding.swipeRefresh.isRefreshing = false
        binding.recyclerMyFriend.setLazyLoadListener(this)
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setAdapter() {
        myFriendListAdapter = AddFriendListAdapter(myFriendList, requireContext(), this)
        binding.recyclerMyFriend.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.recyclerMyFriend.adapter = myFriendListAdapter
    }

    private fun searchProfile() {
        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchKeyword = s.toString()
                Handler().postDelayed({
                    /*if (call != null) {
                        call!!.cancel()
                    }*/
                    binding.recyclerMyFriend.resetLazyLoadListener()
                    myFriendListAdapter!!.clear()
                    getAddFriendList(1, searchKeyword)
                }, 400)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onRefresh() {
        // refreshLayout.setRefreshing(false);
        if (myFriendListAdapter == null) return
        binding.recyclerMyFriend.resetLazyLoadListener()
        myFriendListAdapter!!.clear()
        getAddFriendList(1, searchKeyword)
    }

    fun getAddFriendList(page: Int, search: String?) {
        binding.swipeRefresh.isRefreshing = true
        viewModel.viewModelScope.launch {
            viewModel.getAddFriendList(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, search!!, page)
        }
    }

    private fun sendFriendRequest(other_user_id: String?, addImageView: View) {

        removeAddFriendIcon = addImageView
        viewModel.viewModelScope.launch {
            viewModel.sendFriendRequest(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, other_user_id!!)
        }
    }


    override fun onAddFriendClick(position: Int, user: FriendListModel.User?, addImageView: View?) {

        dialogAskFriendRequest(addImageView!!, user!!.userId, user.username!!)
    }

    override fun onFriendView(position: Int, user: FriendListModel.User?, view: View?) {
    }

    private fun dialogAskFriendRequest(addImageView: View, other_user_id: String?, userName: String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_send_friend_request)
        val linearCancel = dialog.findViewById<LinearLayout>(R.id.linearCancel)
        val linearOk = dialog.findViewById<LinearLayout>(R.id.linearOk)
        val textMessage = dialog.findViewById<TextView>(R.id.text)
        textMessage.text = "Are you sure you want to send friend request to $userName?"
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
        linearOk.setOnClickListener {
            dialog.dismiss()
            sendFriendRequest(other_user_id, addImageView)
        }
        dialog.show()
    }

    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        Log.e("AddFragment1", "$page....$totalItemsCount")
        this.page = page
        getAddFriendList(page+1, searchKeyword)
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {
    }

    override fun onScrollStateChage(upDown: Int) {

    }

    companion object {
        var instance: AddFriendFragment? = null
    }

    override fun getViewModel() = AddFriendFragmentViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAddFriendBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository() = AddFriendRepository(MyApi.getInstance())
}