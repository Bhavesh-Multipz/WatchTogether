package com.instaconnect.android.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbb20.CountryCodePicker
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.terms_webview.TermsWebViewActivity
import com.instaconnect.android.utils.dialog_helper.DialogCallback
import com.instaconnect.android.utils.dialog_helper.ListDialogAdapter
import com.instaconnect.android.utils.models.DialogItem
import com.instaconnect.android.widget.RecyclerClickListener
import java.util.ArrayList

/**
 * Helper class to create basic dialogs and popups
 */
class AppDialogUtil(private val context: Context) {
    /* create and show vertical list dialog*/
    fun createRadiusDialog(name: String?, progress: Int, dialogCallback: DialogCallback): Dialog {
        var progress = progress
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rv_sort_radius)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvName: TextView = dialog.findViewById<TextView>(R.id.tvName)
        val tvRadius: TextView = dialog.findViewById<TextView>(R.id.tvRadius)
        val tvDone: TextView = dialog.findViewById<TextView>(R.id.tvDone)
        val tvCancel: TextView = dialog.findViewById<TextView>(R.id.tvCancel)
        val seekBar: AppCompatSeekBar = dialog.findViewById(R.id.radius)
        if (progress < 1) {
            progress = 1
            seekBar.progress = 1
        }
        //double rad_km = Util.milesToKM(Double.parseDouble(String.valueOf(progress)+""));
        val rad_km: Int = Util.milesToKM((progress.toString() + "").toInt())
        tvRadius.setText("$progress Miles / $rad_km Km")
        seekBar.progress = progress
        tvCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        tvDone.setOnClickListener(View.OnClickListener {
            dialog.setOnDismissListener(null)
            dialogCallback.onCallback(dialog, seekBar, 1)
            dialog.dismiss()
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                var i = i
                if (i < 1) {
                    i = 1
                    seekBar.setProgress(1)
                }
                val rad_km: Int = Util.milesToKM((i.toString() + "").toInt())
                tvRadius.setText("$i Miles / $rad_km Km")
                //tvRadius.setText(String.valueOf(i) + " Miles");
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        dialog.setOnDismissListener { dialogCallback.onDismiss() }
        tvName.setText(name)
        dialog.show()
        return dialog
    }

    /* create and show vertical list dialog*/
    fun createCountryDialog(code: String, dialogCallback: DialogCallback): Dialog {
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rv_select_country)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val countryCodePicker: CountryCodePicker = dialog.findViewById(R.id.ccp)
        val tvDone: TextView = dialog.findViewById<TextView>(R.id.tvDone)
        val tvCancel: TextView = dialog.findViewById<TextView>(R.id.tvCancel)
        if (!code.isEmpty()) {
            countryCodePicker.setDefaultCountryUsingNameCode(code)
            countryCodePicker.resetToDefaultCountry()
        }
        tvDone.setOnClickListener(View.OnClickListener {
            dialog.setOnDismissListener(null)
            dialogCallback.onCallback(dialog, countryCodePicker, 1)
            dialog.dismiss()
        })
        tvCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(dialogInterface: DialogInterface) {
                dialogCallback.onDismiss()
            }
        })
        dialog.show()
        return dialog
    }

    /* create and show vertical list dialog*/
    fun createPostDialog(): Dialog {
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_post_public)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val progressBar: ProgressBar = dialog.findViewById<ProgressBar>(R.id.pbProcessing)
        dialog.show()
        return dialog
    }

    /* create and show vertical list dialog*/
    fun createPublicTermsDialog(activity: Activity): Dialog {
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_public_terms)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvTerms: TextView = dialog.findViewById<TextView>(R.id.tvName)
        val tvCancel: TextView = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvAccpet: TextView = dialog.findViewById<TextView>(R.id.tvAccept)


        /*Set text span*/
        // this is the text we'll be operating on
        val text = SpannableString(context.resources.getString(R.string.public_terms))
        // make  (characters 0 to 5) red
        val values: IntArray = Util.startEndPosition(
            context.resources.getString(R.string.public_terms),
            "'Terms and Conditions'"
        )
        // make (characters 6 to 11) one and a half time smaller than the textbox
        //text.setSpan(new RelativeSizeSpan(0.8f), values[0], values[1], 0);

        // make  (characters 12 to 17) display a toast message when touched
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                context.startActivity(
                    BaseIntent(
                        context as AppCompatActivity,
                        TermsWebViewActivity::class.java,
                        false
                    )
                )
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        text.setSpan(clickableSpan, values[0], values[1], 0)
        text.setSpan(
            ForegroundColorSpan(context.resources.getColor(R.color.sky_blue)),
            values[0],
            values[1],
            0
        )
        tvTerms.text = text
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
        tvTerms.highlightColor = Color.TRANSPARENT
        /*Set type face on texts*/tvAccpet.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            if (activity is HomeActivity) {
//                (activity as HomeActivity).setExploreInitialDialog()
            }
            //    context.startActivity(new BaseIntent((AppCompatActivity) context, PublicActivity.class, false));
        })
        tvCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            (context as AppCompatActivity).finish()
        })
        dialog.show()
        return dialog
    }

    /* create and show vertical list dialog*/
    /*fun createAddHyperlinkDialog(dialogCallback: DialogCallback): Dialog {
        val dialog = Dialog(context)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_hyperlink)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvOk: TextView = dialog.findViewById<TextView>(R.id.tvOk)
        val tvSkip: TextView = dialog.findViewById<TextView>(R.id.tvSkip)
        val tvCancel: TextView = dialog.findViewById<TextView>(R.id.tvCancel)
        val etLink: EditText = dialog.findViewById<EditText>(R.id.etLink)
        tvOk.setOnClickListener(View.OnClickListener {
            if (etLink.getText().toString().isEmpty()) {
                etLink.setError("Please add link")
                return@OnClickListener
            }
            if (!Patterns.WEB_URL.matcher(etLink.getText().toString()).matches()) {
                etLink.setError("Invalid link")
                return@OnClickListener
            }
            dialog.setOnDismissListener(null)
            dialogCallback.onCallback(dialog, etLink, 1)
            dialog.dismiss()
        })
        tvSkip.setOnClickListener(View.OnClickListener { view ->
            dialog.setOnDismissListener(null)
            dialogCallback.onCallback(dialog, view, 2)
            dialog.dismiss()
        })
        tvCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(dialogInterface: DialogInterface) {
                dialogCallback.onDismiss()
            }
        })
        dialog.show()
        return dialog
    }*/

    fun createListDialog(
        dialogItems: ArrayList<DialogItem>?,
        dialogCallback: DialogCallback
    ): Dialog { // disply designing your popoup window
        val listDialog = Dialog(context)
        listDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        listDialog.setContentView(R.layout.pop_up_window)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(listDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        listDialog.window!!.attributes = lp
        listDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val listDialogAdapter = ListDialogAdapter(context)
        val recyclerView: RecyclerView =
            listDialog.findViewById<View>(R.id.rv_pop_up) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = listDialogAdapter
        listDialogAdapter.setData(dialogItems!!)
        listDialogAdapter.OnClickListener { v, _, position ->
            listDialog.setOnDismissListener(null)
            listDialog.dismiss()
            dialogCallback.onCallback(listDialog, v, position)
        }
        listDialog.setOnDismissListener { dialogCallback.onDismiss() }
        listDialog.show()
        return listDialog
    }
}