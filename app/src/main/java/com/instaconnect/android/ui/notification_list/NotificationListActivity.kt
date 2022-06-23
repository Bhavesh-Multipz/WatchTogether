package com.instaconnect.android.ui.notification_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityNotificationBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.login.LoginRepository
import com.instaconnect.android.ui.login.LoginViewModel
import com.instaconnect.android.ui.login.LoginViewModelFactory
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.models.MessagelistItem
import kotlinx.coroutines.launch

class NotificationListActivity : AppCompatActivity(), View.OnClickListener, LazyLoadListener {

    var notificationListAdapter: NotificationListAdapter? = null
    var notificationListList: ArrayList<MessagelistItem> = ArrayList<MessagelistItem>()

    private lateinit var binding : ActivityNotificationBinding
    private lateinit var viewModel : NotificationViewModel
    var page: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        page = 1
        viewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(
                NotificationRepository(
                    MyApi.getInstance()
                )
            )
        )[NotificationViewModel::class.java]

        setView()
        setAdapter()
        getNotificationList(page)

        viewModel.getNotificationListResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null && it.value.response!!.code.equals("200")) {
                        if (it.value.response!!.messagelist == null || it.value.response!!.messagelist.isEmpty()
                        ) {
                            if (page == 1) {
                                binding.txtEmpty.visibility = View.VISIBLE
                                binding.txtEmpty.text = it.value.response!!.message
                            }
                        } else {
                            binding.txtEmpty.visibility = View.GONE
                            notificationListList = it.value.response!!.messagelist as ArrayList<MessagelistItem>
                            notificationListAdapter!!.addUser(notificationListList)
                        }
                        if (it.value.response!!.isLastPage == 0) {
                            binding.recyclerView.isLoading(false)
                        } else {
                            binding.recyclerView.setNextScrollingEnabled(false)
                        }
                    } else if (it.value.response!!.code.equals("301")) {
                        if (page == 1) {
                            binding.txtEmpty.visibility = View.VISIBLE
                            binding.txtEmpty.text = it.value.response!!.message
                        } else {
                            binding.txtEmpty.visibility = View.GONE
                        }
                    }
                }
                is Resource.Failure -> {
                }
                else -> {}
            }
        }
    }

    private fun setAdapter() {
        notificationListAdapter = NotificationListAdapter(this, notificationListList)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setLazyLoadListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = notificationListAdapter
    }

    private fun setView() {
        binding.imgBack.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_back -> finish()
        }
    }

    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        this.page = page
        getNotificationList(page)
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {
    }

    override fun onScrollStateChage(upDown: Int) {
    }

    private fun getNotificationList(page: Int) {
        viewModel.viewModelScope.launch {
            viewModel.getNotificationList(Prefrences.getPreferences(this@NotificationListActivity, Constants.PREF_USER_ID)!!, page)
        }
    }
}