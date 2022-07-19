package com.instaconnect.android.ui.fragment.explore

import android.Manifest
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
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.hbb20.CountryCodePicker
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.databinding.ExploreFragmentBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.ui.fragment.worldwide.WorldwideFragment
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.dialog_helper.DialogCallback
import com.instaconnect.android.utils.dialog_helper.DialogUtil
import com.instaconnect.android.utils.models.DialogItem
import gun0912.tedimagepicker.util.ToastUtil
import io.reactivex.functions.Consumer

class ExploreFragment : BaseFragment<ExploreViewModel, ExploreFragmentBinding, ExploreRepository>(),
    View.OnClickListener, LocationListener {
    private val permissionsRequestCode = 112
    private var locationManager: LocationManager? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private val mainDialogItem: ArrayList<DialogItem> = ArrayList<DialogItem>()
    private var worldwideFragment: WorldwideFragment = WorldwideFragment()
    //private val trendingFragment: TrendingFragment = TrendingFragment(this)
    private lateinit var managePermissions: ManagePermissions
    var isPlayVideoDueToChat = false
    private var permissionUtil: PermissionUtil? = null
    var selectedItem = "Worldwide"
    var appDialogUtil: AppDialogUtil? = null
    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location: Location? = null
    var latitude = 0.0
    var longitude = 0.0
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    lateinit var viewUtil: ViewUtil

    var list = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    private val refresh =
        Consumer<Boolean> { refresh ->
            if (refresh) {
                requireActivity().runOnUiThread {
                    println("EXPLORE : IN REFRESH CONSUMER")
                    worldwideFragment.onRefresh()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showMainFragment()
        createMainPopUp()
        permissionUtil = PermissionUtil(requireActivity())
        managePermissions = ManagePermissions(requireActivity(), list.toList(), permissionsRequestCode)
        viewUtil = ViewUtil(requireActivity())
        appDialogUtil = AppDialogUtil(requireContext())
        Prefrences.savePreferencesString(requireContext(), Constants.PREF_SEARCH_BY_DISTANCE, "0")

    }

    fun setInitialDialog() {
        /*RxBus.instance!!.subscribe(
            BusMessage.REFRESH_PUBLIC.name, this,
            Boolean::class.java, refresh
        )*/
//        getViewModel().dataManager.prefHelper().putBoolean(AppPreferencesHelper.Key.PUBLIC_FIRST_RUN.name(), true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.communityIv.setOnClickListener(this)
        binding.binocularIv.setOnClickListener(this)
        binding.mypageIv.setOnClickListener(this)
        binding.myfavIv.setOnClickListener(this)
        binding.shareIv.setOnClickListener(this)
        binding.tvTitle.setOnClickListener(this)
        binding.ivGrid.setOnClickListener(this)
        binding.ivList.setOnClickListener(this)
        binding.ivGrid.setColorFilter(resources.getColor(R.color.dark_gray))
    }

    private fun createMainPopUp() {
        mainDialogItem.add(DialogItem(getString(R.string.public_profile), R.color.sky_blue, true))
        mainDialogItem.add(DialogItem(getString(R.string.sort_by_near_me), R.color.sky_blue, true))
        mainDialogItem.add(
            DialogItem(
                getString(R.string.change_bg_trending),
                R.color.sky_blue,
                true
            )
        )
        mainDialogItem.add(DialogItem(getString(R.string.hyperlink), R.color.sky_blue, true))
        mainDialogItem.add(DialogItem(getString(R.string.reset_public), R.color.sky_blue, true))
        mainDialogItem.add(DialogItem(getString(R.string.cancel), R.color.colorAccent))
    }

    private fun showMainFragment() {
        if (!worldwideFragment.isAdded) {
            println("EXPLORE ADD: IN SHOW WORLD WIDE")
            val bundle = Bundle()
            worldwideFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.main_frg_fl, worldwideFragment, "worldwide_frg").commit()
//            RxBus.instance!!.publish(BusMessage.SWITCH_CONATINERS.name, R.id.main_frg_fl)
        } else {
            worldwideFragment = WorldwideFragment()
            val bundle = Bundle()
            worldwideFragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_frg_fl, worldwideFragment, "worldwide_frg").commit()
            //RxBus.instance!!.publish(BusMessage.SWITCH_CONATINERS.name, R.id.main_frg_fl)
        }
        Log.e("ExploreAdd", "IN SHOW WORLD WIDE")
        //  fragmentUtil.fragment(new WorldwideFragment(), R.id.main_frg_fl, false).skipStack().commit();
    }

    override fun getViewModel() = ExploreViewModel::class.java

    fun rePlayVideo() {
        if (isPlayVideoDueToChat) {
            resumePlayback()
            isPlayVideoDueToChat = false
        }
    }

    fun resumePlayback() {
        try {
            val handler = Handler()
            handler.postDelayed({ worldwideFragment.resumeAnyPlayback() }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = ExploreFragmentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = ExploreRepository(
        MyApi.getInstance()
    )

    override fun onClick(view: View) {
        when (view.id) {
            /*R.id.rbWorldwide -> {}
            R.id.rbNearMe -> {}
            R.id.rbTrending -> {}*/
            R.id.community_iv -> if (permissionUtil!!.hasPermissionsGroup(Constants.appPermissionsForHomeScreen)) {
                //CJ: UPDATED AS ADDED 3rd parameter in Trending
                /*startActivity(
                    BaseIntent(requireActivity(), TrendingViewActivity::class.java, false)
                        .setModel(
                            Trending(
                                getString(R.string.my_community),
                                R.drawable.mycommunity,
                                getString(R.string.my_community)
                            )
                        )
                )*/
            } else {
                permissionUtil!!.requestPermissionsGroup(
                    Constants.appPermissionsForHomeScreen,
                    PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE
                )
            }
            R.id.mypage_iv -> { /*startActivity(Intent(activity, MoreActivity::class.java))*/
            }
            R.id.myfav_iv -> {}
            R.id.ivGrid -> {
                if (worldwideFragment.videoListAdapter!!.postsLists.isNotEmpty()) {
                    binding.ivGrid.clearColorFilter()
                    binding.ivList.setColorFilter(resources.getColor(R.color.dark_gray))
                    worldwideFragment.setLayoutManagerForVideo("Grid")
                }

            }
            R.id.ivList -> {
                if (worldwideFragment.videoListAdapter!!.postsLists.isNotEmpty()) {
                    binding.ivList.clearColorFilter()
                    binding.ivGrid.setColorFilter(resources.getColor(R.color.dark_gray))
                    worldwideFragment.setLayoutManagerForVideo("List")
                }
            }
            R.id.shareIv -> {
                val pop_list = java.util.ArrayList<DialogItem>()
                pop_list.add(DialogItem("Share with others", R.color.sky_blue, true))
                pop_list.add(DialogItem("Invite Contacts", R.color.sky_blue, true))
                pop_list.add(DialogItem("Cancel", R.color.red, true))
                DialogUtil.createListDialog(requireContext(), pop_list, object : DialogCallback {
                    override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
                        when (position) {
                            0 -> {
                                val link =
                                    "Hi, " + getString(R.string.share_msg_txt) + "https://play.google.com/store/apps/details?id=" + activity!!.packageName
                                Log.d("LINK:", link)
                                ShareUtil.shareTextUrl(getString(R.string.app_name), link, requireActivity())
                            }
                            1 -> ToastUtil.showToast("Invite contact Pending")/*startActivity(
                                BaseIntent(
                                    activity,
                                    InviteContactActivity::class.java, false
                                )
                            )*/
                        }
                    }

                    override fun onDismiss() {

                    }
                })
            }
            R.id.binocular_iv -> {
                if (managePermissions.checkPermissions()) {
                    detectLocation()
                    filterDialog()
                } else {
                    permissionUtil!!.requestPermissionsGroup(Constants.appPermissionsForHomeScreen,
                        PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE)
                }
            }
            R.id.tvTitle -> {
                /*if (selection_tv != null) {
                    selection_tv.setText("Exploring Worldwide")
                }*/
                // setDefaultFilterationValues(0);
//                showTrendingFragmentWithArgument(0, true)
                /*dataManager.prefHelper().setHasCountry(false)
                dataManager.prefHelper().setHasDistance(false)*/
                showMainFragment()
            }
        }
    }

    private fun filterDialog() {
        val binocular_list = java.util.ArrayList<DialogItem>()
        binocular_list.add(
            DialogItem(
                isItemSelected("Explore by Country"),
                "Explore by Country",
                R.color.sky_blue,
                true
            )
        )
        binocular_list.add(
            DialogItem(
                isItemSelected("Explore by Distance"),
                "Explore by Distance",
                R.color.sky_blue,
                true
            )
        )
        binocular_list.add(
            DialogItem(
                isItemSelected("Worldwide"),
                "Worldwide",
                R.color.sky_blue,
                true
            )
        )
        binocular_list.add(DialogItem(false, "Cancel", R.color.red, true))

        DialogUtil.createListDialog(requireContext(), binocular_list, object : DialogCallback {

            override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
                when (position) {
                    0 -> {
                        selectedItem = "Explore by Country"
                        showCountryDialog()
                    }
                    1 -> {
                        selectedItem = "Explore by Distance"
                        showDistanceDialog()
                    }
                    2 -> {
                        selectedItem = "Worldwide"
                        Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_COUNTRY, false)
                        Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_DISTANCE, false)
                        showMainFragment()
                    }
                    3 -> dialog!!.dismiss()
                }
            }

            override fun onDismiss() {}
        })
    }

    private fun isItemSelected(selectType: String): Boolean {
        return selectedItem == selectType
    }

    private fun showCountryDialog() {
        val code: String = Prefrences.getPreferences(requireContext(), "explore_country")!!
        appDialogUtil!!.createCountryDialog(code, object : DialogCallback {

            override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
                if (position == 1) {
                    Prefrences.savePreferencesString(requireContext(),
                        "explore_country",
                        (v as CountryCodePicker).selectedCountryName)
                    Prefrences.savePreferencesString(requireContext(),
                        "explore_country_code",
                        (v as CountryCodePicker).selectedCountryNameCode)
                    /*dataManager.prefHelper().setCountry(
                        "explore_country",
                        (v as CountryCodePicker).getSelectedCountryNameCode(),
                        (v as CountryCodePicker).getSelectedCountryName()
                    )*/
                    /*selection_tv.setText(
                        "Exploring " + dataManager.prefHelper().getCountry("explore_country")
                    )*/
                    // setDefaultFilterationValues(1);
                    println("IN EXPLORE COUNTRY")

                    Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_COUNTRY, true)
                    Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_DISTANCE, false)

                    replaceWithMainFragment(2)

                } else {
                    resumePlayback()
                }
            }

            override fun onDismiss() {
                resumePlayback()
            }
        })
    }


    private fun showDistanceDialog() {
        appDialogUtil!!.createRadiusDialog(
            getString(R.string.change_radius),
            Integer.parseInt(Prefrences.getPreferences(requireContext(), Constants.PREF_SEARCH_BY_DISTANCE)!!),
            object : DialogCallback {

                override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
                    if (position == 1) {
                        Prefrences.savePreferencesString(requireContext(),
                            Constants.PREF_SEARCH_BY_DISTANCE,
                            (v as AppCompatSeekBar).progress.toString())
                        println("IN EXPLORE DISTANCE")
                        Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_COUNTRY, false)
                        Prefrences.savePreferencesBoolean(requireContext(), Constants.PREF_HAS_DISTANCE, true)
                        replaceWithMainFragment(1)
                    } else {
                        resumePlayback()
                    }
                }

                override fun onDismiss() {
                    resumePlayback()
                }
            })
    }

    private fun replaceWithMainFragment(filter: Int) {
        val bundle = Bundle()
        println("EXPLORE FILTER BEFORE SEND: $filter")
        worldwideFragment = WorldwideFragment()
        worldwideFragment.arguments = bundle
        bundle.putInt("explore_filter", filter)
        /*if (trnd != null) {
            bundle.putSerializable("trending", trnd)
        }*/
        worldwideFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frg_fl, worldwideFragment, "worldwide_frg").commit()
//        RxBus.instance!!.publish(BusMessage.SWITCH_CONATINERS.name, R.id.main_frg_fl)
        Log.e("ExploreAdd", "IN SHOW WORLD WIDE Replace")
    }

    private fun detectLocation(): Location? {
        try {
            locationManager = requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                displayLocationSettingsRequest(requireContext())
            } else {
                this.canGetLocation = true
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
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
                                HomeActivity.userLocation = location
                            }
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
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
                                    HomeActivity.userLocation = location
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

                    } catch (e: IntentSender.SendIntentException) {
                        Log.i("TAG", "PendingIntent unable to execute request.")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i("TAG",
                    "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
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
                    showPermissionDialog()
                }
                return
            }
        }
    }

    private fun showPermissionDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_permission_rational)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
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
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun onLocationChanged(p0: Location) {
        HomeActivity.userLocation = p0
    }

}