package com.instaconnect.android.ui.fragment.more_setting

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.ui.fragment.more_setting.ShareAppDialogAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.instaconnect.android.R
import io.alterac.blurkit.BlurKit
import java.util.*

class DialogShareApp(var ctx: Context) : BottomSheetDialogFragment() {
    var dialog: BottomSheetDialog? = null
    var shareAppIntent = Intent(Intent.ACTION_SEND)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        BlurKit.init(ctx)
        dialog = BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        dialog!!.setCanceledOnTouchOutside(true)
        return dialog as BottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_share_app_custome_layout, container, false)
        val imageView = v.findViewById<ImageView>(R.id.img_bg)
        val recyclerView: RecyclerView = v.findViewById(R.id.rv_rate_app)
        val relMain = v.findViewById<View>(R.id.rel_main)
        val view = v.findViewById<View>(R.id.view)
        val pm = ctx.packageManager
        shareAppIntent.type = "text/plain"
        val launchables = pm.queryIntentActivities(shareAppIntent, 0)
        Collections.sort(launchables, ResolveInfo.DisplayNameComparator(pm))
        val shareAppDialogAdapter = ShareAppDialogAdapter(launchables, pm, ctx)
        recyclerView.layoutManager = GridLayoutManager(activity, 4)
        recyclerView.adapter = shareAppDialogAdapter
        view.setOnClickListener { dismiss() }
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(BlurKit.getInstance().fastBlur(imageView, 12, 0.12.toFloat()))
            }
        })
        return v
    }
}