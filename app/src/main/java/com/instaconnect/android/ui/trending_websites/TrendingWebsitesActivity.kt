package com.instaconnect.android.ui.trending_websites

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityTrendingWebsitesBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.trending_websites.models.WebsitesItem
import com.instaconnect.android.utils.Utils.visible
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class TrendingWebsitesActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTrendingWebsitesBinding
    private lateinit var viewModel: TrendingWebsitesViewModel

    var trendingWebsiteListAdapter: TrendingWebsiteListAdapter? = null
    var websitesItemList: ArrayList<WebsitesItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrendingWebsitesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            TrendingWebsitesViewModelFactory(
                TrendingWebsitesRepository(
                    MyApi.getInstance()
                )
            )
        )[TrendingWebsitesViewModel::class.java]


        setView()
        setAdapter()
        getTrendingWebsiteList()

        viewModel.trendingWebsitesResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null) {
                        if (it.value.response!!.code.equals("200")) {
                            websitesItemList = it.value.response!!.streamingwebsites!!.websites!!
                            trendingWebsiteListAdapter!!.updateList(websitesItemList)
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }
    }

    private fun getTrendingWebsiteList() {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.getTrendingWebsites()
        }
    }

    private fun setAdapter() {
        trendingWebsiteListAdapter = TrendingWebsiteListAdapter(this, websitesItemList)
        binding.rvTrendingWebsites.setHasFixedSize(true)
        binding.rvTrendingWebsites.layoutManager = LinearLayoutManager(this)
        binding.rvTrendingWebsites.adapter = trendingWebsiteListAdapter
    }

    private fun setView() {
        binding.ivBackButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                finish()
            }
        }
    }
}