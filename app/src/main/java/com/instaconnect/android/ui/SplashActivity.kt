package com.instaconnect.android.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.instaconnect.android.R
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.login.LoginActivity
import com.instaconnect.android.utils.Constants.LOGIN_STATUS
import com.instaconnect.android.utils.ManagePermissions
import com.instaconnect.android.utils.PermissionUtil
import com.instaconnect.android.utils.Prefrences

class SplashActivity : AppCompatActivity() {
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    var isLogin: Boolean = false

    var list = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        hashFromSHA1("32:3E:27:38:E3:DC:03:DD:6B:1E:AE:B4:A5:90:EB:CF:E2:40:0B:1A")

        managePermissions = ManagePermissions(this, list.toList(), permissionsRequestCode)
        setUpNavigation()
    }

    private fun setUpNavigation() {

        isLogin = Prefrences.getBooleanPreferences(application, LOGIN_STATUS)

        if (isLogin) {
            // Log.e(TAG,it.uid.toString() +".."+ it.name)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (managePermissions.checkPermissions()) {
                    Handler().postDelayed({
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
        } else {
            if (managePermissions.checkPermissions()) {
                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && PermissionUtil.isPermissionGranted(grantResults)) {
            if (isLogin) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun hashFromSHA1(sha1: String) {
        val arr = sha1.split(":").toTypedArray()
        val byteArr = ByteArray(arr.size)
        for (i in arr.indices) {
            byteArr[i] = Integer.decode("0x" + arr[i]).toByte()
        }
        Log.e("hash", Base64.encodeToString(byteArr, Base64.NO_WRAP))
    }
}