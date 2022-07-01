package com.instaconnect.android.ui.fragment.more_setting

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginManager
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.databinding.ActivityMoreSettingsNewBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.block_user.BlockUserActivity
import com.instaconnect.android.ui.login.LoginActivity
import com.instaconnect.android.ui.notification_list.NotificationListActivity
import com.instaconnect.android.ui.profile.PrivateProfileActivity
import com.instaconnect.android.ui.terms_webview.TermsWebViewActivity
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.Utils.visible
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

class MoreSettingsFragment : BaseFragment<MoreSettingViewModel,ActivityMoreSettingsNewBinding, MoreSettingRepository>(),
    View.OnClickListener {

    var reviewManager: ReviewManager? = null
    var notificationStatus : String= ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationStatus = Prefrences.getPreferences(requireContext(), Constants.PREF_NOTIFICATION_STATUS)!!
        setView()

        viewModel.toggleNotificationResponse.observe(requireActivity()) {

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null && it.value.response!!.code.equals("200")) {
                        Prefrences.savePreferencesString(requireContext(), Constants.PREF_NOTIFICATION_STATUS,it.value.response!!.notificationStatus!!)
                        binding.notificationSwitch.isChecked = it.value.response!!.notificationStatus
                            .equals("1")
                        if (it.value.response!!.notificationStatus.equals("1")) {
                            Toast.makeText(context, "Notifications On", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Notifications Off", Toast.LENGTH_SHORT).show()
                        }
                    } else if( it.value.response!!.code.equals("301")){
                        if(it.value.response!!.message!!.contains("ON")){
                            Prefrences.savePreferencesString(requireContext(), Constants.PREF_NOTIFICATION_STATUS,"1")
                        }
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                }
                else -> {}
            }
        }

        viewModel.deleteAccountResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.response != null && it.value.response.code.equals("200")) {
                        ToastUtil.showToast(it.value.response.message!!)

                        Prefrences.savePreferencesBoolean(requireContext(), Constants.LOGIN_STATUS, false)
                        Prefrences.getBooleanPreferences(requireContext(), Constants.LOGIN_STATUS)!!
                        val intent = Intent(activity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                }
                else -> {}
            }
        }
    }

    private fun setView() {
        binding.imgBack.setOnClickListener(this)
        binding.linSettingLogout.setOnClickListener(this)
        binding.linSettingNotification.setOnClickListener(this)
        binding.linSettingRateApp.setOnClickListener(this)
        binding.linSettingFeednack.setOnClickListener(this)
        binding.linSettingPrivacy.setOnClickListener(this)
        binding.linTermsCondition.setOnClickListener(this)
        binding.linSettingShareApp.setOnClickListener(this)
        binding.linSettingProfile.setOnClickListener(this)
        binding.linSettingBlockUser.setOnClickListener(this)
        binding.linAccountDelete.setOnClickListener(this)
        binding.notificationSwitch.isChecked = notificationStatus == "1"
        binding.notificationSwitch.setOnCheckedChangeListener {
                _, isChecked -> enableDisableNotification(if (isChecked) "1" else "0")
        }
    }

    private fun enableDisableNotification(notificationStatus: String) {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.enableDisableNotification(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, notificationStatus)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_back -> {}
            R.id.lin_setting_logout -> showDialogForLogout()
            R.id.lin_account_delete -> showDialogForAccountDelete()
            R.id.lin_setting_notification -> {
                val intent = Intent(context, NotificationListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

            R.id.lin_setting_rate_app ->
                showRateApp()

            R.id.lin_setting_feednack -> {
                val feedbackdialog = DialogFeedback(requireContext())
                feedbackdialog.show(childFragmentManager, "DialogFeedback")
            }

            R.id.lin_setting_privacy -> {
                val intent = Intent(requireActivity(), TermsWebViewActivity::class.java)
                intent.putExtra("TYPE", "Privacy")
                startActivity(intent)
            }

            R.id.lin_terms_condition -> {
                val intent = Intent(requireActivity(), TermsWebViewActivity::class.java)
                intent.putExtra("TYPE", "Terms")
                startActivity(intent)
            }

            R.id.lin_setting_profile -> callPrivateProfile()

            R.id.lin_setting_share_app -> {
                val dialogShareApp = DialogShareApp(requireContext())
                dialogShareApp.show(parentFragmentManager, "DialogRateApp")
            }

            R.id.lin_setting_block_user -> {
                startActivity(Intent(requireActivity(), BlockUserActivity::class.java))
            }
        }
    }


    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }
    private fun showDialogForAccountDelete() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_delete_your_account)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvDelete = dialog.findViewById<TextView>(R.id.tvDelete)
        tvDelete.setOnClickListener { v: View? ->

            deleteUserAccount()

            dialog.dismiss()
        }
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.show()
    }

    private fun deleteUserAccount() {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.deleteUserAccount(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!)
        }
    }

    private fun shareApplication() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "Here is the share content body"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))

        /*Intent shareAppIntent =new Intent();
        shareAppIntent.setType("text/plain");
        shareAppIntent.setAction(Intent.ACTION_SEND);
        shareAppIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android Studio Pro");
        shareAppIntent.putExtra(Intent.EXTRA_TEXT,"Learn Android App Development https://play.google.com/store/apps/details?id=com.tutorial.personal.androidstudiopro ");

        startActivity(shareAppIntent);*/
    }

    private fun showDialogForLogout() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_signout_new)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvLogOut = dialog.findViewById<TextView>(R.id.tvLogout)
        tvLogOut.setOnClickListener { v: View? ->
            Prefrences.savePreferencesBoolean(requireContext(), Constants.LOGIN_STATUS, false)
            Prefrences.getBooleanPreferences(requireContext(), Constants.LOGIN_STATUS)!!
            try {
                LoginManager.getInstance().logOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.show()
    }

    private fun callPrivateProfile() {

        val intent = Intent(requireActivity(), PrivateProfileActivity::class.java)
        intent.putExtra("userName", Prefrences.getPreferences(requireContext(), Constants.PREF_USER_NAME)!!)
        intent.putExtra("profilePhoto", Prefrences.getPreferences(requireContext(), Constants.PREF_USER_PROFILE_PIC)!!)
        startActivity(intent)
    }

    private fun showInAppReview() {
        reviewManager = ReviewManagerFactory.create(requireActivity())
        val reviewInfoTask: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        reviewInfoTask.addOnCompleteListener { task ->
            if (reviewInfoTask.isSuccessful) {
                val reviewInfo: ReviewInfo = reviewInfoTask.getResult()
                val flow: Task<Void> = reviewManager!!.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnSuccessListener {
                    if (task.isSuccessful) {

                    } else {

                    }
                    Toast.makeText(activity, "InAppReviewSuccess", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Some error : Try again later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRateApp() {
        reviewManager = ReviewManagerFactory.create(requireContext())
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = reviewManager!!.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                    Toast.makeText(activity, "Already Submitted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "InAppReviewFailed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)
    = ActivityMoreSettingsNewBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = MoreSettingRepository(MyApi.getInstance())

    companion object {
        var instance: MoreSettingsFragment? = null
    }

    override fun getViewModel() = MoreSettingViewModel::class.java
}