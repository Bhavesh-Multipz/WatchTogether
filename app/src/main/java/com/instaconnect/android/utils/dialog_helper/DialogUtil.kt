package com.instaconnect.android.utils.dialog_helper

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.utils.models.DialogItem
import io.alterac.blurkit.BlurKit
import java.lang.Exception
import java.util.*

/**
 * Helper class to create basic dialogs and popups
 */
class DialogUtil(private val context: Context) {
    var header: String? = null

    companion object{

        fun showAlertDialog(
            context: Context,
            title: String?,
            message: String?,
            setPositiveButton: String?,
            setNegativeButton: String?,
            dialogCallback: AlertDialogCallback
        ): AlertDialog {
            return showAlertDialog(
                context,
                title,
                message,
                setPositiveButton,
                -1,
                setNegativeButton,
                -1,
                dialogCallback
            )
        }

        fun showAlertDialog(
            context: Context,
            title: String?,
            message: String?,
            setPositiveButton: String?,
            @ColorRes positiveBtnColor: Int,
            setNegativeButton: String?,
            @ColorRes negativeBtnColor: Int,
            dialogCallback: AlertDialogCallback
        ): AlertDialog {
            val alertDialogBuilder = AlertDialog.Builder(
                context
            )

            // set title
            alertDialogBuilder.setTitle(title)

            // set dialog message
            alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(setPositiveButton) { dialog, id -> // if this button is clicked, close
                    // current activity
                    dialog.cancel()
                    dialogCallback.onPositiveButton(dialog)
                }
                .setNegativeButton(setNegativeButton) { dialog, id -> // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel()
                    dialogCallback.onNegativeButton(dialog)
                }

            // create alert dialog
            val alertDialog = alertDialogBuilder.create()
            // show it
            alertDialog.show()
            try {
                if (positiveBtnColor != -1) alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(
                        context.resources.getColor(positiveBtnColor)
                    )
                if (negativeBtnColor != -1) alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(
                        context.resources.getColor(negativeBtnColor)
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return alertDialog
        }

        fun createListDialog(
            context: Context,
            dialogItems: ArrayList<DialogItem>,
            dialogCallback: DialogCallback
        ): Dialog {
            // disply designing your popoup window
            val listDialog = Dialog(context)
            listDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            listDialog.setContentView(R.layout.pop_up_window)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(listDialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            listDialog.window!!.attributes = lp
            listDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val listDialogAdapter = ListDialogAdapter(context)
            val recyclerView = listDialog.findViewById<View>(R.id.rv_pop_up) as RecyclerView
            val txt_dialogHeader = listDialog.findViewById<TextView>(R.id.txt_dialogHeader)
            val imageView = listDialog.findViewById<ImageView>(R.id.img_bg)
            val linMain = listDialog.findViewById<RelativeLayout>(R.id.linMain)
            val linearLayoutManager = LinearLayoutManager(
                context
            )
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = listDialogAdapter
            //recyclerView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            listDialogAdapter.setData(dialogItems)
            listDialogAdapter.OnClickListener { v, viewHolder, position ->
                listDialog.setOnDismissListener(null)
                listDialog.dismiss()
                dialogCallback.onCallback(listDialog, v, position)
            }
            listDialog.setOnDismissListener { dialogCallback.onDismiss() }
            val vto = linMain.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                override fun onGlobalLayout() {
                    linMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val width = linMain.measuredWidth
                    val height = linMain.measuredHeight
                    Log.e("View Height", "$width...$height")
                    imageView.layoutParams.height = height
                    imageView.layoutParams.width = width
                    imageView.setImageBitmap(
                        BlurKit.getInstance().fastBlur(imageView, 18, 0.12.toFloat())
                    )
                }
            })
            listDialog.show()
            return listDialog
        }

        fun createListDialog(
            context: Context,
            dialogItems: ArrayList<DialogItem>,
            dialogCallback: DialogCallback,
            header: String?
        ): Dialog {
            val listDialog = Dialog(context)
            listDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            listDialog.setContentView(R.layout.pop_up_window)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(listDialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            listDialog.window!!.attributes = lp
            listDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val listDialogAdapter = ListDialogAdapter(context)
            val recyclerView = listDialog.findViewById<View>(R.id.rv_pop_up) as RecyclerView
            val txt_dialogHeader = listDialog.findViewById<TextView>(R.id.txt_dialogHeader)
            val linMain = listDialog.findViewById<RelativeLayout>(R.id.linMain)
            val imageView = listDialog.findViewById<ImageView>(R.id.img_bg)
            val view = listDialog.findViewById<View>(R.id.view)
            view.visibility = View.VISIBLE
            txt_dialogHeader.visibility = View.VISIBLE
            txt_dialogHeader.text = header
            val linearLayoutManager = LinearLayoutManager(
                context
            )
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = listDialogAdapter
            listDialogAdapter.setData(dialogItems)
            listDialogAdapter.OnClickListener { v, _, position ->
                listDialog.setOnDismissListener(null)
                listDialog.dismiss()
                dialogCallback.onCallback(listDialog, v, position)
            }
            listDialog.setOnDismissListener { dialogCallback.onDismiss() }
            val vto = linMain.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                override fun onGlobalLayout() {
                    linMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val width = linMain.measuredWidth
                    val height = linMain.measuredHeight
                    Log.e("View Height", "$width...$height")
                    imageView.layoutParams.height = height
                    imageView.layoutParams.width = width
                    imageView.setImageBitmap(
                        BlurKit.getInstance().fastBlur(imageView, 18, 0.12.toFloat())
                    )
                }
            })

            listDialog.show()
            return listDialog
        }
    }


    /* create and show vertical PopupWindow*/
    fun createListPopUp(
        dialogItems: ArrayList<DialogItem?>?,
        popUpCallback: PopUpCallback
    ): PopupWindow { // disply designing your popoup window
        val popupWindow = PopupWindow(context) // inflet your layout or diynamic add view
        val view: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.conetxt_pop_up_window, null)
        popupWindow.isFocusable = true
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.contentView = view
        val popUpWindowAdapter = PopUpWindowAdapter(context)
        val recyclerView = view.findViewById<View>(R.id.rv_pop_up) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(
            context
        )
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = popUpWindowAdapter
        popUpWindowAdapter.setData(dialogItems)
        popUpWindowAdapter.OnClickListener { v, viewHolder, position ->
            popupWindow.dismiss()
            popUpCallback.onCallback(popupWindow, v, position)
        }
        //popupWindow.showAsDropDown(view,-40, 18);
        return popupWindow
    }

    /* create and show Dialog
    *  @param title of the dialog
    *  @param view to set as content view of dialog
    */
    fun createDialog(title: String?, view: View?): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(view!!)
        dialog.setTitle(title)
        dialog.show()
        return dialog
    }

    /* create and show Dialog
    *  @param title of the dialog
    *  @param viewId to set as content view of dialog
    */
    fun createDialog(title: String?, @LayoutRes view: Int): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(view)
        dialog.setTitle(title)
        dialog.show()
        return dialog
    }

    /* create and show DatePickerDialog
    *  @param listener on the dialog
    *  @param theme of dialog. see {@link android.app.AlertDialog}
    */
    fun showDatePicker(
        listener: DatePickerDialog.OnDateSetListener?,
        theme: Int
    ): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datepickerdialog = DatePickerDialog(context, theme, listener, year, month, day)
        datepickerdialog.show()
        return datepickerdialog
    }

    /* create and show TimePickerDialog
    *  @param listener on the dialog
    */
    fun showTimePicker(
        listener: TimePickerDialog.OnTimeSetListener?,
        is24HourView: Boolean
    ): TimePickerDialog {
        // Get Current time
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        return TimePickerDialog(context, listener, hour, minute, is24HourView)
    }
}