package com.instaconnect.android.ui.fragment.explore

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatSeekBar
import com.google.android.gms.common.api.GoogleApiClient
import com.hbb20.CountryCodePicker
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.databinding.ExploreFragmentBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.ui.fragment.worldwide.WorldwideFragment
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.dialog_helper.DialogCallback
import com.instaconnect.android.utils.dialog_helper.DialogUtil
import com.instaconnect.android.utils.models.DialogItem
import gun0912.tedimagepicker.util.ToastUtil
import io.reactivex.functions.Consumer

class ExploreFragment : BaseFragment<ExploreViewModel, ExploreFragmentBinding, ExploreRepository>(),
    View.OnClickListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private val mainDialogItem: ArrayList<DialogItem> = ArrayList<DialogItem>()
    private var worldwideFragment: WorldwideFragment = WorldwideFragment()
    //private val trendingFragment: TrendingFragment = TrendingFragment(this)

    var isPlayVideoDueToChat = false
    private var permissionUtil: PermissionUtil? = null
    var selectedItem = "Worldwide"
    var appDialogUtil: AppDialogUtil? = null

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


}