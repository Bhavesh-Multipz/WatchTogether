package com.instaconnect.android.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.BuildConfig
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityLoginBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.profile.PrivateProfileActivity
import com.instaconnect.android.ui.terms_webview.TermsWebViewActivity
import com.instaconnect.android.utils.CommonUtil
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.models.User
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    var dotsCount = 0
    lateinit var dots: Array<ImageView?>
    var images = intArrayOf(R.drawable.intro_1, R.drawable.intro_2, R.drawable.intro_3)
    var slidingImageAdapter: IntoScreenAdapter? = null
    var callbackManager: CallbackManager? = null
    var gso: GoogleSignInOptions? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var userName = ""
    var userPhotoUri = ""
    var number = ""
    var deviceToken = ""
    var currentVersion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(
                LoginRepository(
                    MyApi.getInstance()
                )
            )
        )[LoginViewModel::class.java]

        setClickListener()
        setUpIntroScreen()

        currentVersion = packageManager.getPackageInfo(packageName, 0).versionName

        deviceToken = Prefrences.getPreferences(this@LoginActivity, Constants.PREF_DEVICE_TOKEN).toString()
        callbackManager = create()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso!!)

        try {
            mGoogleSignInClient!!.signOut()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        try {
            LoginManager.getInstance().logOut()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        viewModel.sendSocialIdResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {

                        viewModel.viewModelScope.launch {
                            viewModel.loadPreference(currentVersion, "android", Prefrences.getPreferences(this@LoginActivity, Constants.PREF_USER_ID)!!)
                        }

                        when {
                            it.value.response!!.code.equals("200") -> {

                                /*if(it.value.response!!.message!!.contains("deactivated")){
                                                    ToastUtil.showToast(it.value.response!!.message!!)
                                                } else {*/

                                /*if(it.value.response!!.username != null && it.value.response!!.userProfileUrl != null &&
                                                        !it.value.response!!.userProfileUrl!!.contains(""))*/

                                Prefrences.savePreferencesBoolean(this, Constants.LOGIN_STATUS, true)
                                val user = User()
                                startActivity(Intent(this, HomeActivity::class.java))
                                Prefrences.savePreferencesString(this, Constants.PREF_USER_ID, number)

                                if (it.value.response!!.username!!.isEmpty()) {
                                    Prefrences.savePreferencesString(
                                        this,
                                        Constants.PREF_USER_NAME,
                                        userName
                                    )
                                    Prefrences.savePreferencesString(
                                        this,
                                        Constants.PREF_USER_PROFILE_PIC,
                                        userPhotoUri
                                    )

                                    user.phone = number
                                    user.name = userName
                                    user.avatar = userPhotoUri

                                } else {
                                    Prefrences.savePreferencesString(
                                        this,
                                        Constants.PREF_USER_NAME,
                                        it.value.response!!.username!!
                                    )
                                    Prefrences.savePreferencesString(
                                        this,
                                        Constants.PREF_USER_PROFILE_PIC,
                                        it.value.response!!.userProfileUrl!!
                                    )

                                    user.phone = number
                                    user.name = it.value.response!!.username!!
                                    user.avatar = it.value.response!!.userProfileUrl!!
                                }

                                Prefrences.setUser(user)
                    //                            }


                            }
                            it.value.response!!.code.equals("201") -> {
                                Prefrences.savePreferencesString(this, Constants.PREF_USER_ID, number)
                                val intent = Intent(this, PrivateProfileActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.putExtra("userName", userName)
                                intent.putExtra("profilePhoto", userPhotoUri)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                ToastUtil.showToast(getString(R.string.msg_something_wrong))
                            }
                        }

                    }
                }
                is Resource.Failure -> {
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }

        viewModel.loadPreferenceResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {}
                }
                is Resource.Failure -> {}
                else -> {}
            }
        })
    }

    private fun setClickListener() {
        binding.imgGoogle.setOnClickListener(this)
        binding.imgFb.setOnClickListener(this)
        binding.tvTerms.setOnClickListener(this)
        binding.tvHelp.setOnClickListener(this)
    }

    private fun setUpIntroScreen() {
        dotsCount = images.size
        dots = arrayOfNulls(images.size)
        binding.SliderDots.removeAllViews()
        for (i in 0 until dotsCount) {
            dots[i] = ImageView(this)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.circle_view_white
                )
            )
            val params = LinearLayout.LayoutParams(20, 20)
            params.setMargins(4, 0, 4, 0)
            binding.SliderDots.addView(dots[i], params)
        }
        try {
            dots[0]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.circle_view_black
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        slidingImageAdapter = IntoScreenAdapter(this, images, this)
        binding.viewpager.adapter = slidingImageAdapter
        binding.viewpager.offscreenPageLimit = images.size
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0 until dotsCount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@LoginActivity,
                            R.drawable.circle_view_white
                        )
                    )
                }
                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@LoginActivity,
                        R.drawable.circle_view_black
                    )
                )
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.img_google -> {
                signIn()
            }
            R.id.img_fb -> {
                LoginManager.getInstance().logOut()
                LoginManager.getInstance().logInWithReadPermissions(
                    this, callbackManager!!, listOf("public_profile")
                )
            }
            R.id.tv_terms -> {
                val intent = Intent(this, TermsWebViewActivity::class.java)
                intent.putExtra("TYPE", "Terms")
                startActivity(intent)
            }
            R.id.tv_help -> {}
        }

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    getUserProfile(result.accessToken, result.accessToken.userId)
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun signIn() {
        mGoogleSignInClient!!.signOut()
        val signInIntent = mGoogleSignInClient!!.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    var googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("GoogleSingin", account.id + "....." + account.email)

            number = account.id!!
            userName = account.displayName!!
            userPhotoUri = account.photoUrl.toString()
            viewModel.viewModelScope.launch {
                viewModel.sendSocialId(
                    account!!.id!!, "0", deviceToken, "android", account.displayName!!,
                    if (account.photoUrl != null) account.photoUrl.toString() else ""
                )
            }
        } catch (e: ApiException) {
            Log.w("SigningError", "signInResult:failed code=" + e.statusCode)
        }
    }

    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, middle_name, last_name, name, picture, email")
        GraphRequest(
            token, "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

                if (jsonObject!!.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    number = facebookId.toString()
                } else {
                    number = resources.getString(R.string.not_exist)
                }

                if (jsonObject.has("name")) {
                    val facebookName = jsonObject.getString("name")
                    userName = facebookName

                } else {
                    userName = resources.getString(R.string.not_exist)
                }

                if (jsonObject.has("picture")) {
                    val facebookPictureObject = jsonObject.getJSONObject("picture")
                    if (facebookPictureObject.has("data")) {
                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
                        if (facebookDataObject.has("url")) {
                            val facebookProfilePicURL = facebookDataObject.getString("url")
                            userPhotoUri = facebookProfilePicURL
                        }
                    }
                } else {
                    userPhotoUri = resources.getString(R.string.not_exist)
                }

                if (jsonObject != null && userName.isNotEmpty()) {
                    viewModel.viewModelScope.launch {
                        viewModel.sendSocialId(
                            number,
                            "0",
                            deviceToken,
                            "android",
                            userName,
                            userPhotoUri.toString()
                        )
                    }
                } else {
                    viewModel.viewModelScope.launch {
                        viewModel.sendSocialId(
                            number,
                            "0",
                            deviceToken,
                            "android",
                            "",
                            ""
                        )
                    }
                }

            }).executeAsync()
    }
}