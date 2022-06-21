package com.instaconnect.android.ui.fragment.add_post

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.databinding.FragmentCaptureBinding
import com.instaconnect.android.listener.OnCategorySelectedListener
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter.Companion.extractYTId
import com.instaconnect.android.ui.trending_websites.TrendingWebsitesActivity
import com.instaconnect.android.ui.youtube_webview.YoutubeWebViewActivity
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.LocationUtil
import com.instaconnect.android.utils.ProgressRequestBody
import com.instaconnect.android.utils.Utils.visible
import com.instaconnect.android.utils.models.Trending
import com.instaconnect.android.widget.FlashView.FlashListener
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class CaptureFragment : BaseFragment<CaptureFragmentViewModel, FragmentCaptureBinding, CaptureFragmentRepository>(),

    View.OnClickListener, FlashListener, OnCategorySelectedListener, ProgressRequestBody.UploadCallbacks {

    var myOrientationEventListener: OrientationEventListener? = null

    private val PICK_MEDIA = 102
    private var captureType = ""
    private var dialog: Dialog? = null
    private var webLinkAdapter: WebLinkAdapter? = null
    private var webLinksItemArrayList: ArrayList<WebLinksItem>? = null
    private var trending: Trending? = null
    private var countryName: String? = null
    private var categoryName: String? = null
    var videoView: VideoView? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getAndManageData()

//        viewModel.init(binding.surfaceCamera, binding.surfaceCamera.getHolder())
        /*binding.btnCapture.setOnLongClickListener(View.OnLongClickListener {
            if (captureType == "Video") {
                binding.tvTimer.setVisibility(View.VISIBLE)
                binding.audioPulsator.start()
                viewModel.onTouchDown()
                video = true
                return@OnLongClickListener false
            }
            true
        })*/
        /*binding.btnCapture.setOnClickListener(View.OnClickListener {
            if (clicked) return@OnClickListener
            clicked = true
            var time = 0
            if (video) time = 1000
            binding.frMain.showLoading()
            binding.audioPulsator.stop()
            Handler().postDelayed(Runnable { viewModel.onTouchUp() }, time)
            binding.tvTimer.setVisibility(View.INVISIBLE)
        })*/
        /*binding.btnCapture.setOnTouchListener(View.OnTouchListener { view, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_UP) {
                view.performClick()
            }
            false
        })*/
        setEventListeners()

        viewModel.getWebLinksResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null) {
                        if (it.value.response!!.code.equals("200")) {
                            webLinkAdapter!!.updateList(it.value.response!!.webLinks!!)
                        } else {
                            Toast.makeText(context, it.value.response!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }

    }

    private fun getAndManageData() {
        val bundle: Bundle? = arguments
        if (bundle != null) {
            captureType = bundle.getString("CaptureType").toString()
            categoryName = bundle.getString("PostType")
        }
        viewModel.setPostType(categoryName!!)
        viewModel.setCaptureType(captureType)
        webLinksItemArrayList = ArrayList()
        webLinkAdapter = WebLinkAdapter(requireContext(), webLinksItemArrayList!!, captureType, categoryName!!)
        binding.webLinkRecyclerview.adapter = webLinkAdapter!!
        binding.webLinkRecyclerview.layoutManager = GridLayoutManager(context, 2)
        if (captureType == "Video") {
            binding.tvCaptureText.text = "Hold for Video"
            binding.ivGallery.visibility = View.VISIBLE
            binding.ivSwitchCamera.visibility = View.VISIBLE
            binding.frMain.visibility = View.VISIBLE
            binding.relText.visibility = View.GONE
            binding.relWatchTogether.visibility = View.GONE
        } else if (captureType == "Image") {
            binding.tvCaptureText.text = "Tap for Photo"
            binding.ivGallery.visibility = View.VISIBLE
            binding.ivSwitchCamera.visibility = View.VISIBLE
            binding.frMain.visibility = View.VISIBLE
            binding.relWatchTogether.visibility = View.GONE
        } else if (captureType == "Text") {
            binding.frMain.visibility = View.GONE
            binding.relWatchTogether.visibility = View.GONE
            binding.relText.visibility = View.VISIBLE
        } else if (captureType == "WatchTogether") {
            binding.frMain.visibility = View.GONE
            binding.ivGallery.visibility = View.GONE
            binding.ivSwitchCamera.visibility = View.GONE
            binding.relText.visibility = View.GONE
            binding.relWatchTogether.visibility = View.VISIBLE
        }
        if (captureType == "Text" && categoryName == "My Community") {
            binding.linCategory.visibility = View.GONE
        } else {
            binding.linCategory.visibility = View.VISIBLE
            getWebLinkData()
        }

        //----------------- onPostCreated --------------
        myOrientationEventListener = object : OrientationEventListener(context,
            SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                var or = 90
                or = if (orientation <= 50 || orientation >= 300) {
                    //viewModel.setOrientation(90)
                    90
                } else if (orientation >= 250) {
                    //viewModel.setOrientation(0)
                    0
                } else if (orientation <= 120) {
                    // viewModel.setOrientation(180)
                    180
                } else {
                    //viewModel.setOrientation(270)
                    270
                }
                //Log.d("Orientation", " = " + String.valueOf(or));
            }
        }

        myOrientationEventListener!!.enable()
    }

    private fun setEventListeners() {
        binding.ivGallery.setOnClickListener(this)
        binding.gallery.setOnClickListener(this)
        binding.ivCancel.setOnClickListener(this)
        binding.ivSwitchCamera.setOnClickListener(this)
        binding.ivCancelText.setOnClickListener(this)
        binding.linCategory.setOnClickListener(this)
        binding.tvTextPost.setOnClickListener(this)
        binding.ivCancelWatch.setOnClickListener(this)
        binding.relPopularWebsites.setOnClickListener(this)
        binding.tvGo.setOnClickListener(this)
    }

    private fun getWebLinkData() {
        viewModel.viewModelScope.launch {
            viewModel.getWebLinks("android")
        }
    }


    /*private val webLinkData: Unit
        private get() {
            binding.progressBar.setVisibility(View.VISIBLE)
            val call: Call<WebLinkResponse> = dataManager.apiHelper().getWebLinks("android")
            call.enqueue(object : Callback<WebLinkResponse?>() {
                fun onResponse(call: Call<WebLinkResponse?>?, response: Response<WebLinkResponse?>) {
                    binding.progressBar.setVisibility(View.GONE)
                    if (response.body() != null) {
                        if (response.body().getResponse() != null) {
                            if (response.body().getResponse().getCode().equals("200")) {
                                webLinkAdapter.updateList(response.body().getResponse().getWebLinks() as ArrayList<WebLinksItem?>)
                            } else {
                                Toast.makeText(context, response.body().getResponse().getMessage(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                fun onFailure(call: Call<WebLinkResponse?>?, t: Throwable) {
                    binding.progressBar.setVisibility(View.GONE)
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }*/
    private var video = false

    override fun onDestroy() {
        super.onDestroy()
    }

    /*@Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }*/
    private var clicked = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_MEDIA) {
                binding.frMain.showLoading()
//                viewModel.onPickMediaResult(fileUtils, data)
            }
        }
    }

    fun onBackPress(fragmentCount: Int): Boolean {
        return true
    }

    fun onRefresh(classToRefresh: String?) {}

    override fun flashAuto() {
//        viewModel.flashAuto()
    }

    override fun flashOff() {
//        viewModel.flashOff()
    }

    override fun flashOn() {
//        viewModel.flashOn()
    }

    fun hideControls() {
        binding.ivCancel.setVisibility(View.GONE)
        binding.flashView.setVisibility(View.GONE)
        binding.ivGallery.setVisibility(View.GONE)
        binding.gallery.setVisibility(View.GONE)
        binding.ivSwitchCamera.setVisibility(View.GONE)
    }

    fun reset() {
        binding.ivCancel.setVisibility(View.VISIBLE)
        binding.flashView.setVisibility(View.VISIBLE)
        binding.ivGallery.setVisibility(View.VISIBLE)
        binding.gallery.setVisibility(View.VISIBLE)
        binding.ivSwitchCamera.setVisibility(View.VISIBLE)
        showContent()
        video = false
        clicked = false
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.gallery, R.id.iv_gallery -> if (captureType == "Video" || captureType == "WatchTogether") {
                val pickPhoto = Intent(Intent.ACTION_GET_CONTENT)
                pickPhoto.type = "*/*"
                val mimetypes = arrayOf("video/*")
                pickPhoto.putExtra("android.intent.extra.MIME_TYPES", mimetypes)
                startActivityForResult(pickPhoto, PICK_MEDIA)
            } else if (captureType == "Image") {
                val pickPhoto = Intent(Intent.ACTION_GET_CONTENT)
                pickPhoto.type = "*/*"
                val mimetypes = arrayOf("image/*")
                pickPhoto.putExtra("android.intent.extra.MIME_TYPES", mimetypes)
                startActivityForResult(pickPhoto, PICK_MEDIA)
            }
            R.id.rel_popular_websites -> startActivity(Intent(context, TrendingWebsitesActivity::class.java))
            R.id.iv_cancelWatch, R.id.iv_cancelText, R.id.iv_cancel -> {}
            R.id.iv_switch_camera -> {
                /*viewModel.switchCamera()*/
            }
            R.id.linCategory -> {
                callCategoryDialog()
            }
            R.id.tvTextPost -> {
//                postAText()
            }
            R.id.tvGo -> {
                var enteredLink: String = binding.edtWebUrl.getText().toString()
                if (enteredLink.isEmpty()) {
                    ToastUtil.showToast("Enter Link")
                } else {
                    if (!enteredLink.contains("http")) {
                        enteredLink = "http://$enteredLink"
                        getDataFromYoutubeUrl(enteredLink)
                    } else {
                        getDataFromYoutubeUrl(enteredLink)
                    }
                }
            }
        }
    }

    private fun getDataFromYoutubeUrl(enteredLink: String) {
        if (enteredLink.contains("watch?v=") && enteredLink.contains("youtube.com") || enteredLink.contains("youtu.be")) {
            val youtubeVideoId: String = extractYTId(enteredLink)!!
            if (youtubeVideoId != null) {
                showVideoPreview(youtubeVideoId)
            }
        } else {
            val youtubeWebViewIntent1 = Intent(context, YoutubeWebViewActivity::class.java)
            youtubeWebViewIntent1.putExtra("PostType", categoryName)
            youtubeWebViewIntent1.putExtra("CaptureType", captureType)
            if (!enteredLink.contains("http")) {
                val newEnteredLink = "https://www.google.com/search?q=$enteredLink"
                youtubeWebViewIntent1.putExtra("URL_TO_LOAD", newEnteredLink)
            } else {
                youtubeWebViewIntent1.putExtra("URL_TO_LOAD", enteredLink)
            }
            startActivity(youtubeWebViewIntent1)
        }
    }

    private fun showVideoPreview(youtubeVideoId: String) {
        Thread {
            val chatMessage = ChatMessage()
            chatMessage.videoType = youtubeVideoId
            chatMessage.dataType = Constants.VIDEO_TYPE
            chatMessage.postType = categoryName!!
            chatMessage.captureType = captureType
//            startActivity(BaseIntent(requireActivity(), VideoPreviewsActivity::class.java, true).setModel(chatMessage))
        }.start()
    }

    /*private fun postAText() {
        if (categoryName == "My Community") dialog = Dialog(context, R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading...")
        progressDialog.show()
        val caption: String = binding.edtTextPostTitle.getText().toString().toString() + "!@#$" +
                binding.edtTextPostText.getText().toString()
        publicPostText(caption, categoryName,
            HomeActivity.userLocation.getLatitude(), HomeActivity.userLocation.getLongitude(),
            country, dataManager, this)
    }*/

    /*fun publicPostText(
        caption: String?, category: String?, lati: Double, longi: Double, countrySelected: String?,
        dataManager: DataManager, uploadCallbacks: UploadCallbacks
    ) {
        val user_id: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), dataManager.prefHelper().getUser().getPhone())
        val media: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "0")
        val lat: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), lati.toString())
        val lng: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), longi.toString())
        val rCat: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), category)
        val imageCaption: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), caption)
        val country: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), countrySelected)
        val call: Call<PublicPost> = dataManager.apiHelper().publicPostText(user_id, media, rCat, lat, lng, imageCaption, country)
        call.enqueue(object : Callback<PublicPost?>() {
            fun onResponse(call: Call<PublicPost?>?, response: Response<PublicPost?>) {
                if (response.body() != null && response.body().getResponse().getCode().equals("200")) {
                    val intent = Intent(activity, HomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    // finish();
                }
            }

            fun onFailure(call: Call<PublicPost?>?, t: Throwable?) {
                uploadCallbacks.onError(ChatMessage())
            }
        })
    }*/

    val country: String
        get() {
            if (countryName == null) countryName = LocationUtil.getCountryName(context, HomeActivity.userLocation)
            return if (countryName == null) resources.configuration.locale.displayCountry else countryName!!
        }

    private fun callCategoryDialog() {
        dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
        dialog!!.setContentView(R.layout.layout_trending_dialog)
        val trending_dialog_rv: RecyclerView = dialog!!.findViewById(R.id.trending_dialog_rv)
        trending_dialog_rv.setLayoutManager(GridLayoutManager(context, 3))
        /*val adapter = TrendingAdapter(context, fragmentUtil, 1, this, dataManager)
        trending_dialog_rv.setAdapter(adapter)
        adapter.setTrendings(setTrendingList())*/
        dialog!!.show()
    }

    fun setTrendingList(): ArrayList<Trending> {
        val trendingName = resources.getStringArray(R.array.trending)
        val trendingApiName = resources.getStringArray(R.array.category_api)
        //int[] trendingIcon = getResources().getIntArray(R.array.category_imgs);
        //TypedArray trendingIcon = getResources().obtainTypedArray(R.array.category_imgs);
        val trendingIcon = IntArray(trendingName.size)
        trendingIcon[0] = R.drawable.category_trending
        trendingIcon[1] = R.drawable.category_news
        trendingIcon[2] = R.drawable.category_movie
        trendingIcon[3] = R.drawable.category_travel
        trendingIcon[4] = R.drawable.category_music
        trendingIcon[5] = R.drawable.category_food
        trendingIcon[6] = R.drawable.category_fashion
        trendingIcon[7] = R.drawable.category_funny
        trendingIcon[8] = R.drawable.category_sports
        trendingIcon[9] = R.drawable.category_idea
        trendingIcon[10] = R.drawable.category_fitness
        trendingIcon[11] = R.drawable.category_wedding
        trendingIcon[12] = R.drawable.category_technology
        trendingIcon[13] = R.drawable.category_art
        trendingIcon[14] = R.drawable.category_video_games
        trendingIcon[15] = R.drawable.category_pets
        trendingIcon[16] = R.drawable.category_cars
        trendingIcon[17] = R.drawable.category_home_decor
        trendingIcon[18] = R.drawable.category_comics
        trendingIcon[19] = R.drawable.category_diy
        trendingIcon[20] = R.drawable.category_seasonal
        trendingIcon[21] = R.drawable.category_kidszone
        trendingIcon[22] = R.drawable.category_university
        trendingIcon[23] = R.drawable.category_events
        trendingIcon[24] = R.drawable.category_shopping
        trendingIcon[25] = R.drawable.category_finance
        trendingIcon[26] = R.drawable.category_real_estate
        val trendings: ArrayList<Trending> = ArrayList()
        for (i in trendingName.indices) {
            trendings.add(Trending(trendingName[i], trendingIcon[i], trendingApiName[i]))
        }
        return trendings
    }

    fun launchActivity(intent: BaseIntent?) {
        startActivity(intent)
    }

    fun getStringRes(id: Int): String {
        return getString(id)
    }

    fun showLoading() {
        binding.frMain.showLoading()
    }

    fun showContent() {
        binding.frMain.showContent()
    }

    fun getIntent(cls: Class<*>?, finish: Boolean): BaseIntent {
        return BaseIntent(requireActivity(), cls, finish)
    }

    fun setTimer(time: String?) {
        binding.tvTimer.text = time
    }

    fun showFlash(status: Boolean) {
        binding.flashView.visibility = if (status) View.VISIBLE else View.GONE
    }

    /*fun setCameraDisplayOrientation(currentCameraId: Int, camera: Camera?) {
        CameraUtil.setCameraDisplayOrientation(activity, currentCameraId, camera)
    }

    fun getRoatationAngle(camId: Int): Int {
        return CameraUtil.getRoatationAngle(activity, camId)
    }*/

    companion object {
        private val TAG = CaptureFragment::class.java.name
        var instance: CaptureFragment? = null
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCaptureBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = CaptureFragmentRepository(MyApi.getInstance())
    override fun onProgressUpdate(percentage: Int, chatMessage: ChatMessage) {
        if (dialog == null) return
        val progressBar: ProgressBar = dialog!!.findViewById(R.id.pbProcessing)
        progressBar.progress = percentage
    }

    override fun onError(chatMessage: ChatMessage) {
        if (dialog == null) return
        ToastUtil.showToast("Uploading failed")
    }

    override fun onFinish(chatMessage: ChatMessage) {

        if (dialog == null) return
        if (progressDialog != null) progressDialog!!.dismiss()
        Handler().postDelayed({
            dialog!!.dismiss()
        }, 200)
    }

    override fun getViewModel() = CaptureFragmentViewModel::class.java

    override fun onCategorySelected(trending: Trending?) {
        binding.edtTextPostCategory.setText(trending!!.api_name)
        binding.ivCategoryImage.setImageResource(trending.icon)
        categoryName = trending.api_name
        Log.e("SelectedCategory", "$categoryName...")
        this.trending = trending
        if (dialog!!.isShowing) {
            dialog!!.cancel()
        }
    }
}