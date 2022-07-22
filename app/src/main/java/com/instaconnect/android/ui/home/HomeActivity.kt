package com.instaconnect.android.ui.home

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.databinding.ActivityMainBinding
import com.instaconnect.android.model.Response1
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.fragment.add_post.CaptureFragment
import com.instaconnect.android.ui.fragment.explore.ExploreFragment
import com.instaconnect.android.ui.fragment.more_setting.MoreSettingsFragment
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.ui.fragment.worldwide.WorldwideFragment
import com.instaconnect.android.ui.friends.FriendsFragment
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter
import com.instaconnect.android.ui.watch_together_room.WatchTogetherVideoActivity
import com.instaconnect.android.utils.*
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class HomeActivity : AppCompatActivity(), LocationListener, View.OnClickListener {
    private var isPlusClicked: Boolean = false
    private val permissionsRequestCode = 112
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

    companion object {
        var userLocation: Location? = null
    }

    var userProfile: Post.UserProfile? = null
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HomeViewModel
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
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    var isONECHAT = false
    var isCall = false
    var isStream = false
    var permissionUtil: PermissionUtil? = null
    var currentVersion = ""
    private var worldwideFragment: WorldwideFragment = WorldwideFragment()
    var user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                HomeRepository(
                    MyApi.getInstance()
                )
            )
        )[HomeViewModel::class.java]

        currentVersion = packageManager.getPackageInfo(packageName, 0).versionName
        user_id = Prefrences.getPreferences(this, Constants.PREF_USER_ID).toString()
        initializeVariables()
        setOnClickListener()
        showStreamFragment()
        loadPreference()

        // notification status by default ON
        Prefrences.savePreferencesString(this, Constants.PREF_NOTIFICATION_STATUS, "1")

        try {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                if (result != null) {
                    updateToken(result)
                    Prefrences.savePreferencesString(
                        this@HomeActivity,
                        Constants.PREF_DEVICE_TOKEN,
                        result
                    )
                }
            }
        } catch (e: IllegalStateException) {
            //Firebase not initialized automatically, do it manually
            FirebaseApp.initializeApp(this)
        }


        Handler(Looper.getMainLooper()).postDelayed({
            if (managePermissions.checkPermissions()) {
                detectLocation()
            } else {
                permissionUtil!!.requestPermissionsGroup(Constants.appPermissionsForHomeScreen,
                    PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE)
            }
        }, 10000)


        // load Preference response handler
        viewModel.loadPreferenceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        if (it.value.response.alertArr!!.status == 1) {
                            openForceUpdateAppDialog(it.value.response)
                        }
                    }
                }
                is Resource.Failure -> {}
                else -> {}
            }
        }

        // update token response handler
        viewModel.updateTokenResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        Prefrences.savePreferencesString(
                            this,
                            Constants.PREF_NOTIFICATION_STATUS,
                            it.value.response.notificationStatus!!
                        )
                    }
                }
                is Resource.Failure -> {
                    Log.d("TAG", "onCreate: failed")
                }
                else -> {

                }
            }
        }
    }

    private fun updateToken(token: String) {
        viewModel.viewModelScope.launch {
            viewModel.updateToken(token, user_id!!, "android", "")
        }
    }

    private fun openForceUpdateAppDialog(response: Response1) {

        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_update_app)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
        val relMain = dialog.findViewById<View>(R.id.rel_main)
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(
                    BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat())
                )
            }
        })

        tvOk.setOnClickListener { v: View? ->
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(response.alertArr!!.button1Link))
            startActivity(browserIntent)
            dialog.dismiss()
        }

        if (!(this@HomeActivity).isFinishing) {
            dialog.show()
        }

    }

    private fun loadPreference() {
        viewModel.viewModelScope.launch {
            viewModel.loadPreference(currentVersion, "android", user_id!!)
        }
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
                                location =
                                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
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

                    Handler(Looper.getMainLooper()).postDelayed({
                        detectLocation()
                    }, 500)

                }
                Activity.RESULT_CANCELED -> Log.i(
                    "TAG",
                    "User chose not to make required location settings changes."
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        handleNotification()
    }

    private fun handleNotification() {
        if (intent.getStringExtra("from") != null && intent.getStringExtra("from")
                .equals("notification")
        ) {
            intent.putExtra(
                "from",
                ""
            ) // in case user come back this popup will not open second time
            val notificationType = intent.getStringExtra("type")
            if (notificationType == "send_invitation" || notificationType == "post_liked") {
                val notiBody = intent.getStringExtra("noti_body")
                val postData = intent.getStringExtra("post_data")
                var postObject: JSONObject? = null
                try {
                    postObject = JSONObject(postData)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                showStreamFragment()
                try {
                    handleInvitationNotification(postObject!!, notiBody!!)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (notificationType == "send_request") {
                if (intent.getStringExtra("title") != null) {
                    showFriendFragment("notification", intent.getStringExtra("title")!!)
                } else {
                    showFriendFragment("notification", "")
                }

            } else {
                showStreamFragment()
            }
        }
    }

    @Throws(JSONException::class)
    private fun handleInvitationNotification(postObject: JSONObject, noti_body: String) {
        var groupPassword = ""
        if (postObject.has("group_password")) {
            groupPassword = postObject["group_password"].toString()
        }
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_watch_video_notification)
        val ivPostProtected = dialog.findViewById<ImageView>(R.id.ivPostProtected)
        val tvNotificationDesc = dialog.findViewById<TextView>(R.id.tvNotificationDesc)
        tvNotificationDesc.text = noti_body
        val tvJoin = dialog.findViewById<TextView>(R.id.tvJoin)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        if (groupPassword.isEmpty()) {
            ivPostProtected.setImageResource(R.drawable.ic_unlock)
        } else {
            ivPostProtected.setImageResource(R.drawable.ic_lock)
        }
        val finalGroupPassword = groupPassword
        tvJoin.setOnClickListener { v: View? ->
            dialog.dismiss()
            Log.d("TAG", "after dialog dismiss: ")
            if (finalGroupPassword.isEmpty()) {
                Log.d("TAG", "Watch together video: ")
                goToWatchPartyRoom(postObject)
                Log.d("TAG", "postObject$postObject")
            } else {
                verifyVideoPasswordDialog(finalGroupPassword, postObject)
            }
        }
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
        val relMain = dialog.findViewById<View>(R.id.rel_main)
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(
                    BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat())
                )
            }
        })
        if (!(this@HomeActivity).isFinishing) {
            dialog.show()
        }
    }

    private fun verifyVideoPasswordDialog(groupPassword: String, postObject: JSONObject) {
        Log.d("TAG", "verifyVideoPasswordDialog: ")
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_private_room_video_password_new)
        val edtPassword = dialog.findViewById<EditText>(R.id.edtPassword)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
        val relMain = dialog.findViewById<View>(R.id.rel_main)
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(
                    BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat())
                )
            }
        })
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        tvOk.setOnClickListener { v: View? ->
            dialog.dismiss()
            val enteredPassword = edtPassword.text.toString()
            if (enteredPassword.length == 0) {
                Toast.makeText(this@HomeActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (enteredPassword == groupPassword) {
                    goToWatchPartyRoom(postObject)
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "You've entered wrong password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (!(this@HomeActivity).isFinishing) {
            dialog.show()
        }
    }

    private fun goToWatchPartyRoom(postObject: JSONObject) {
        val intent = BaseIntent(this, WatchTogetherVideoActivity::class.java, false)
        try {
            if (postObject.has("user_id")) intent.putExtra(
                "USER_ID",
                postObject["user_id"].toString()
            )
            if (postObject["youTubeVideoId"].toString() == "empty") {
                // for other type of video
                intent.putExtra("VIDEO_ID", postObject["hyperlink"].toString())
            } else {
                // for youtube video
                intent.putExtra("VIDEO_ID", postObject["youTubeVideoId"].toString())
            }
            intent.putExtra("COMING_FROM", "Home")
            if (postObject.has("mediaType")) intent.putExtra(
                "POST_ID",
                postObject["mediaType"].toString()
            ) else intent.putExtra("POST_ID", "")
            if (postObject.has("id")) intent.putExtra(
                "ACTUAL_POST_ID",
                postObject["id"].toString()
            ) else intent.putExtra("ACTUAL_POST_ID", "")
            if (postObject.has("group_name")) intent.putExtra(
                "ROOM_NAME",
                postObject["group_name"].toString()
            ) else intent.putExtra("ROOM_NAME", "")
            if (postObject.has("username")) intent.putExtra(
                "USER_NAME",
                postObject["username"].toString()
            ) else intent.putExtra("USER_NAME", "")
            if (postObject.has("yourReaction")) intent.putExtra(
                "POST_REACTION",
                postObject["yourReaction"].toString()
            ) else intent.putExtra("POST_REACTION", "0")
            if (postObject.has("userimage")) intent.putExtra(
                "USER_IMAGE",
                postObject["userimage"].toString()
            ) else intent.putExtra("USER_IMAGE", "")
            if (postObject.has("total_views")) intent.putExtra(
                "TOTAL_VIEWS",
                postObject["total_views"].toString()
            ) else intent.putExtra("TOTAL_VIEWS", "0")
            if (postObject.has("group_name")) intent.putExtra(
                "GROUP_NAME",
                postObject["group_name"].toString()
            ) else intent.putExtra("GROUP_NAME", "")
            if (postObject.has("likes")) intent.putExtra(
                "TOTAL_LIKES",
                VideoListAdapter.prettyCount(postObject["likes"].toString().toInt())
            ) else intent.putExtra(
                "TOTAL_LIKES",
                VideoListAdapter.prettyCount("0".toInt())
            )
        } catch (e: JSONException) {
            Log.d("TAG", "JSONException$e")
            e.printStackTrace()
        }
        Log.d("TAG", "Activity Started")
        startActivity(intent)
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
                    Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings")
                    try {
                        runOnUiThread(Runnable {
                            showPermissionDialog()
                        })

                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("TAG", "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i("TAG",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
            }
        }
    }

    private fun showPermissionDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_permission_rational)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val tvMessage = dialog.findViewById<TextView>(R.id.text)
        tvMessage.text = "Something wrong with the Enable location, Please Enable Location Manually."
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

        tvOk.setOnClickListener { v: View? ->
            val intent = Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(intent)
            dialog.dismiss()
        }
        if (!(this@HomeActivity).isFinishing) {
            dialog.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    detectLocation()
                    if (isPlusClicked)
                        showAddPostFragment()
                } else {

                    if (!permissionUtil!!.hasPermissionsGroup(Constants.appPermissionsForStorage)) {
                        permissionUtil!!.openPermissionDeniedPopup(this, Constants.PERMISSION_TAG_STORAGE)
                    } else if (!permissionUtil!!.hasPermissionsGroup(Constants.appPermissionsForLocation)) {
                        permissionUtil!!.openPermissionDeniedPopup(this, Constants.PERMISSION_TAG_LOCATION)
                    }
                }
                return
            }
        }
    }


    override fun onLocationChanged(p0: Location) {
        userLocation = p0
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.relPlus -> {
                isPlusClicked = true
                if (managePermissions.checkPermissions()) {
                    detectLocation()
                    showAddPostFragment()
                } else {
                    permissionUtil!!.requestPermissionsGroup(
                        Constants.appPermissionsForHomeScreen,
                        PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE
                    )
                }

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

        fragmentUtil!!.fragment(moreSettingsFragment, R.id.fl_container_home_other, true)
            .skipStack().commit()

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

        fragmentUtil!!.fragment(friendsFragment, R.id.fl_container_home_other, true).skipStack()
            .commit()
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
        isPlusClicked = false
        val captureFragment = CaptureFragment()
        val bundle = Bundle()
        bundle.putString("CaptureType", "WatchTogether")
        bundle.putString("PostType", "")
        captureFragment.arguments = bundle
        fragmentUtil!!.fragment(captureFragment, R.id.fl_container_home_other, true).skipStack()
            .commit()
        binding.relLive.setBackgroundResource(0)
        binding.relFriend.setBackgroundResource(0)
        binding.relPlus.setBackgroundResource(R.drawable.layout_rounded_white_glass)
        binding.relSetting.setBackgroundResource(0)
    }

}