package com.instaconnect.android.ui.fragment.add_post

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseViewHolder
import com.instaconnect.android.databinding.ItemWeblinkBinding
import com.instaconnect.android.ui.youtube_webview.YoutubeWebViewActivity
import com.instaconnect.android.utils.dialog_helper.BaseRecyclarAdapter
import com.instaconnect.android.utils.helper_classes.GlideHelper

class WebLinkAdapter(
    private val context: Context,
    private val webLinksItemArrayList: ArrayList<WebLinksItem>,
    private val captureType: String,
    private val categoryName: String
) : BaseRecyclarAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = getDataBinding<ItemWeblinkBinding>(inflater, R.layout.item_weblink, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return webLinksItemArrayList.size
    }

    private inner class ViewHolder(binding: ItemWeblinkBinding?) : BaseViewHolder<ItemWeblinkBinding?>(
        binding!!) {
        override fun onBind(position: Int) {
            binding!!.tvWebName.text = webLinksItemArrayList[position].name
            GlideHelper.loadFromUrl(context, webLinksItemArrayList[position].image, R.drawable.placeholder, binding.ivIcon)
            binding.relLink.setOnClickListener { v: View? ->
                goToYoutubeWebViewActivity(
                    webLinksItemArrayList[position].link)
            }
        }

        override fun onDetached() {}
        override fun onViewRecycled() {}
    }

    private fun goToYoutubeWebViewActivity(link: String) {
        var link = link
        val youtubeWebViewIntent1 = Intent(context, YoutubeWebViewActivity::class.java)
        youtubeWebViewIntent1.putExtra("PostType", categoryName)
        youtubeWebViewIntent1.putExtra("CaptureType", captureType)
        if (!link.startsWith("http")) {
            link = "http://$link"
        }
        youtubeWebViewIntent1.putExtra("URL_TO_LOAD", link)
        context.startActivity(youtubeWebViewIntent1)
    }

    fun updateList(newWebList: ArrayList<WebLinksItem>?) {
        webLinksItemArrayList.clear()
        webLinksItemArrayList.addAll(newWebList!!)
        notifyDataSetChanged()
    }
}