package com.instaconnect.android.ui.block_user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityBlockUserBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.Utils.visible
import com.instaconnect.android.utils.models.User
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class BlockUserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBlockUserBinding
    private lateinit var blockUserAdapter: BlockedUserAdapter
    private lateinit var viewModel: BlockedUserViewModel
    private var blockedUserList: ArrayList<User> = ArrayList()
    var unBlockUserItemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            BlockedUserViewModelFactory(
                BlockedUserRepository(
                    MyApi.getInstance()
                )
            )
        )[BlockedUserViewModel::class.java]

        binding.ivBack.setOnClickListener(this)
        binding.llayout.setOnClickListener(this)
        setFollowerAdapter()
        getBlockedUsers()

        viewModel.blockedUserListResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null) {
                        if (it.value.response!!.code.equals("200")) {
                            blockedUserList.clear()
                            blockedUserList = it.value.response!!.blocklist!!

                            if (blockedUserList.isEmpty()) {
                                binding.txtEmpty.visibility = View.VISIBLE
                            } else {
                                binding.txtEmpty.visibility = View.GONE
                            }
                            blockUserAdapter.updateList(blockedUserList)
                        } else if (it.value.response!!.code.equals("301")) {
                            binding.txtEmpty.visibility = View.VISIBLE
                        }
                    }
                }

                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    ToastUtil.showToast(it.toString())
                }
            }
        }

        viewModel.unblockUserResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null) {
                        if (it.value.response!!.code.equals("200")) {
                            ToastUtil.showToast(it.value.response!!.message!!)
                            blockUserAdapter.clear()
                            getBlockedUsers()
                        } else {
                            ToastUtil.showToast(it.value.response!!.message!!)
                        }
                    }
                }

                is Resource.Loading -> {}

                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    ToastUtil.showToast(it.toString())
                }
            }
        }
    }

    private fun getBlockedUsers() {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.getBlockUserList(Prefrences.getPreferences(this@BlockUserActivity, Constants.PREF_USER_ID)!!)
        }
    }


    private fun setFollowerAdapter() {
        blockUserAdapter = BlockedUserAdapter(this, blockedUserList) { it, position ->
            unBlockUserItemPosition = position
            binding.progressBar.visible(true)
            viewModel.viewModelScope.launch {
                viewModel.callUnblockUser(it.user_id,
                    "0",
                    Prefrences.getPreferences(this@BlockUserActivity, Constants.PREF_USER_ID))
            }

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = blockUserAdapter
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }
}