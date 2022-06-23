package com.instaconnect.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.text.TextUtils
import android.content.pm.ResolveInfo
import android.content.pm.PackageManager
import android.net.Uri
import java.util.*

class IntentUtil {
    companion object{
        var PICK_IMAGE_CODE = 1
        var PICK_VIDEO_CODE = 2
        var PICK_IMAGE_VIDEO_CODE = 3

        fun sendSMS(context: Context, smsText: String?, smsNumber: String) {
            val uri = Uri.parse("smsto:$smsNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", smsText)
            context.startActivity(intent)
        }

        fun shareOnFacebook(appCompatActivity: Activity, urlToShare: String) {
            var intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, if (!TextUtils.isEmpty(urlToShare)) urlToShare else "")
            var facebookAppFound = false
            val matches = appCompatActivity.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (info in matches) {
                if (info.activityInfo.packageName.lowercase(Locale.getDefault()).startsWith("com.facebook.katana") ||
                    info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.lite")
                ) {
                    intent.setPackage(info.activityInfo.packageName)
                    facebookAppFound = true
                    break
                }
            }
            if (facebookAppFound) {
                appCompatActivity.startActivity(intent)
            } else {
                val sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=$urlToShare"
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl))
                appCompatActivity.startActivity(intent)
            }
        }
        fun emailIntent(activity: Activity, address: String, subject: String?, body: String?) {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            activity.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }
    fun imageIntent(activity: Activity, requestCode: Int): Intent {
        val pickPhoto = Intent(Intent.ACTION_GET_CONTENT)
        pickPhoto.type = "*/*"
        val mimetypes = arrayOf("image/*")
        pickPhoto.putExtra("android.intent.extra.MIME_TYPES", mimetypes)
        activity.startActivityForResult(pickPhoto, requestCode)
        return pickPhoto
    }

    fun videoIntent(activity: Activity, requestCode: Int): Intent {
        val pickVideo = Intent(Intent.ACTION_GET_CONTENT)
        pickVideo.type = "*/*"
        val mimetypes = arrayOf("video/*")
        pickVideo.putExtra("android.intent.extra.MIME_TYPES", mimetypes)
        activity.startActivityForResult(pickVideo, requestCode)
        return pickVideo
    }

    fun imageVideoIntent(activity: Activity, requestCode: Int): Intent {
        val imageVideo = Intent(Intent.ACTION_GET_CONTENT)
        imageVideo.type = "*/*"
        val mimetypes = arrayOf("image/*", "video/*")
        imageVideo.putExtra("android.intent.extra.MIME_TYPES", mimetypes)
        activity.startActivityForResult(imageVideo, requestCode)
        return imageVideo
    }

    /*public static boolean pickGooglePlace(Activity activity,int requestCode) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            activity.startActivityForResult(builder.build(activity), requestCode);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }*/
    fun contactPicker(activity: Activity, requestCode: Int) {
        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        )
        activity.startActivityForResult(contactPickerIntent, requestCode)
    }

}