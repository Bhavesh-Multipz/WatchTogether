package com.instaconnect.android.ui.profile

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityPrivateProfileBinding
import com.instaconnect.android.fileHelper.FileUtils
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.Utils.toast
import com.instaconnect.android.utils.helper_classes.GlideHelper
import com.instaconnect.android.utils.models.User
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.createFormData
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.Executors

class PrivateProfileActivity : AppCompatActivity(), View.OnClickListener {
    private val permissionsRequestCode = 111
    private lateinit var binding: ActivityPrivateProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private var fileUtils: FileUtils? = null
    private lateinit var managePermissions: ManagePermissions
    private var mCurrentPhotoPath: String? = null

    var userId = ""
    var list = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrivateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(
                ProfileRepository(
                    MyApi.getInstance()
                )
            )
        ).get(ProfileViewModel::class.java)

        managePermissions = ManagePermissions(this, list.toList(), permissionsRequestCode)
        fileUtils = FileUtils(this)

        setOnClickListener()

        setUserData()
        viewModel.profileResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        if(it.value.response!!.code == "200"){
                            val user = User()
                            user.phone = userId
                            user.name = it.value.response!!.username!!
                            user.avatar = it.value.response!!.image!!
                            Prefrences.savePreferencesString(this,Constants.PREF_USER_NAME, it.value.response!!.username!!)
                            Prefrences.savePreferencesString(this,Constants.PREF_USER_PROFILE_PIC, it.value.response!!.image!!)
                            Prefrences.setUser(user)
                            Prefrences.savePreferencesBoolean(this, Constants.LOGIN_STATUS, true)
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        } else {
                            ToastUtil.showToast(it.value.response!!.message!!)
                        }
                    }
                }
                is Resource.Failure -> {
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }
    }

    private fun setOnClickListener() {
        binding.btnContinue.setOnClickListener(this)
        binding.cardImg.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
    }

    private fun setUserData() {
        val bn = intent.extras
        val name = bn!!.getString("userName")
        val userProfile = bn.getString("profilePhoto")

        GlideHelper.loadFromUrl(this, userProfile, R.drawable.loader, binding.imgProfile)
        /*val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap? = null
        executor.execute {
            val imageURL = userProfile
            try {
                val openStream = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(openStream)
                handler.post {
                    binding.imgProfile.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

        binding.etName.setText(name)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentUtil.PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            //setImage(data!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_continue -> {
                saveProfile()
            }

            R.id.card_img -> {
                if (managePermissions.checkPermissions()) {
                    TedImagePicker.with(this)
                        .start { uri -> showSingleImage(uri) }
                }
            }

            R.id.iv_back ->{
                finish()
            }
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        userId = Prefrences.getPreferences(this, Constants.PREF_USER_ID)!!
        if (name.isNotEmpty()) {

            if (Utils.isConnected(this)) {

                var filePart: MultipartBody.Part? = null
                val params = HashMap<String, String>()

                if (mCurrentPhotoPath != null && mCurrentPhotoPath!!.isNotEmpty()) {
                    val mediaFile = File(mCurrentPhotoPath!!)
                    val mediaFileBody: ProgressRequestBody = ProgressRequestBody(
                        mediaFile,
                        ProgressRequestBody.IMAGE,null
                    )
                    filePart = createFormData(
                        "image",
                        mediaFile.name,
                        mediaFileBody
                    )
                } else {

                    val attachmentEmpty: RequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
                    filePart = createFormData(
                        "image",
                        "",
                        attachmentEmpty
                    )
                }

                params["user_id"] = userId
                params["username"] = name

                viewModel.viewModelScope.launch {
                    viewModel.addProfile(filePart!!, params)
                }

            } else {

            }
        } else {
            ToastUtil.showToast("Please enter name")
        }
    }

    private fun showSingleImage(uri: Uri) {
        mCurrentPhotoPath = FileUtil.getPath(ToastUtil.context, uri)
        Glide.with(ToastUtil.context)
            .load(mCurrentPhotoPath)
            .into(binding.imgProfile)
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
                    TedImagePicker.with(this)
                        .start { uri -> showSingleImage(uri) }
                } else {
                    this.toast("Permissions denied.")
                }
                return
            }
        }
    }

}