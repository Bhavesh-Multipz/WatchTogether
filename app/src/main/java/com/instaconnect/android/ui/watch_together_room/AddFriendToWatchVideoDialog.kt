package com.instaconnect.android.ui.watch_together_room

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.allattentionhere.autoplayvideos.recyclerview.BasicRecyclerView
import com.allattentionhere.autoplayvideos.recyclerview.LazyLoadListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.instaconnect.android.R
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.friends.contact_fragment.ContactFragmentNew
import com.instaconnect.android.ui.watch_together_room.AddFriendToVideoListAdapter.AddFriendListListener
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences
import com.instaconnect.android.utils.Utils.visible
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch
import retrofit2.Call

class AddFriendToWatchVideoDialog(
    var ctx: Context,
    var postId: String,
    var addFriendListListener: AddFriendListListener,
    var viewModel: WatchTogetherVideoViewModel
) : BottomSheetDialogFragment(), LazyLoadListener,
    AddFriendListListener, View.OnClickListener {
    var dialog: BottomSheetDialog? = null

    //    DataManager dataManager;
    var call: Call<FriendListModel>? = null
    var recyclerView: BasicRecyclerView? = null
    var progressBar: ProgressBar? = null
    var myFriendListAdapter: AddFriendToVideoListAdapter? = null
    var myFriendList: List<FriendListModel.User> = ArrayList()
    var myContactList = ArrayList<Contacts>()
    var ed_search: AppCompatEditText? = null
    var searchKeyword = ""
    var linExploriiUsers: LinearLayout? = null
    var linContactUsers: LinearLayout? = null
    var currentSelectedType = "friends"
    var inviteContactFrg: FrameLayout? = null
    var page: Int = 0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        BlurKit.init(ctx)
        dialog = BottomSheetDialog(requireContext(), R.style.MyTransparentBottomSheetDialogTheme)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setOnShowListener { dialogInterface: DialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog as BottomSheetDialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {

        val bottomSheet = bottomSheetDialog.findViewById<View>(gun0912.tedimagepicker.R.id.design_bottom_sheet)
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = windowHeight
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    // Calculate window height for fullscreen use
    private val windowHeight: Int
        private get() {
            // Calculate window height for fullscreen use
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_add_friend_to_watch_video, container, false)
        val imageView = v.findViewById<ImageView>(R.id.img_bg)
        inviteContactFrg = v.findViewById(R.id.invite_contact_frg)
        ed_search = v.findViewById(R.id.ed_search)
        linExploriiUsers = v.findViewById(R.id.linExploriiUsers)
        linContactUsers = v.findViewById(R.id.linContactUsers)
        //        ImageView imageViewContact = v.findViewById(R.id.img_contact);
        imageView.setImageResource(R.drawable.main_bg)
        val relMain = v.findViewById<View>(R.id.rel_border)
        //TextView txt_contacts = v.findViewById(R.id.txt_contacts);
        recyclerView = v.findViewById(R.id.recycler_my_friend)
        progressBar = v.findViewById(R.id.progressBar)
        recyclerView!!.setLazyLoadListener(this)
        linExploriiUsers!!.setOnClickListener(this)
        linContactUsers!!.setOnClickListener(this)
        setAdapter()
        getUserData(currentSelectedType)
        searchProfile()
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(BlurKit.getInstance().fastBlur(imageView, 12, 0.12.toFloat()))
            }
        })

        viewModel.friendListResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    progressBar!!.visible(false)
                    if (it.value.response!!.code.equals("200")) {
                        if (it.value.response!!.userlist == null || it.value.response!!.userlist!!.isEmpty()
                        ) {
                            if (page == 1) {
                                //txtEmpty.setVisibility(View.VISIBLE);
                                //txtEmpty.setText(it.value.response.getMessage());
                            }
                        } else {
                            // txtEmpty.setVisibility(View.GONE);
                            myFriendList = it.value.response!!.userlist!!
                            myFriendListAdapter!!.addUser(myFriendList)
                        }
                        if (it.value.response!!.isLastPage === 0) {
                            recyclerView!!.isLoading = false
                        } else {
                            recyclerView!!.isNestedScrollingEnabled = false
                        }
                    } else if (it.value.response!!.code.equals("301")) {
                        if (page == 1) {
                            // txtEmpty.setVisibility(View.VISIBLE);
                            // txtEmpty.setText(it.value.response.getMessage());
                        } else {
                            //txtEmpty.setVisibility(View.GONE);
                        }
                    }
                }
                is Resource.Loading -> {
                    recyclerView!!.isLoading = true
                }
                is Resource.Failure -> {
                    progressBar!!.visible(false)
                    recyclerView!!.isLoading = false
                }
            }

        }

        viewModel.invitePeopleResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null){
                        if (it.value.response!!.code.equals("200")) {
                            Toast.makeText(context, it.value.response!!.message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                is Resource.Loading -> {
                    recyclerView!!.isLoading = true
                }
                is Resource.Failure -> {
                    recyclerView!!.isLoading = false
                }
                else -> {
                    recyclerView!!.isLoading = false
                }
            }

        }

        return v
    }

    private fun showContactFragment() {
        val bundle = Bundle()
        val frg = ContactFragmentNew()
        bundle.putBoolean("isSharing", true)
        frg.arguments = bundle
        childFragmentManager.beginTransaction().replace(R.id.invite_contact_frg, frg, "invite_contact_frg").commit()
    }

    private fun getUserData(currentSelectedType: String) {
        if (currentSelectedType == "friends") {
            recyclerView!!.visibility = View.VISIBLE
            inviteContactFrg!!.visibility = View.GONE
            ed_search!!.visibility = View.VISIBLE
            getAddFriendList(page, searchKeyword)
        } else {
            recyclerView!!.visibility = View.GONE
            inviteContactFrg!!.visibility = View.VISIBLE
            ed_search!!.visibility = View.GONE
            showContactFragment()
        }
    }

    private fun searchProfile() {

        ed_search!!.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {

                progressBar!!.visible(true)
                searchKeyword = ed_search!!.text.toString()
                Handler(Looper.myLooper()!!).postDelayed({

                    recyclerView!!.resetLazyLoadListener()
                    myFriendListAdapter!!.clear()
                    getAddFriendList(page, searchKeyword)
                }, 400)
            }
            false
        }
    }

    private fun setAdapter() {
        myFriendListAdapter = AddFriendToVideoListAdapter(myFriendList as MutableList<FriendListModel.User>, ctx, this)
        recyclerView!!.layoutManager = GridLayoutManager(activity, 3)
        recyclerView!!.adapter = myFriendListAdapter
    }

    fun getAddFriendList(page: Int, search: String?) {
        this.page = page
        recyclerView!!.isLoading = true
        viewModel.viewModelScope.launch {
            viewModel.getAddFriendList(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, search!!, page+1)
        }
    }


    override fun onScrollNext(page: Int, totalItemsCount: Int): Boolean {
        this.page = page
        getAddFriendList(page, searchKeyword)
        return true
    }

    override fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean {
        return false
    }

    override fun onScrolling() {}

    override fun onScrollStateChage(upDown: Int) {}

    override fun onAddFriendClick(position: Int, user: FriendListModel.User?, view: View?) {
        // invite Watch Together user for my watch together party...
        invitePeopleForWatchTogetherParty(Prefrences.getPreferences(requireContext(), Constants.PREF_USER_ID)!!, user!!.userId!!)
    }

    private fun invitePeopleForWatchTogetherParty(loginUserId: String, otherUserId: String) {
        viewModel.viewModelScope.launch {
            viewModel.invitePeopleToWatchVideo(loginUserId,otherUserId, postId)
        }
    }

    override fun onFriendView(position: Int, user: FriendListModel.User?, view: View?) {

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.linExploriiUsers -> getUserData("friends")
            R.id.linContactUsers -> getUserData("my_contacts")
        }
    }
}