package com.instaconnect.android.ui.block_user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityBlockUserBinding
import com.instaconnect.android.utils.models.User
import com.instaconnect.android.widget.RecyclerClickListener

class BlockUserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBlockUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener(this)
        binding.llayout.setOnClickListener(this)
        setFollowerAdapter()
        setAdapterClick()

    }

    private fun setAdapterClick() {
        /*followAdapter.OnClickListener(object : RecyclerClickListener {
            override fun onClick(v: View, viewHolder: View?, position: Int) {
                if (v.id == R.id.tvFollow) {
                    val user: User = followAdapter.getData().get(position)
                    profileViewModel.getPositionUpdate().observe(this@BlockUserActivity, object : Observer<Int?> {
                        fun onChanged(integer: Int) {
                            followAdapter.getData().remove(integer)
                            if (followAdapter.getData().isEmpty()) {
                                getViewDataBinding().txtEmpty.setVisibility(View.VISIBLE)
                            } else {
                                getViewDataBinding().txtEmpty.setVisibility(View.GONE)
                            }
                            followAdapter.notifyDataSetChanged()
                        }
                    })
                    profileViewModel.callFollowUserApi(position,
                        String.valueOf(user.getUser_id()),
                        "0",
                        profileViewModel.dataManager.prefHelper().getUser().getPhone())
                }
            }
        })*/
    }

    private fun setFollowerAdapter() {
        /*profileViewModel.getFollowers().observe(this, followerObserver)
        val recyclerChat: RecyclerView = getViewDataBinding().recyclerView
        recyclerChat.layoutManager = layoutManager
        recyclerChat.adapter = followAdapter*/
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}