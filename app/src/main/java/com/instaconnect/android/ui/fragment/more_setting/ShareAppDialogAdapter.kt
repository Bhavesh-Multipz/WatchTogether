package com.instaconnect.android.ui.fragment.more_setting

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R

class ShareAppDialogAdapter internal constructor(
    var mResolveInfo: List<ResolveInfo>,
    var pm: PackageManager,
    var context: Context
) : RecyclerView.Adapter<ShareAppDialogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_share_app_dialog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.imageView.setImageDrawable(mResolveInfo[position].loadIcon(pm))
        holder.textView.text = mResolveInfo[position].loadLabel(pm)
        holder.imageView.setOnClickListener {
            val launchable = mResolveInfo[position]
            val activity = launchable.activityInfo
            val name = ComponentName(activity.applicationInfo.packageName,
                activity.name)
            val targetIntent = Intent(Intent.ACTION_SEND)
            targetIntent.type = "text/plain"
            targetIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi")
            targetIntent.putExtra(Intent.EXTRA_TEXT, "Hey! " + context.getString(R.string.invite_sub_new))
            targetIntent.setPackage(activity.packageName)
            targetIntent.component = ComponentName(activity.packageName, activity.name)
            context.startActivity(targetIntent)
        }
    }

    override fun getItemCount(): Int {
        return mResolveInfo.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_app)
        val textView: TextView = view.findViewById(R.id.txt_name)

    }
}