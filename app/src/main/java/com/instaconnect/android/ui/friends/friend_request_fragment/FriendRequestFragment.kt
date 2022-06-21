package com.instaconnect.android.ui.friends.friend_request_fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.allattentionhere.autoplayvideos.recyclerview.BasicRecyclerView
import com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.databinding.FragmentFriendRequestBinding
import com.instaconnect.android.fileHelper.DataManager
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.friends.friend_request_fragment.FriendRequestListAdapter.FriendRequestListener
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import kotlinx.coroutines.launch
import retrofit2.Call
import javax.inject.Inject

class FriendRequestFragment : BaseFragment<FriendsRequestViewModel, FragmentFriendRequestBinding, FriendsRequestRepository>(),
    OnRefreshListener, FriendRequestListener, LazyLoadListener {

    var myFriendListAdapter: FriendRequestListAdapter? = null
    var myFriendList: ArrayList<FriendListModel.User> = ArrayList()


    var searchKeyword = ""
    var page = 1
    var postToRemove : Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setAdapter()
        getFriendRequestList(1, searchKeyword)
        searchProfile()

        // get friend request list response handler
        viewModel.getFriendListResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (it.value.response!!.code.equals("200")) {
                        if (it.value.response!!.userlist == null || it.value.response!!.userlist!!.isEmpty()) {
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
                            binding.txtEmpty.visibility = View.VISIBLE
                        } else {
                            binding.txtEmpty.visibility = View.GONE
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

        viewModel.getFriendRequestResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    if (it.value.response!!.code.equals("200")) {
                        if (it.value.response!!.userlist == null || it.value.response!!.userlist!!.isEmpty()) {
                            if (page == 1) {
                                //txtEmpty.setVisibility(View.VISIBLE);
                                //txtEmpty.setText(it.value.response.getMessage());
                            }
                        } else {
                            // txtEmpty.setVisibility(View.GONE);
                            myFriendList = it.value.response!!.userlist!!
                            myFriendListAdapter!!.removeUser(postToRemove!!)
                        }
                        if (it.value.response!!.isLastPage == 0) {
                            binding.recyclerMyFriend.isLoading(false)
                        } else {
                            binding.recyclerMyFriend.isNestedScrollingEnabled = false
                        }
                    } else if (it.value.response!!.code.equals("301")) {
                        if (page == 1) {
                            binding.txtEmpty.visibility = View.VISIBLE
                        } else {
                            binding.txtEmpty.visibility = View.GONE
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
    }

    fun onBackPress(fragmentCount: Int) {}

    private fun setListeners() {


        binding.swipeRefresh.isRefreshing = false
        binding.recyclerMyFriend.setLazyLoadListener(this)
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun setAdapter() {
        myFriendListAdapter = FriendRequestListAdapter(myFriendList, requireContext(), this)
        binding.recyclerMyFriend.layoutManager = GridLayoutManager(activity, 1)
        binding.recyclerMyFriend.adapter = myFriendListAdapter
    }

    fun getFriendRequestList(page: Int, search: String?) {
        viewModel.viewModelScope.launch {
            viewModel.getFriendRequest(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, search!!, page)
        }
    }

    private fun getFriendRequestResponse(other_user_id: String?, is_accept: String?, pos: Int) {
        this.postToRemove = pos
        binding.swipeRefresh.isRefreshing = true

        viewModel.viewModelScope.launch {
            viewModel.getFriendRequestResponse(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!,
                other_user_id!!,
                is_accept!!)
        }
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
                    getFriendRequestList(1, searchKeyword)
                }, 400)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onRefresh() {
        // refreshLayout.setRefreshing(false);
        if (binding.recyclerMyFriend == null || myFriendListAdapter == null) return
        binding.recyclerMyFriend.resetLazyLoadListener()
        myFriendListAdapter!!.clear()
        getFriendRequestList(1, searchKeyword)
    }

    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        this.page = page
        getFriendRequestList(page, searchKeyword)
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {
    }

    override fun onScrollStateChage(upDown: Int) {
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFriendRequestBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository() = FriendsRequestRepository(MyApi.getInstance())

    companion object {
        var instance: FriendRequestFragment? = null
    }

    override fun getViewModel() = FriendsRequestViewModel::class.java
    override fun onItemClick(position: Int, user: FriendListModel.User?, view: View?, isAccept: String?) {
        var is_accept = ""
        is_accept = if (isAccept == "1") {
            "1"
        } else {
            "2"
        }
        //  rlView.setVisibility(View.VISIBLE);
        getFriendRequestResponse(user!!.userId, is_accept, position)
    }
}