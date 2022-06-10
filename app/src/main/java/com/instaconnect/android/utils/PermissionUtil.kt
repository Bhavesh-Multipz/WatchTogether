package com.instaconnect.android.utils

import android.Manifest
import android.app.Activity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.instaconnect.android.utils.PermissionUtil
import android.os.Build

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