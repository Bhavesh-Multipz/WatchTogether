package com.instaconnect.android.ui.trending_websites

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.instaconnect.android.ui.trending_websites.models.WebsitesItem
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R

class TrendingWebsiteListAdapter internal constructor(var context: Context, var websitesItemList: MutableList<WebsitesItem>) :
    RecyclerView.Adapter<TrendingWebsiteListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trending_websites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val websitesItem = websitesItemList[position]
        holder.website.text = websitesItem.name
        holder.webTitleTextView.text = websitesItem.name
        holder.copyUrlImageview.setOnClickListener { view: View? ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Url", websitesItem.link)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Website URL Link Copied. You may paste this to create a room", Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return websitesItemList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val website: TextView = view.findViewById(R.id.tvWebsite)
        val webTitleTextView: TextView = view.findViewById(R.id.tvWebTitle)
        val copyUrlImageview: ImageView = view.findViewById(R.id.ivCopyUrl)
    }

    fun clear() {
        websitesItemList.clear()
        notifyDataSetChanged()
    }

    fun updateList(newWebsitesItemList: List<WebsitesItem>?) {
        websitesItemList.clear()
        websitesItemList.addAll(newWebsitesItemList!!)
        notifyDataSetChanged()
    }
}