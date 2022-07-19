package com.instaconnect.android.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.instaconnect.android.utils.PermissionUtil
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.fragment.app.Fragment
import com.instaconnect.android.R
import com.instaconnect.android.utils.Utils.visible

/**
 * Helper class for managing permissions at runtime
 */
class PermissionUtil(private val activity: Activity?) {
    /* check if audio permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForAudio(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if WRITE_EXTERNAL_STORAGE permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForWriteExternalStorage(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if READ_EXTERNAL_STORAGE permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForReadExternalStorage(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if ACCESS_FINE_LOCATION permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForFineLocation(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if ACCESS_COARSE_LOCATION permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForCoarseLocation(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if CAMERA permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForCamera(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if READ_SMS permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForReadSms(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_SMS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* check if SEND_SMS permission granted
    *  return true if permission granted
    *  return false if permission not granted
    */
    fun checkPermissionForSendSms(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    /* request permission to READ_SMS */
    fun requestPermissionForReadSms(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.READ_SMS
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_SMS),
                READ_SMS_REQUEST_CODE
            )
            true
        }
    }

    /* request permission to SEND_SMS */
    fun requestPermissionForSendSms(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.SEND_SMS
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.SEND_SMS),
                WRITE_SMS_REQUEST_CODE
            )
            true
        }
    }

    /* request permission to RECORD_AUDIO */
    fun requestPermissionForRecord(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                AUDIO_REQUEST_CODE
            )
            true
        }
    }

    /* request permission to WRITE_EXTERNAL_STORAGE */
    fun requestPermissionForExternalStorage(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
            true
        }
    }

    /* request permission to ACCESS_FINE_LOCATION */
    fun requestPermissionForFineLocation(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_CODE
            )
            false
        }
    }

    /* request permission to ACCESS_COARSE_LOCATION */
    fun requestPermissionForCoarseLocation(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                ACCESS_COARSE_LOCATION_CODE
            )
            true
        }
    }

    /* request permission to READ_EXTERNAL_STORAGE */
    fun requestPermissionForReadExternalStorage(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
            true
        }
    }

    /* request permission to CAMERA */
    fun requestPermissionForCamera(): Boolean {
        return if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.CAMERA
            )
        ) {
            false
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            true
        }
    }

    /* check permission for multiple components
    * @param permissions_group array of permissions
    */


    fun hasPermissionsGroup(permissions_group: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null && permissions_group != null) {
            for (permission in permissions_group) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /* request permission for multiple components
    * @param permissions_group array of permissions
    * @param permissions_group_code request code for permissions
    */
    fun requestPermissionsGroup(permissions_group: Array<String>?, permissions_group_code: Int) {
        ActivityCompat.requestPermissions(activity!!, permissions_group!!, permissions_group_code)
    }

    /* check if requested permissions granted */

    fun openPermissionDeniedPopup(context: Context, permissionTag : String){
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_permission_rational)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val tvMessage = dialog.findViewById<TextView>(R.id.text)
        val tvSetting = dialog.findViewById<TextView>(R.id.tvSetting)
        val tvHeader = dialog.findViewById<TextView>(R.id.tvHeader)
        tvSetting.visible(true)
        if(permissionTag == Constants.PERMISSION_TAG_STORAGE){
            tvHeader.text = "Your 'Storage' permission is turned off"
            tvMessage.text = "Please turn on your Storage permission from your phone settings to manage file with WatchTogether App."
        } else if(permissionTag == Constants.PERMISSION_TAG_LOCATION){
            tvHeader.text = "Your 'Location' permission is turned off"
            tvMessage.text = "Please turn on your Location Permission from your phone settings to see rooms near you."
        } else if(permissionTag == Constants.PERMISSION_TAG_CONTACTS){
            tvHeader.text = "Your 'Contacts' permission is turned off"
            tvMessage.text = "Please turn on your Contacts permission from your phone settings to Invite Contacts."
        } else if(permissionTag == Constants.PERMISSION_TAG_CAMERA){
            tvHeader.text = "Your 'Camera' permission is turned off"
            tvMessage.text = "Please turn on your Camera permission from your phone settings to To Capture Images"
        }
        val relMain: View = dialog.findViewById(R.id.rel_main)
        val vto: ViewTreeObserver = relMain.viewTreeObserver
        val imageView: ImageView = dialog.findViewById(R.id.img_bg)
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width: Int = relMain.measuredWidth
                val height: Int = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                /*imageView.setImageBitmap(
                    BlurKit.getInstance()!!.fastBlur(imageView, 8, 0.12.toFloat())
                )*/
            }
        })

        tvSetting.setOnClickListener { v: View? ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            dialog.dismiss()
        }
        tvOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    companion object {
        const val AUDIO_REQUEST_CODE = 1
        const val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2
        const val CAMERA_PERMISSION_REQUEST_CODE = 3
        const val PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE = 4
        const val ACCESS_FINE_LOCATION_CODE = 5
        const val ACCESS_COARSE_LOCATION_CODE = 6
        const val READ_SMS_REQUEST_CODE = 7
        const val WRITE_SMS_REQUEST_CODE = 8
        val STORAGE_CAMERA_AUDIO_PERMISSIONS_GROUP = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        fun isPermissionGranted(grantResults: IntArray): Boolean {
            var permissionGranted = true
            for (count in grantResults.indices) {
                if (grantResults[count] != PackageManager.PERMISSION_GRANTED) permissionGranted = false
            }
            return permissionGranted
        }

    }
}