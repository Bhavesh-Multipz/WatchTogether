package com.instaconnect.android.ui.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.installations.InstallationTokenResult
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityMainBinding
import com.instaconnect.android.ui.fragment.add_post.CaptureFragment
import com.instaconnect.android.ui.fragment.explore.ExploreFragment
import com.instaconnect.android.ui.fragment.more_setting.MoreSettingsFragment
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.ui.fragment.worldwide.WorldwideFragment
import com.instaconnect.android.ui.friends.FriendsFragment
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.Utils.toast


class HomeActivity : AppCompatActivity(), LocationListener, View.OnClickListener {
    private val permissionsRequestCode = 112
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

    companion object {
        var userLocation: Location? = null
    }

    var userProfile: Post.UserProfile? = null
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    private lateinit var binding: ActivityMainBinding
    private lateinit var managePermissions: ManagePermissions
    private var locationManager: LocationManager? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location: Location? = null
    var latitude = 0.0
    var longitude = 0.0
    lateinit var viewUtil: ViewUtil
    private var exploreFragment: ExploreFragment? = null
    private var fragmentUtil: FragmentUtil? = null
    var list = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    var isONECHAT = false
    var isCall = false
    var isStream = false
    var permissionUtil:PermissionUtil? = null

    private var worldwideFragment: WorldwideFragment = WorldwideFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeVariables()
        setOnClickListener()
        showStreamFragment()
        // notification status by default ON
        Prefrences.savePreferencesString(this, Constants.PREF_NOTIFICATION_STATUS,"1")

        FirebaseInstallations.getInstance().getToken(false)
            .addOnCompleteListener(object : OnCompleteListener<InstallationTokenResult?> {
                override fun onComplete(@NonNull task: Task<InstallationTokenResult?>) {
                    if (!task.isSuccessful) {
                        return
                    }
                    val token: String = task.result!!.token
                    Prefrences.savePreferencesString(this@HomeActivity, Constants.PREF_DEVICE_TOKEN, token)
                }
            })
    }

    private fun showStreamFragment() {
        fragmentUtil!!.removeFragment(exploreFragment)
        isONECHAT = false
        isStream = true
        showExploreFragment()
        exploreFragment!!.rePlayVideo()
        binding.relLive.setBackgroundResource(R.drawable.layout_rounded_white_glass)
        binding.relFriend.setBackgroundResource(0)
        binding.relPlus.setBackgroundResource(0)
        binding.relSetting.setBackgroundResource(0)
    }

    private fun initializeVariables() {
        viewUtil = ViewUtil(this)
        managePermissions = ManagePermissions(this, list.toList(), permissionsRequestCode)
        permissionUtil = PermissionUtil(this)
        if (managePermissions.checkPermissions()) {
            detectLocation()
        } else {
            permissionUtil!!.requestPermissionsGroup(Constants.appPermissionsForHomeScreen,
                PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE)
        }
        exploreFragment = ExploreFragment()
        fragmentUtil = FragmentUtil(supportFragmentManager)
    }

    fun setExploreInitialDialog() {
        exploreFragment!!.setInitialDialog()
    }

    private fun setOnClickListener() {
        binding.relLive.setOnClickListener(this)
        binding.relFriend.setOnClickListener(this)
        binding.relPlus.setOnClickListener(this)
        binding.relSetting.setOnClickListener(this)
    }

    private fun detectLocation(): Location? {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                displayLocationSettingsRequest(this)
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        viewUtil.showPermissionSnack()
                    } else {

                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )

                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                                userLocation = location
                            }
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            viewUtil.showPermissionSnack()
                        } else {
                            locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                            )
                            Log.d("GPS", "GPS Enabled")
                            if (locationManager != null) {
                                location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    latitude = location!!.latitude
                                    longitude = location!!.longitude
                                    userLocation = location
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            123456 -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i("TAG", "User agreed to make required location settings changes.")

                    Handler().postDelayed({
                        detectLocation()
                    },500)

                }
                Activity.RESULT_CANCELED -> Log.i("TAG",
                    "User chose not to make required location settings changes.")
            }
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000 / 2
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> Log.i("TAG", "All location settings are satisfied.")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i("TAG","Location settings are not satisfied. Show the user a dialog to upgrade location settings")
                    try {
                        status.startResolutionForResult(this@HomeActivity, 123456)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("TAG","PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i("TAG","Location settings are inadequate, and cannot be fixed here. Dialog not created.")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    detectLocation()
                } else {
                    this.toast("Permissions denied.")
                }
                return
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        userLocation = location
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.relPlus -> {
                showAddPostFragment()
            }

            R.id.relSetting -> {
                showSettingFragment()
            }

            R.id.relFriend -> {
                showFriendFragment("", "")
            }

            R.id.relLive -> {
                showStreamFragment()
            }
        }
    }

    private fun showSettingFragment() {
        val moreSettingsFragment = MoreSettingsFragment()
        val bundle = Bundle()
        moreSettingsFragment.setArguments(bundle)

        fragmentUtil!!.fragment(moreSettingsFragment, R.id.fl_container_home_other, true).skipStack().commit()

        binding.relLive.setBackgroundResource(0)
        binding.relFriend.setBackgroundResource(0)
        binding.relPlus.setBackgroundResource(0)
        binding.relSetting.setBackgroundResource(R.drawable.layout_rounded_white_glass)
    }

    private fun showFriendFragment(from: String, title: String) {

        val friendsFragment = FriendsFragment()
        val bundle = Bundle()
        bundle.putString("from", from)
        bundle.putString("title", title)
        friendsFragment.arguments = bundle

        fragmentUtil!!.fragment(friendsFragment, R.id.fl_container_home_other, true).skipStack().commit()
        binding.relLive.setBackgroundResource(0)
        binding.relFriend.setBackgroundResource(R.drawable.layout_rounded_white_glass)
        binding.relPlus.setBackgroundResource(0)
        binding.relSetting.setBackgroundResource(0)
    }

    private fun showExploreFragment() {
        val bundle = Bundle()
        exploreFragment!!.arguments = bundle
        fragmentUtil!!.fragment(
            exploreFragment,
            R.id.fl_container_home_other,
            !exploreFragment!!.isAdded
        ).skipStack().commit()
    }

    private fun showAddPostFragment() {

        val captureFragment = CaptureFragment()
        val bundle = Bundle()
        bundle.putString("CaptureType", "WatchTogether")
        bundle.putString("PostType", "")
        captureFragment.arguments = bundle
        fragmentUtil!!.fragment(captureFragment, R.id.fl_container_home_other, true).skipStack().commit()
        binding.relLive.setBackgroundResource(0)
        binding.relFriend.setBackgroundResource(0)
        binding.relPlus.setBackgroundResource(R.drawable.layout_rounded_white_glass)
        binding.relSetting.setBackgroundResource(0)
    }

}