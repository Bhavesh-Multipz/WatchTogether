package com.instaconnect.android.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityMainBinding
import com.instaconnect.android.ui.fragment.explore.ExploreFragment
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.ui.fragment.worldwide.WorldwideFragment
import com.instaconnect.android.utils.FragmentUtil
import com.instaconnect.android.utils.ManagePermissions
import com.instaconnect.android.utils.Utils.toast
import com.instaconnect.android.utils.ViewUtil

class HomeActivity : AppCompatActivity(), LocationListener, View.OnClickListener {
    private val permissionsRequestCode = 112
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

    companion object{
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
    private var fragmentUtil : FragmentUtil? = null
    var list = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    var isONECHAT = false
    var isCall = false
    var isStream = false

    private var worldwideFragment: WorldwideFragment = WorldwideFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeVariables()
        setOnClickListener()

        showStreamFragment()

        //        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession arg1) {
//                if (hostname.equalsIgnoreCase("http://99.79.19.208/webapp/terms_and_conditions.html")||
//                        hostname.equalsIgnoreCase("http://99.79.19.208/webapp/dev/api/") ||
//                        hostname.equalsIgnoreCase("http://99.79.19.208/webapp/dev/uploads/")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
        //Subscribe for RxBus REFRESH event.
        //see {@link rana.jatin.core.model.Event#REFRESH}
        //RxBus.instance!!.subscribe(Event.REFRESH.name, this, String::class.java, refreshConsumer)
    }

    private fun showStreamFragment() {
//        dataManager.prefHelper().setWatchList(true)
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

        if (managePermissions.checkPermissions()) {
            detectLocation()
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
                            Log.d("Network", "Network Enabled")
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
        when(v!!.id){
            R.id.relPlus ->{
                showAddPostFragment()
            }
            R.id.relSetting ->{
                showSettingFragment()
            }
            R.id.relFriend ->{
                showFriendFragment("","")
            }

            R.id.relLive ->{
                showStreamFragment()
            }
        }
    }

    private fun showSettingFragment() {

    }

    private fun showFriendFragment(s: String, s1: String) {

    }

    //    private void showMoreFragment() {
    //        pager_headers_flv.setVisibility(View.GONE);
    //        explorii_header_inc.setVisibility(View.GONE);
    //        RxBus.getInstance().publish(BusMessage.SWITCH_CONATINERS.name(), R.id.fl_container_home_other);
    //        fragmentUtil.fragment(new MoreFragment(), R.id.fl_container_home_other, true).commit();
    //    }

    private fun showExploreFragment() {

        val bundle = Bundle()
        exploreFragment!!.arguments = bundle

//        RxBus.instance!!.publish(BusMessage.SWITCH_CONATINERS.name, R.id.fl_container_home_other)

        fragmentUtil!!.fragment(
            exploreFragment,
            R.id.fl_container_home_other,
            !exploreFragment!!.isAdded
        ).skipStack().commit()

       /* worldwideFragment = WorldwideFragment()
        val bundle = Bundle()
        worldwideFragment.setArguments(bundle)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container_home_other, worldwideFragment, "worldwide_frg").commit()*/
    }

    private fun showAddPostFragment() {

    }
}