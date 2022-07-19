package com.instaconnect.android.ui.fragment.worldwide

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.allattentionhere.autoplayvideos.ExoViewHolder
import com.allattentionhere.autoplayvideos.VideoRecyclerView
import com.allattentionhere.autoplayvideos.VideoRecyclerView.setMute
import com.danikula.videocache.HttpProxyCacheServer
import com.google.common.eventbus.Subscribe
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.databinding.FragmentWorldWideBinding
import com.instaconnect.android.network.ApiEndPoint
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.contact.SelectContactCallback
import com.instaconnect.android.ui.fragment.worldwide.Post.PostsList
import com.instaconnect.android.ui.fragment.worldwide.Post.UserProfile
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.previews.PreviewCallbacks
import com.instaconnect.android.ui.previews.holders.YoutubeViewHolder
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter
import com.instaconnect.android.ui.watch_together_room.WatchTogetherVideoActivity
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.dialog_helper.AlertDialogCallback
import com.instaconnect.android.utils.dialog_helper.DialogCallback
import com.instaconnect.android.utils.dialog_helper.DialogUtil
import com.instaconnect.android.utils.models.DialogItem
import com.instaconnect.android.utils.models.ExploreEvent
import com.instaconnect.android.utils.models.Trending
import com.instaconnect.android.widget.FrameViewLayout
import com.instaconnect.android.widget.recyclerview.LazyLoadListenerR
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import gun0912.tedimagepicker.util.ToastUtil
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.util.*

class WorldwideFragment : BaseFragment<WorldWideViewModel, FragmentWorldWideBinding, WorldWideRepository>(),
    View.OnClickListener, VideoListAdapter.VideoListListener,
    PreviewCallbacks, ProgressRequestBody.UploadCallbacks, SwipeRefreshLayout.OnRefreshListener,
    LazyLoadListenerR, SelectContactCallback,
    VideoListAdapter.VideoCountIncreaseListener,
    com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener {
    var file_size = ""
    private var trending: Trending? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var recyclerView: VideoRecyclerView? = null
    private var frameViewLayout: FrameViewLayout? = null
    private val currentLayoutType = "List"
    private val mainDialogItem: ArrayList<DialogItem> = ArrayList()
    var videoListAdapter: VideoListAdapter? = null
    private var index = 0
    private var post: PostsList? = null
    private var filter = 0
    private var to: String? = null
    private var from: String? = null
    var userProfile: UserProfile? = null
    private var alertDialog: AlertDialog? = null
    private var isFavourite = "0"
    private val ivList: ImageView? = null
    private val ivGrid: ImageView? = null
    private var pageType = ""
    var user_id: String? = null
    var page = "0"
    var currentVersion = ""
    var emptyTextViewMsg: TextView? = null
    var proxyCacheServer: HttpProxyCacheServer? = null

    private var spacesItemDecoration1: SpacesItemDecoration? = null
    private var spacesItemDecoration2: SpacesItemDecoration? = null
    val postsLists: ArrayList<PostsList> = ArrayList()

    private val reportDialogCallback: DialogCallback = object : DialogCallback {
        override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
            var reason = ""
            if (position == 0) {
                reason = resources.getString(R.string.spaming)
                //  Toast.makeText(getContext(),reason+"..",Toast.LENGTH_SHORT).show();
                reportPost(
                    post!!.id,
                    reason,
                    Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID).toString()
                )
            } else if (position == 1) showInappropriateDialog()

        }

        override fun onDismiss() {
            resumeAnyPlayback()
        }
    }
    private val inappropriateDialogCallback: DialogCallback = object : DialogCallback {
        override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
            var reason = ""
            when (position) {
                0 -> reason =
                    resources.getString(R.string.harassment)
                1 -> reason =
                    resources.getString(R.string.hate)
                2 -> reason =
                    resources.getString(R.string.sexual_activity)
                3 -> reason =
                    resources.getString(R.string.copyright)
                4 -> reason =
                    resources.getString(R.string.self_injury)
            }
            if (reason != "") {
                reportPost(
                    post!!.id,
                    reason,
                    Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID).toString()
                )
            }
            /* if (position == 2)
                reason = getResources().getString(R.string.violate_term);
            String body = AppConstants.body + "\n Post ID:- " + post.getId() + "\n Reason:- " + reason + "\n Sent from my ANDROID";
            IntentUtil.emailIntent(getActivity(), AppConstants.email, reason, body);*/
        }

        override fun onDismiss() {
            resumeAnyPlayback()
        }
    }
    private val mainDialogCallback: DialogCallback = object : DialogCallback {
        override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
            when {
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.copy_url), true) -> {
                    var link = ""
                    link =
                        if (post!!.video != null && !post!!.video!!.isEmpty()) post!!.video!! else post!!.image!!
                    ViewUtil.showShareSheet(
                        "Hi",
                        "Check this post " + ApiEndPoint.UPLOADS_BASE_URL + link, requireContext()
                    )
                }
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.post_facebook), true) -> {
                    IntentUtil.shareOnFacebook(requireActivity(), post!!.video!!)
                }
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.delete_post), true) -> {
                    deletePost()
                }
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.report_concern), true) -> {
                    showReportDialog()
                }
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.share_with_my_contacts_txt), true) -> {
                    //dataManager.prefHelper().setPostPosition("post_position", index)
                    //Toast.makeText(getActivity(), "Under Developement", Toast.LENGTH_SHORT).show();
//                    shareWithMyContacts()
                }
                mainDialogItem[position].name
                    .equals(resources.getString(R.string.share_with_others_txt), true) -> {
                    shareLink
                    val link = shareLink
                    Log.d("LINK:", link)
                    ShareUtil.shareTextUrl(getString(R.string.app_name), link, requireActivity())
                }
                mainDialogItem[position].name.equals(
                    resources.getString(R.string.block_user),
                    true
                ) -> {
                    // Toast.makeText(getContext(),post.getUsername(),Toast.LENGTH_SHORT).show();

                    viewModel.viewModelScope.launch {
                        viewModel.blocUser(
                            post!!.userId!!,
                            "1",
                            Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)
                                .toString()
                        )
                    }
                }
            }
        }

        override fun onDismiss() {
            resumeAnyPlayback()
        }
    }

    private fun deletePost() {
        viewModel.viewModelScope.launch {
            viewModel.deletePost(post!!.id!!)
        }
    }

    private var dialog: Dialog? = null
    private var countryName: String? = null
    var refreshLayout: SwipeRefreshLayout? = null

    private var worldWideView: View? = null

    private val shareLink: String
        private get() {
            var link = ""
            val base_url: String = ApiEndPoint.UPLOADS_BASE_URL
            var media = ""
            val type = getItemViewType(index)
            val post_item: PostsList = videoListAdapter!!.postsLists.get(index)
            if (type == Constants.IMAGE_TYPE) {
                println("LINK: IMAGE")
                media = post_item.image!!
            } else if (type == Constants.VIDEO_TYPE) {
                println("LINK: VIDEO")
                media = post_item.video!!
            }
            println("LINK: MEDIA$media")
            link += base_url + media
            if (post_item.caption != null || post_item.caption != "") {
                link += """ 
${post_item.caption}"""
            }
            link += """
        
        Shared via Watch Together
        ${getString(R.string.share_msg_txt)}https://play.google.com/store/apps/details?id=${requireActivity().packageName}
        """.trimIndent()
            return link
        }

    /*private fun shareWithMyContacts() {
        SelectContactActivity.setContactCallback(this)
        startActivity(BaseIntent(getActivity(), SelectContactActivity::class.java, false))
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        worldWideView = inflater.inflate(R.layout.fragment_worldwide_new, container, false)
        instance = this
        userProfile = getModel()
        val bundle: Bundle = requireArguments()

        viewModel = ViewModelProvider(
            this,
            WorldwideViewModelFactory(
                WorldWideRepository(
                    MyApi.getInstance()
                )
            )
        )[WorldWideViewModel::class.java]

        BlurKit.init(requireContext())
        proxyCacheServer = InstaConnectApp.instance!!.getProxy(requireContext())
        currentVersion = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
        spacesItemDecoration1 = SpacesItemDecoration(0)
        spacesItemDecoration2 = SpacesItemDecoration(4)
        if (bundle != null) {
            trending = bundle.getSerializable("trending") as Trending?
            filter = bundle.getInt("explore_filter", 0)
            isFavourite = bundle.getString("isFavourite", "0")
            if (bundle.getString("isMyPost") != null) {
                pageType = bundle.getString("isMyPost") as String
            }
            if (bundle.getString("USER_ID") != null) {
                user_id = bundle.getString("USER_ID")
            }
        }

        // list of post response handler
        viewModel.getWatchlistResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                    if (it.value.response != null) {
                        if (it.value.response != null && it.value.response!!.code.equals("200", true)) {
                            if (it.value.response!!.postsList!!.isEmpty()) {
                                if (page == "0") {
                                    Log.d("TAG", "onCreateView: $page")
                                    emptyTextViewMsg!!.text =
                                        "No Streams Available.\nCheck Back Later"
                                    frameViewLayout!!.showEmpty()
                                }
                                recyclerView!!.isScrollingEnabled(false)
                            } else {
                                frameViewLayout!!.showContent()
                                setData(it.value.response!!.postsList!!)
                            }
                            if (activity is HomeActivity) {
                                (activity as HomeActivity).userProfile =
                                    it.value.response!!.userProfile
                            }
                        } else {
                            emptyTextViewMsg!!.text = "No Streams Available.\n Check Back Later"
                            frameViewLayout!!.showEmpty()
                        }

                    }
                }
                is Resource.Loading -> {
                    refreshLayout!!.isRefreshing = true
                    recyclerView!!.isLoading = true
                }
                is Resource.Failure -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                }
            }
        }

        // report post response handler
        viewModel.reportPostResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                    if (it.value.response != null) {
                        // Toast.makeText(getContext(), response.body().getResponse().getMessage()+"..."+response.body().getResponse().getCode(), Toast.LENGTH_SHORT).show();
                        if (it.value.response!!.code.equals("200")) {
                            // Toast.makeText(getContext(),response.body().getResponse().getMessage()+"",Toast.LENGTH_SHORT).show();
                            DialogUtil.showAlertDialog(
                                requireContext(),
                                "Thanks for reporting this post",
                                "Your feedback is helping us keep Watch Together safe for everyone",
                                getString(R.string.ok_caps),
                                "",
                                object : AlertDialogCallback {
                                    override fun onPositiveButton(dialog: DialogInterface?) {
                                        videoListAdapter!!.postsLists.removeAt(index)
                                        videoListAdapter!!.notifyDataSetChanged()
                                    }

                                    override fun onNegativeButton(dialog: DialogInterface?) {}
                                }).show()
                        } else {
                            Toast.makeText(
                                context,
                                it.value.response!!.message + "..." + it.value.response!!.code,
                                Toast.LENGTH_SHORT
                            ).show();
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error..Code" + it.value.response!!.code,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }
                is Resource.Loading -> {
                    refreshLayout!!.isRefreshing = true
                    recyclerView!!.isLoading = true
                }
                is Resource.Failure -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                }
            }

        }

        // block user response handler
        viewModel.blockUserResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                    if (it.value.response != null) {
                        // Toast.makeText(getContext(), response.body().getResponse().getMessage()+"..."+response.body().getResponse().getCode(), Toast.LENGTH_SHORT).show();
                        if (it.value.response!!.code.equals("200")) {
                            Toast.makeText(context, "User Blocked Successfully", Toast.LENGTH_SHORT)
                                .show()
                            videoListAdapter!!.postsLists.removeAt(index)
                            videoListAdapter!!.notifyItemRemoved(index)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Error..Code" + it.value.response!!.code,
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                is Resource.Loading -> {
                    refreshLayout!!.isRefreshing = true
                    recyclerView!!.isLoading = true
                }

                is Resource.Failure -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                }
            }
        }

        // delete post response handler
        viewModel.deletePostResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false

                    Toast.makeText(context, "Room deleted", Toast.LENGTH_SHORT).show()
                    videoListAdapter!!.delete(index)
                }

                is Resource.Loading -> {
                    refreshLayout!!.isRefreshing = true
                    recyclerView!!.isLoading = true
                }

                is Resource.Failure -> {
                    refreshLayout!!.isRefreshing = false
                    recyclerView!!.isLoading = false
                }
            }
        }

        return worldWideView
    }

    @Subscribe
    fun exploreEvent(exploreEvent: ExploreEvent?) {
//        Log.d("ExploreEvent", exploreEvent.getTrending().getApi_name());
//        trending = exploreEvent.getTrending();
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("Chirag : WorldWide: In On ViewCreated")
        findView(view)
        videoListAdapter = VideoListAdapter(
            requireActivity(), proxyCacheServer!!,
            this, Prefrences.getUser()!!, lifecycle,
            currentLayoutType, this
        )
        //   recyclerView.setAdapter(videoListAdapter);
        setVideoAdapter(currentLayoutType)
        getPosts(page)
    }

    private fun getPosts(page: String) {

        this.page = page
        refreshLayout!!.isRefreshing = true
        recyclerView!!.isLoading = true

        user_id = Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID).toString()
        var ctName = "worldwide"
        var country = ""
        var radius = ""
        var lat = 0.0
        var lng = 0.0
        var includeOtherMedia = 1

        if (Prefrences.getBooleanPreferences(requireContext(), Constants.PREF_HAS_COUNTRY)) {
            country = Prefrences.getPreferences(requireContext(), "explore_country")!!
        } else if (Prefrences.getBooleanPreferences(requireContext(), Constants.PREF_HAS_DISTANCE)) {
            radius = Prefrences.getPreferences(requireContext(), Constants.PREF_SEARCH_BY_DISTANCE)!!
            if (HomeActivity.userLocation != null) {
                lat = HomeActivity.userLocation!!.latitude
                lng = HomeActivity.userLocation!!.longitude
            } 
        }

        if (trending != null) {
            Log.d("Treding_Cat", trending!!.api_name)
            ctName = trending!!.api_name
        }

        viewModel.viewModelScope.launch {
            viewModel.getWatchList(user_id!!, ctName, page, country, radius, lat.toString(), lng.toString())
        }
    }

    fun onBackPress(fragmentCount: Int) {}

    private fun onPauseActivity() {
        if (recyclerView != null) {
            println("WorldwideFragment.PauseActivity: In if")
            recyclerView!!.isForeground = false
            recyclerView!!.stopVideos()
            stopYoutubeVideo()
        }
    }

    fun setLayoutManagerForVideo(layoutType: String) {
        if (layoutType == "List") {
            recyclerView!!.layoutManager = GridLayoutManager(getActivity(), 1)
            videoListAdapter!!.setLayout("List")
            //recyclerView.addItemDecoration(spacesItemDecoration1);
        } else if (layoutType == "Grid") {
            recyclerView!!.layoutManager = GridLayoutManager(getActivity(), 2)
            //recyclerView.addItemDecoration(spacesItemDecoration2);
            videoListAdapter!!.setLayout("Grid")
        }
        videoListAdapter!!.notifyDataSetChanged()
    }

    private fun stopYoutubeVideo() {
        val firstVisiblePosition =
            (recyclerView!!.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        val lastVisiblePosition =
            (recyclerView!!.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
        if (firstVisiblePosition >= 0) {
            for (i in firstVisiblePosition..lastVisiblePosition) {
                val holder: RecyclerView.ViewHolder? =
                    recyclerView!!.findViewHolderForAdapterPosition(i)
                try {
                    val cvh: ExoViewHolder? = holder as ExoViewHolder?
                    if (cvh is YoutubeViewHolder) {
                        val postsList: PostsList =
                            videoListAdapter!!.postsLists.get(cvh.getAdapterPosition())
                        val videoId = postsList.youTubeVideoId
                        val youtubeViewHolder: YoutubeViewHolder? = cvh as YoutubeViewHolder?
                        if (i >= 0 && cvh != null && videoId != null && !videoId.isEmpty() && !videoId.equals(
                                "null",
                                ignoreCase = true
                            )
                        ) {
                            if (youtubeViewHolder!!.mYouTubePlayer != null) {
                                youtubeViewHolder.isLoading = false
                                youtubeViewHolder.isPlayingOrNot = false
                                youtubeViewHolder.mYouTubePlayer!!.pause()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun resumeAnyPlayback() {
        println("SHINY RECYCLER: $recyclerView")
        try {
            recyclerView!!.isForeground = true
            if (videoListAdapter != null && videoListAdapter!!.postsLists != null && !videoListAdapter!!.postsLists
                    .isEmpty()
            ) {
                recyclerView!!.playAvailableVideos(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findView(view: View) {
        refreshLayout = view.findViewById(R.id.swiperefresh)
        recyclerView = view.findViewById(R.id.rv_worldwide)
        val ivCamera: ImageView = view.findViewById(R.id.iv_camera)
        val ivCreatePost: ImageView = view.findViewById(R.id.ivCreatePost)
        frameViewLayout = view.findViewById(R.id.flViewLayout)
        emptyTextViewMsg = frameViewLayout!!.emptyView!!.findViewById(R.id.tv_message)
        ivCamera.setOnClickListener(this)
        ivCreatePost.setOnClickListener(this)
        refreshLayout!!.setOnRefreshListener(this)
    }

    override fun onClick(view: View) {
        when (view.getId()) {
            R.id.iv_camera -> {
                //setCallBacks()
                //startActivity(BaseIntent(requireActivity(), CaptureFragment::class.java, false))
            }
            /*R.id.ivCreatePost -> if (dataManager.prefHelper().isWatchList()) {
                val intent = Intent(getContext(), CaptureFragment::class.java)
                intent.putExtra("CaptureType", "WatchTogether")
                intent.putExtra("PostType", "")
                startActivity(intent)
                viewUtil.hideKeyboard()
            } else {
                val intent = Intent(getContext(), CreatePostActivity::class.java)
                intent.putExtra("PostType", "")
                startActivity(intent)
                viewUtil.hideKeyboard()
            }*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setMute(true)
        onPauseActivity()
        //RxBus.instance!!.unregister(this)
        //removeCallBacks()
    }

    /*private fun setCallBacks() {
        ImagePreviewActivity.setPreviewCallbacks(this, WorldwideFragment::class.java.name)
        VideoPreviewsActivity.setPreviewCallbacks(this, WorldwideFragment::class.java.name)
    }*/

    /*private fun removeCallBacks() {
        ImagePreviewActivity.setPreviewCallbacks(null, "")
        VideoPreviewsActivity.setPreviewCallbacks(null, "")
    }*/

    fun stopAnyPlayback() {
        if (recyclerView != null) {
            recyclerView!!.stopVideos()
        }
    }

    private fun setData(postsLists: List<PostsList>) {
        videoListAdapter!!.setData(postsLists)
        recyclerView!!.playAvailableVideos(0)
    }

    private fun createMainPopUp(me: Boolean) {
        mainDialogItem.clear()
        //mainDialogItem.add(new DialogItem(getString(R.string.share_with_my_contacts_txt), R.color.sky_blue, true));
        //mainDialogItem.add(new DialogItem(getString(R.string.share_with_others_txt), R.color.sky_blue, true));
        if (!me) mainDialogItem.add(
            DialogItem(
                getString(R.string.block_user),
                R.color.sky_blue,
                true
            )
        )

//        mainDialogItem.add(new DialogItem(getString(R.string.copy_url), R.color.sky_blue, true));
//        mainDialogItem.add(new DialogItem(getString(R.string.post_facebook), R.color.sky_blue, true));
        if (me) mainDialogItem.add(
            DialogItem(
                getString(R.string.delete_post),
                R.color.colorAccent,
                true
            )
        )
        if (!me) mainDialogItem.add(
            DialogItem(
                getString(R.string.report_concern),
                R.color.red,
                true
            )
        )
        mainDialogItem.add(DialogItem(getString(R.string.cancel), R.color.red))
    }

    private fun showReportDialog() {
        val dialogItems: ArrayList<DialogItem> = ArrayList()
        dialogItems.add(DialogItem(getString(R.string.spaming), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.inappropriate), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.cancel), R.color.red, true))
        DialogUtil.createListDialog(requireContext(), dialogItems, reportDialogCallback)
    }

    private fun showInappropriateDialog() {
        val dialogItems: ArrayList<DialogItem> = ArrayList()
        dialogItems.add(DialogItem(getString(R.string.harassment), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.hate), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.sexual_activity), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.copyright), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.self_injury), R.color.sky_blue, true))
        dialogItems.add(DialogItem(getString(R.string.cancel), R.color.red, true))
        //    dialogItems.add(new DialogItem(getString(R.string.copyright_infrigement), R.color.sky_blue, true));
        //   dialogItems.add(new DialogItem(getString(R.string.violate_term), R.color.sky_blue));
        DialogUtil.createListDialog(
            requireContext(),
            dialogItems,
            inappropriateDialogCallback,
            getString(R.string.inappropriate_dialog_heading)
        )
    }

    fun setVideoAdapter(currentLayoutType: String) {
        mLayoutManager = LinearLayoutManager(getActivity())
        gridLayoutManager = GridLayoutManager(getActivity(), 2)
        if (currentLayoutType == "List") {
            recyclerView!!.layoutManager = mLayoutManager
        } else if (currentLayoutType == "Grid") {
            recyclerView!!.layoutManager = gridLayoutManager
        }
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.setActivity(activity)
        recyclerView!!.setCheckForMp4(false)
        recyclerView!!.setPlayOnlyFirstVideo(true)
        recyclerView!!.setVisiblePercent(Constants.visiblePercent) // percentage of View that needs to be visible to start playing
        recyclerView!!.setLazyLoadListener(this)
        recyclerView!!.isForeground = true
        recyclerView!!.adapter = videoListAdapter
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView!!, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisiblePosition =
                    (Objects.requireNonNull(recyclerView.layoutManager) as LinearLayoutManager).findFirstVisibleItemPosition()
                val lastVisiblePosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (firstVisiblePosition >= 0) {
                    val rect_parent = Rect()
                    recyclerView.getGlobalVisibleRect(rect_parent)
                    //                    boolean foundFirstVideo = false;
                    for (i in firstVisiblePosition..lastVisiblePosition) {
                        val holder: RecyclerView.ViewHolder =
                            recyclerView.findViewHolderForAdapterPosition(i)!!
                        try {
                            val cvh: ExoViewHolder = holder as ExoViewHolder
                            if (cvh is YoutubeViewHolder) {
                                val postsList: PostsList =
                                    videoListAdapter!!.postsLists[cvh.getAdapterPosition()]
                                val videoId = postsList.youTubeVideoId
                                val youtubeViewHolder: YoutubeViewHolder = cvh as YoutubeViewHolder
                                if (postsList.groupPassword!!.isEmpty()) {
                                    if (i >= 0 && cvh != null && videoId != null && !videoId.isEmpty() && !videoId.equals(
                                            "null",
                                            ignoreCase = true
                                        )
                                    ) {
                                        /*int[] location = new int[2];
                                    youtubeViewHolder.youTubePlayerView.getLocationOnScreen(location);
                                    Rect rect_child = new Rect(location[0], location[1], location[0] + youtubeViewHolder.youTubePlayerView.getWidth(), location[1] + youtubeViewHolder.youTubePlayerView.getHeight());
                                    float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                    float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                    float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                    float overlapArea = x_overlap * y_overlap;
                                    float percent = (overlapArea / rect_parent_area) * 100.0f;*/
                                        val rowRect = Rect()
                                        (recyclerView.getLayoutManager() as LinearLayoutManager).findViewByPosition(
                                            i
                                        )!!
                                            .getGlobalVisibleRect(rowRect)
                                        var percent: Int
                                        percent = if (rowRect.bottom >= rect_parent.bottom) {
                                            val visibleHeightFirst: Int =
                                                rect_parent.bottom - rowRect.top
                                            visibleHeightFirst * 100 / (recyclerView.getLayoutManager() as LinearLayoutManager).findViewByPosition(
                                                i
                                            )!!
                                                .height
                                        } else {
                                            val visibleHeightFirst: Int =
                                                rowRect.bottom - rect_parent.top
                                            visibleHeightFirst * 100 / (recyclerView.getLayoutManager() as LinearLayoutManager).findViewByPosition(
                                                i
                                            )!!
                                                .height
                                        }
                                        if (percent >= 10 && percent < Constants.visiblePercent) {
                                            if (!youtubeViewHolder.isLoading && youtubeViewHolder.mYouTubePlayer == null) {
                                                youtubeViewHolder.isLoading = true
                                                youtubeViewHolder.youTubePlayerView.addYouTubePlayerListener(
                                                    object : AbstractYouTubePlayerListener() {

                                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                                            super.onReady(youTubePlayer)
                                                            youtubeViewHolder.mYouTubePlayer =
                                                                youTubePlayer
                                                            youtubeViewHolder.mYouTubePlayer!!.cueVideo(
                                                                videoId,
                                                                0f
                                                            )
                                                            //                                                    if (percent >= AppConstants.visiblePercent) {
                                                            if (youtubeViewHolder.isPlayingOrNot) {
                                                            } else {
                                                                youtubeViewHolder.isPlayingOrNot =
                                                                    true
                                                                youtubeViewHolder.mYouTubePlayer!!.loadVideo(
                                                                    videoId,
                                                                    0f
                                                                )
                                                            }
                                                        }
                                                    })
                                            }
                                        } else if (percent >= Constants.visiblePercent) {
                                            if (!youtubeViewHolder.isLoading && youtubeViewHolder.mYouTubePlayer == null) {
                                                youtubeViewHolder.isLoading = true
                                                youtubeViewHolder.youTubePlayerView.addYouTubePlayerListener(
                                                    object : AbstractYouTubePlayerListener() {

                                                        override fun onReady(youTubePlayer: YouTubePlayer) {
                                                            super.onReady(youTubePlayer)
                                                            youtubeViewHolder.mYouTubePlayer =
                                                                youTubePlayer
                                                            youtubeViewHolder.mYouTubePlayer!!.cueVideo(
                                                                videoId,
                                                                0f
                                                            )
                                                            //                                                    if (percent >= AppConstants.visiblePercent) {
                                                            if (youtubeViewHolder.isPlayingOrNot) {
                                                            } else {
                                                                youtubeViewHolder.isPlayingOrNot =
                                                                    true
                                                                youtubeViewHolder.mYouTubePlayer!!.loadVideo(
                                                                    videoId,
                                                                    0f
                                                                )
                                                            }
                                                        }

                                                    })
                                            } else if (youtubeViewHolder.mYouTubePlayer != null) {
                                                if (youtubeViewHolder.isPlayingOrNot) {
                                                } else {
                                                    youtubeViewHolder.isPlayingOrNot = true
                                                    youtubeViewHolder.mYouTubePlayer!!.loadVideo(
                                                        videoId,
                                                        0f
                                                    )
                                                }
                                            }
                                        } else {
                                            youtubeViewHolder.isLoading = false
                                            youtubeViewHolder.isPlayingOrNot = false
                                            if (youtubeViewHolder.mYouTubePlayer != null) youtubeViewHolder.mYouTubePlayer!!.pause()
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        println("WorldwideFragment: On Pause method = $recyclerView")
        onPauseActivity()
    }

    override fun onStop() {
        super.onStop()
        println("WorldwideFragment: On Stop method")
        onPauseActivity()
    }

    override fun onItemViewClick(post: PostsList?, icon: ImageView?, type: Int) {}

    override fun onItemSingleClick(
        post: PostsList?,
        imageView: ImageView?,
        type: Int,
        position: Int,
    ) {
        if (type == VideoListAdapter.VIDEO) {
            makeServerCallForIncreaseView(post!!.id!!, position)
            if (post.mediaType.equals("web", ignoreCase = true)) {
                val intent =
                    BaseIntent(requireActivity(), WatchTogetherVideoActivity::class.java, false)
                intent.putExtra("USER_ID", post.userId)
                intent.putExtra("VIDEO_ID", post.hyperlink)
                intent.putExtra("POST_ID", post.mediaType)
                intent.putExtra("ROOM_NAME", post.groupName)
                intent.putExtra("USER_NAME", post.username)
                intent.putExtra("USER_IMAGE", post.userimage)
                intent.putExtra("TOTAL_VIEWS", post.totalViews)
                intent.putExtra("GROUP_NAME", post.groupName)
                intent.putExtra("ACTUAL_POST_ID", post.id)
                intent.putExtra("POST_REACTION", post.reaction!!.yourReaction)
                intent.putExtra("TOTAL_LIKES", VideoListAdapter.prettyCount(post.reaction!!.likes))
                intent.putExtra("COMING_FROM", "Home")
                startActivity(intent)
            } else {
                /*val intent = BaseIntent(requireActivity(), VideoActivity::class.java, false)
                 val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                          requireActivity(),
                         imageView!!,
                         "app_icon"
                     )
                 intent.setModel(LinkBundle(post.video, post.thumbnail, post.mediaType))
                 startActivity(intent, options.toBundle())*/
            }
        } else if (type == VideoListAdapter.YOUTUBE) {
            makeServerCallForIncreaseView(post!!.id!!, position)
            if (post.mediaType.equals("web", ignoreCase = true)) {
                val intent = BaseIntent(requireActivity(), WatchTogetherVideoActivity::class.java, false)
                intent.putExtra("USER_ID", post.userId)
                intent.putExtra("VIDEO_ID", post.youTubeVideoId)
                intent.putExtra("POST_ID", post.mediaType)
                intent.putExtra("ROOM_NAME", post.groupName)
                intent.putExtra("USER_NAME", post.username)
                intent.putExtra("USER_IMAGE", post.userimage)
                intent.putExtra("TOTAL_VIEWS", post.totalViews)
                intent.putExtra("GROUP_NAME", post.groupName)
                intent.putExtra("ACTUAL_POST_ID", post.id)
                intent.putExtra("POST_REACTION", post.reaction!!.yourReaction)
                intent.putExtra("TOTAL_LIKES", VideoListAdapter.prettyCount(post.reaction!!.likes))
                intent.putExtra("COMING_FROM", "Home")
                startActivity(intent)
            }
        } else if (type == VideoListAdapter.IMAGE) {
            /*val intent = BaseIntent(getActivity(), ImageActivity::class.java, false)
            intent.setModel(LinkBundle(post.image))
            val options: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), icon, "app_icon")
            startActivity(intent, options.toBundle())*/
        } else if (type == VideoListAdapter.GRID_VIEW) {
            /*if (getContext() is MyFavouriteActivity) {
                MyFavouriteActivity.getActivity().getB().callOnClick()
                recyclerView!!.scrollToPosition(position)
                videoListAdapter.notifyDataSetChanged()
            } else {
                ExploreFragment.getFragment().getB().callOnClick()
                recyclerView!!.scrollToPosition(position)
                videoListAdapter.notifyDataSetChanged()
            }*/
        }
    }

    override fun onTextItemClick(post: PostsList?, textView: TextView?, type: Int, position: Int) {
        //ExploreFragment.getFragment().getB().callOnClick()
        recyclerView!!.scrollToPosition(position)
        videoListAdapter!!.notifyDataSetChanged()
    }

    override fun onOptionClick(position: Int, post: PostsList?, type: Int) {
        stopAnyPlayback()
        index = position
        this.post = post
        createMainPopUp(post!!.userId!!.equals(user_id, false))
        DialogUtil.createListDialog(requireContext(), mainDialogItem, mainDialogCallback)
    }

    override fun onItemClick(position: Int, post: PostsList?, view: View?) {
        /*if (view.getId() === R.id.tv_follow) {
             var status = "0"
             val textView: TextView = view as TextView
             Log.e("followStatus", post.follow.status.toString() + "..")
             if (post.follow.status != 0) {
                 Log.e("followStatus1", post.follow.status.toString() + "..")
                 unfollowAlert(post, position)
             } else {
                 if (post.isPrivate == "0") {
                     status = "2"
                     textView.setText(getResources().getString(R.string.following))
                 } else if (post.isPrivate == "1") {
                     status = "1"
                     textView.setText(getResources().getString(R.string.requested))
                 }
                 callFollowUserApi(
                     post.userId,
                     status,
                     dataManager.prefHelper().getUser().getPhone(),
                     position,
                     post
                 )
             }
         } else if (view.getId() === R.id.iv_favourite) {
             val imageView: ImageView = view as ImageView
             if (post.favourite.status == "1") {
                 imageView.setImageResource(R.drawable.ic_favourites)
             } else {
                 imageView.setImageResource(R.drawable.ic_unfavourites)
             }
             callAddRemoveFaourite(
                 dataManager.prefHelper().getUser().getPhone(),
                 post.id,
                 position,
                 post
             )
         } else if (view.getId() === R.id.lin_like) {
             var reaction = "0"
             reaction = if (post.reaction.yourReaction.equals("1", ignoreCase = true)) {
                 "0"
             } else {
                 "1"
             }
             callAddPostReaction(
                 dataManager.prefHelper().getUser().getPhone(),
                 reaction,
                 post.id,
                 position,
                 post
             )
         } else if (view.getId() === R.id.lin_comment) {
             val intent = BaseIntent(getContext(), CommentsActivity::class.java)
             intent.setModel(
                 CommentBundle(
                     post.id,
                     post.hyperlink,
                     post.thumbnail,
                     post.date,
                     post.commentTotal.toString(),
                     post.caption,
                     post.username,
                     post.userimage,
                     post.mediaType,
                     post.image,
                     post.youTubeVideoId,
                     post.isVideoLink,
                     post.video,
                     post.hyperlink,
                     post.groupPassword,
                     post.groupName
                 )
             )
             startActivity(intent)
         }*/
    }

    override fun onPrivateRoomVideoClick(
        post: PostsList?,
        mediaType: String?,
        password: String?,
        hyperLink: String?,
        type: Int,
    ) {
        val dialog = Dialog(requireContext(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_private_room_video_password_new)
        val edtPassword: EditText = dialog.findViewById(R.id.edtPassword)
        val tvCancel: TextView = dialog.findViewById(R.id.tvCancel)
        val tvOk: TextView = dialog.findViewById(R.id.tvOk)
        val imageView: ImageView = dialog.findViewById(R.id.img_bg)
        val relMain: View = dialog.findViewById(R.id.rel_main)
        val vto: ViewTreeObserver = relMain.viewTreeObserver
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
        tvCancel.setOnClickListener { v -> dialog.dismiss() }
        tvOk.setOnClickListener { v ->
            val enteredPassword: String = edtPassword.text.toString()
            if (enteredPassword.isEmpty()) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
            } else {
                if (enteredPassword == password) {
                    goToWatchTogetherVideoActivity(post!!, type)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        "You've entered wrong password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        dialog.show()
    }

    private fun goToWatchTogetherVideoActivity(post: PostsList, type: Int) {
        if (type == VideoListAdapter.VIDEO) {
            if (post.mediaType.equals("web", ignoreCase = true)) {
                val intent =
                    BaseIntent(requireActivity(), WatchTogetherVideoActivity::class.java, false)
                intent.putExtra("USER_ID", post.userId)
                intent.putExtra("VIDEO_ID", post.hyperlink)
                intent.putExtra("POST_ID", post.mediaType)
                intent.putExtra("ROOM_NAME", post.groupName)
                intent.putExtra("USER_NAME", post.username)
                intent.putExtra("USER_IMAGE", post.userimage)
                intent.putExtra("TOTAL_VIEWS", post.totalViews)
                intent.putExtra("GROUP_NAME", post.groupName)
                intent.putExtra("ACTUAL_POST_ID", post.id)
                intent.putExtra("POST_REACTION", post.reaction!!.yourReaction)
                intent.putExtra("TOTAL_LIKES", VideoListAdapter.prettyCount(post.reaction!!.likes))
                startActivity(intent)
            }
        } else if (type == VideoListAdapter.YOUTUBE) {
            if (post.mediaType.equals("web", ignoreCase = true)) {
                val intent =
                    BaseIntent(requireActivity(), WatchTogetherVideoActivity::class.java, false)
                intent.putExtra("USER_ID", post.userId)
                intent.putExtra("VIDEO_ID", post.youTubeVideoId)
                intent.putExtra("POST_ID", post.mediaType)
                intent.putExtra("ROOM_NAME", post.groupName)
                intent.putExtra("USER_NAME", post.username)
                intent.putExtra("USER_IMAGE", post.userimage)
                intent.putExtra("TOTAL_VIEWS", post.totalViews)
                intent.putExtra("GROUP_NAME", post.groupName)
                intent.putExtra("ACTUAL_POST_ID", post.id)
                intent.putExtra("POST_REACTION", post.reaction!!.yourReaction)
                intent.putExtra("TOTAL_LIKES", VideoListAdapter.prettyCount(post.reaction!!.likes))
                startActivity(intent)
            }
        }
    }

    /*private fun openWebViewVideo(link: String) {
        var link: String? = link
        if (link != null) {
            if (!link.contains("http")) link = "http://$link"
            Builder(requireActivity()).webViewJavaScriptEnabled(true)
                .webViewJavaScriptCanOpenWindowsAutomatically(true)
                .iconDefaultColor(getResources().getColor(R.color.white))
                .show(link)
        }
    }*/


    override fun onPreviewActivityCallback(type: String?, chatMessage: ChatMessage?, flag: Int) {

    }

    override fun onPreviewActivityCallback(
        type: String?,
        chatMessage: ChatMessage?,
        flag: Int,
        trending: Trending?,
    ) {
        val s = ""
    }

    /*fun sendMessage(type: String?, rawChatMessage: ChatMessage) {
        when (type) {
            Constants.IMAGE_TYPE -> {
                if (HomeActivity.userLocation == null) {
                    dialogUtil.showAlertDialog(getString(R.string.location_off),
                        getString(R.string.please_on_location),
                        getString(R.string.ok_caps), "",
                        object : AlertDialogCallback() {
                            fun onPositiveButton(dialog: DialogInterface?) {}
                            fun onNegativeButton(dialog: DialogInterface?) {}
                        })
                    return
                }
                dialog = appDialogUtil.createPostDialog()
                val imageChatMessage: ChatMessage = PrepareMessageFactory.prepareImageMessage(
                    "",
                    "",
                    rawChatMessage.getUniqueCode(),
                    rawChatMessage.getMessageBody(),
                    rawChatMessage.getCaption(),
                    rawChatMessage.getMessageThumb(),
                    rawChatMessage.getFileSize()
                )
                UploadMessageFactory.publicPostMedia(
                    imageChatMessage,
                    "worldwide",
                    rawChatMessage.getData(),
                    HomeActivity.userLocation.getLatitude(),
                    HomeActivity.userLocation.getLongitude(),
                    country,
                    dataManager,
                    this
                )
            }
            Constants.VIDEO_TYPE -> {
                if (HomeActivity.userLocation == null) {
                    dialogUtil.showAlertDialog(
                        getResources().getString(R.string.location_off),
                        getResources().getString(R.string.please_on_location),
                        "OK",
                        "",
                        object : AlertDialogCallback() {
                            fun onPositiveButton(dialog: DialogInterface?) {}
                            fun onNegativeButton(dialog: DialogInterface?) {}
                        })
                    return
                }
                dialog = appDialogUtil.createPostDialog()
                val videoChatMessage: ChatMessage = PrepareMessageFactory.prepareVideoMessage(
                    "",
                    "",
                    rawChatMessage.getUniqueCode(),
                    rawChatMessage.getMessageBody(),
                    rawChatMessage.getCaption(),
                    rawChatMessage.getMessageThumb(),
                    rawChatMessage.getFileSize()
                )
                UploadMessageFactory.publicPostMedia(
                    videoChatMessage,
                    "worldwide",
                    rawChatMessage.getData(),
                    HomeActivity.userLocation!!.getLatitude(),
                    HomeActivity.userLocation!!.getLongitude(),
                    country,
                    dataManager,
                    this
                )
            }
        }
    }*/

    val country: String
        get() {
            if (countryName == null) countryName =
                LocationUtil.getCountryName(getActivity(), HomeActivity.userLocation)
            return if (countryName == null) resources.configuration.locale.displayCountry else countryName!!
        }

    override fun onProgressUpdate(percentage: Int, chatMessage: ChatMessage) {
        println("POST UPLOADING")
        if (dialog == null) return
        val progressBar: ProgressBar = dialog!!.findViewById(R.id.pbProcessing)
        progressBar.setProgress(percentage)
    }

    override fun onError(chatMessage: ChatMessage) {
        println("POST FAILED")
        if (dialog == null) return
        dialog!!.dismiss()
        ToastUtil.showToast("Uploading failed")
    }

    override fun onFinish(chatMessage: ChatMessage) {
        println("POST FINISH")
        if (dialog == null) return
        val progressBar: ProgressBar = dialog!!.findViewById(R.id.pbProcessing)
        progressBar.setProgress(100)
        Handler().postDelayed({
            dialog!!.dismiss()
            //RxBus.instance!!.publish(BusMessage.REFRESH_PUBLIC.name, true)
        }, 2000)
    }

    override fun onRefresh() {
        if (recyclerView == null || videoListAdapter == null) return
        recyclerView!!.resetLazyLoadListener()
        videoListAdapter!!.clear()
        getPosts("0")
        resumeAnyPlayback()
    }

    override fun onResume() {
        super.onResume()
        //videoListAdapter.clear();
        //getPosts("0");
        resumeAnyPlayback()
    }

    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        getPosts(page.toString())
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {
//        if (((HomeActivity) getActivity()).getCurrentItem() == 0)
        recyclerView!!.onScroll()
    }

    override fun onScrollStateChage(upDown: Int) {
    }

    private fun getFileSize(url_str: String): String {
        try {
            val myUrl = URL(url_str)
            Thread {
                try {
                    val urlConnection: URLConnection = myUrl.openConnection()
                    urlConnection.connect()
                    file_size = java.lang.String.valueOf(urlConnection.getContentLength())
                    Log.i("File Size: ", "file_size = $file_size")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file_size
    }

    fun uploadMedia(
        chatMessage: ChatMessage?,
        uploadCallbacks: ProgressRequestBody.UploadCallbacks,
    ) {
        /* smackManager.sendMessage(chatMessage).subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Consumer<Boolean?>() {
                 @Throws(Exception::class)
                 fun accept(sent: Boolean) {
                     if (sent) {
                         uploadCallbacks.onFinish(chatMessage)
                     } else uploadCallbacks.onError(chatMessage)
                 }
             })*/
    }

    fun postShare(type: String?, rawChatMessage: ChatMessage, from: String?, to: String?) {
        /*when (type) {
            SmackConstants.IMAGE_TYPE -> {
                val imageChatMessage: ChatMessage = PrepareMessageFactory.prepareImageMessage(
                    from,
                    to,
                    rawChatMessage.getUniqueCode(),
                    rawChatMessage.getMessageBody(),
                    rawChatMessage.getCaption(),
                    rawChatMessage.getMessageThumb(),
                    rawChatMessage.getFileSize()
                )
                uploadMedia(imageChatMessage, this)
            }
            SmackConstants.VIDEO_TYPE -> {
                val videoChatMessage: ChatMessage = PrepareMessageFactory.prepareVideoMessage(
                    from,
                    to,
                    rawChatMessage.getUniqueCode(),
                    rawChatMessage.getMessageBody(),
                    rawChatMessage.getCaption(),
                    rawChatMessage.getMessageThumb(),
                    rawChatMessage.getFileSize()
                )
                uploadMedia(videoChatMessage, this)
            }
        }*/
    }

    fun getItemViewType(position: Int): String {
        var return_type = ""
        if (videoListAdapter!!.postsLists[position].video != null && !videoListAdapter!!.postsLists[position].video!!.isEmpty()
        ) {
            return_type = Constants.VIDEO_TYPE
        } else if (videoListAdapter!!.postsLists.get(position).image != null && !videoListAdapter!!.postsLists[position].image!!.isEmpty()
        ) {
            return_type = Constants.IMAGE_TYPE
        }
        return return_type
    }

    /*private fun unfollowAlert(post: PostsList, position: Int) {
        val builder: AlertDialog.Builder = Builder(getContext())
        val inflater: LayoutInflater = LayoutInflater.from(getActivity())
        val view: View = inflater.inflate(R.layout.layout_unfollow_alert, null)
        builder.setView(view)
        val tvUnfollowMsg: TextView = view.findViewById(R.id.tvUnfollowMsg)
        val img_profile: ImageView = view.findViewById(R.id.img_profile)
        tvUnfollowMsg.setText(getString(R.string.unfollow_msg, post.username))
        GlideHelper.loadFromUrl(
            img_profile.getContext(),
            post.userimage,
            R.drawable.default_user_avatar,
            img_profile
        )
        val btnUnfollow: Button = view.findViewById(R.id.btnUnfollow)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
        btnUnfollow.setOnClickListener(object : OnClickListener() {
            fun onClick(v: View?) {
                callFollowUserApi(
                    post.userId,
                    "0",
                    dataManager.prefHelper().getUser().getPhone(),
                    position,
                    post
                )
                alertDialog.dismiss()
            }
        })
        btnCancel.setOnClickListener(object : OnClickListener() {
            fun onClick(v: View?) {
                alertDialog.dismiss()
            }
        })
        alertDialog = builder.show()
    }*/

    fun callFollowUserApi(
        followingId: String,
        status: String,
        userId: String?,
        position: Int,
        post: PostsList,
    ) {
        //  getBridge().showLoading();
        /* val call: Call<FollowUser> = dataManager.apiHelper().followUser(followingId, status, userId)
         call.enqueue(object : Callback<FollowUser?>() {
             fun onResponse(call: Call<FollowUser?>?, response: Response<FollowUser?>) {
                 if (response.body() != null) {
                     if (response.body().getResponse().getCode().equals("200")) {
                         post.follow.status = response.body().getResponse().getFollow().getStatus()
                         // videoListAdapter.getPostsLists().set(position,post);
                         videoListAdapter.notifyItemChanged(position, post)
                         var pos = 0
                         for (d in videoListAdapter.getPostsLists()) {
                             if (d.userId == followingId) {
                                 d.follow.status =
                                     response.body().getResponse().getFollow().getStatus()
                                 videoListAdapter.getPostsLists().set(pos, post)
                                 pos = pos + 1
                             }
                             //something here
                         }
                         videoListAdapter.notifyDataSetChanged()
                     }
                     if (status == "0") {
                         Toast.makeText(getContext(), "Unfollow Successfully", Toast.LENGTH_SHORT)
                             .show()
                     }
                 } else {
                     // getBridge().getViewUtil().showSnack(R.string.msg_check_your_number);
                 }
             }

             fun onFailure(call: Call<FollowUser?>?, t: Throwable?) {
                 // getBridge().getViewUtil().showSnack(R.string.msg_something_wrong);
                 //  getBridge().showContent();
             }
         })*/
    }

    fun callAddRemoveFaourite(
        loggedInUserId: String?,
        postId: String?,
        position: Int,
        post: PostsList,
    ) {
        //  getBridge().showLoading();
        /*val call: Call<FaouritePost> =
            dataManager.apiHelper().addRemoveFavourite(loggedInUserId, postId)
        call.enqueue(object : Callback<FaouritePost?>() {
            fun onResponse(call: Call<FaouritePost?>?, response: Response<FaouritePost?>) {
                if (response.body() != null) {
                    if (response.body().getResponse().getCode().equals("200")) {
                        post.favourite.status =
                            response.body().getResponse().getFavourite().getStatus()
                        // videoListAdapter.getPostsLists().set(position,post);
                        if (isFavourite == "1") {
                            videoListAdapter.getPostsLists().remove(position)
                            videoListAdapter.notifyItemRemoved(position)
                        } else {
                            videoListAdapter.notifyItemChanged(position, post)
                        }
                        Toast.makeText(
                            getContext(),
                            response.body().getResponse().getMessage(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // Toast.makeText(getContext(),"success",Toast.LENGTH_SHORT).show();
                } else {
                    // getBridge().getViewUtil().showSnack(R.string.msg_check_your_number);
                }
            }

            fun onFailure(call: Call<FaouritePost?>?, t: Throwable?) {
                // getBridge().getViewUtil().showSnack(R.string.msg_something_wrong);
                //  getBridge().showContent();
            }
        })*/
    }

    fun callAddPostReaction(
        userId: String?,
        reaction: String?,
        postId: String?,
        position: Int,
        post: PostsList,
    ) {
        /*val call: Call<PostReaction> =
            dataManager.apiHelper().addPostReaction(postId, reaction, userId)
        call.enqueue(object : Callback<PostReaction?>() {
            fun onResponse(call: Call<PostReaction?>?, response: Response<PostReaction?>) {
                if (response.body() != null) {
                    if (response.body().getResponse().getCode().equals("200")) {
                        post.reaction.likes = response.body().getResponse().getReaction().getLikes()
                        post.reaction.dislikes =
                            response.body().getResponse().getReaction().getDislikes()
                        post.reaction.yourReaction =
                            response.body().getResponse().getReaction().getYourReaction()
                        //  videoListAdapter.getPostsLists().set(position,post);
                        videoListAdapter.notifyItemChanged(position, post)
                    }
                }
            }

            fun onFailure(call: Call<PostReaction?>?, t: Throwable?) {}
        })*/
    }

    fun reportPost(postId: String?, reason: String?, userId: String?) {
        //  getBridge().showLoading();

        viewModel.viewModelScope.launch {
            viewModel.reportPost(postId!!, reason!!, userId!!)
        }
    }


    private fun makeServerCallForIncreaseView(postId: String, position: Int) {
        /*val call: Call<ViewIncreaseData> = dataManager.apiHelper().increasePostView(postId)
        call.enqueue(object : Callback<ViewIncreaseData?>() {
            fun onResponse(
                @NonNull call: Call<ViewIncreaseData?>?,
                @NonNull response: Response<ViewIncreaseData?>
            ) {
                if (response.body() != null) {
                    if (response.body().getResponse().getCode().equals("200")) {
                        val totalCounts: String = response.body().getResponse().getTotalViews()
                        videoListAdapter.getPostsLists().get(position).setTotalViews(totalCounts)
                        // Toast.makeText(getContext(), ""+totalCounts, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            fun onFailure(call: Call<ViewIncreaseData?>?, t: Throwable?) {}
        })*/
    }

    companion object {
        var instance: WorldwideFragment? = null
    }

    override fun getViewModel() = WorldWideViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentWorldWideBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = WorldWideRepository(MyApi.getInstance())


    override fun onContactSelected(contactsList: List<ContactsContract.Contacts?>?) {
    }

    override fun increaseViewCount(postId: String?, position: Int) {
        makeServerCallForIncreaseView(postId!!, position)
    }


}