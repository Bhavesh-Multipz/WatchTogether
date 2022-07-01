package com.instaconnect.android.ui.fragment.more_setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.instaconnect.android.utils.IntentUtil.Companion.emailIntent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instaconnect.android.R
import com.instaconnect.android.utils.IntentUtil
import gun0912.tedimagepicker.util.ToastUtil
import io.alterac.blurkit.BlurKit

class DialogFeedback(var ctx: Context) : BottomSheetDialogFragment() {
    var dialog: BottomSheetDialog? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        BlurKit.init(ctx)
        dialog = BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        dialog!!.setCanceledOnTouchOutside(true)
        return dialog as BottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.bottom_feedback_app_dialog, container, false)
        val imageView = v.findViewById<ImageView>(R.id.img_bg)
        val relMain = v.findViewById<View>(R.id.rel_main)
        val txt_submit = v.findViewById<TextView>(R.id.txt_submit)
        val txt_title: AppCompatEditText = v.findViewById(R.id.txt_title)
        val view = v.findViewById<View>(R.id.view)
        view.setOnClickListener { v1: View? -> dismiss() }
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
        txt_submit.setOnClickListener { v12: View? ->
            val review = txt_title.text.toString().trim { it <= ' ' }
            if (review.isEmpty()) {
                Toast.makeText(ctx, "Please Write Your Feedback", Toast.LENGTH_SHORT).show()
            } else {
                //sendReview(review);
                emailIntent(requireActivity(), "contact@explorii.com", "Feedback for Android", review)
                if (dialog != null) {
                    dialog!!.cancel()
                }

                ToastUtil.showToast("Thank You for submitting your feedback.")
            }
        }
        return v
    } /*public void sendReview(String review) {

        Call<FriendListModel> call = null;

        call = dataManager.apiHelper().getWriteReview(dataManager.prefHelper().getUser().getPhone(),review);

        call.enqueue(new Callback<FriendListModel>() {
            @Override
            public void onResponse(Call<FriendListModel> call, Response<FriendListModel> response) {
                Toast.makeText(getContext(),response.body().getResponse().getMessage(),Toast.LENGTH_SHORT).show();
                if(dialog != null){
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<FriendListModel> call, Throwable t) {}
        });
    }*/
}