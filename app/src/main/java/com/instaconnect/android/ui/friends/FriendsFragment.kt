package com.instaconnect.android.ui.friends

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.databinding.FragmentFriendsBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.ui.friends.add_friend_fragment.AddFriendFragment
import com.instaconnect.android.ui.friends.contact_fragment.ContactFragmentNew
import com.instaconnect.android.ui.friends.friend_request_fragment.FriendRequestFragment
import com.instaconnect.android.ui.friends.my_friends.MyFriendFragment
import com.instaconnect.android.utils.ManagePermissions
import com.instaconnect.android.utils.PermissionUtil

class FriendsFragment :
    BaseFragment<FriendsFragmentViewModel, FragmentFriendsBinding, FriendsRepository>(),
    View.OnClickListener {
    private var isShowInviteFrag: Boolean = true
    private val permissionsRequestCode = 123
    var permissionUtil: PermissionUtil? = null
    private lateinit var managePermissions: ManagePermissions
    var list = arrayOf(
        Manifest.permission.READ_CONTACTS
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        permissionUtil = PermissionUtil(requireActivity())
        managePermissions =
            ManagePermissions(requireActivity(), list.toList(), permissionsRequestCode)

        binding.txtAddFriend.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.txtMyFriend.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent_white_hex_10
            )
        )
        binding.txtInviteFriend.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent_white_hex_10
            )
        )
        showAddFriendFragment()
    }

    private fun setOnClickListeners() {
        binding.txtAddFriend.setOnClickListener(this)
        binding.txtMyFriend.setOnClickListener(this)
        binding.txtInviteFriend.setOnClickListener(this)
        binding.ivFriendRequest.setOnClickListener(this)
        binding.ivIcon.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.txt_add_friend -> {

                binding.txtAddFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.txtMyFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtInviteFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                showAddFriendFragment()
            }
            R.id.txt_my_friend -> {
                binding.txtAddFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtMyFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.txtInviteFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                showMyFriendFragment()
            }
            R.id.txt_invite_friend -> {
                binding.txtAddFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtMyFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtInviteFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                showContactFragment()
            }
            R.id.iv_friend_request -> {
                binding.txtAddFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtMyFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                binding.txtInviteFriend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent_white_hex_10
                    )
                )
                showFriendRequestFragment()
            }
            R.id.iv_icon -> {}
        }
    }

    private fun showContactFragment() {
        val bundle = Bundle()
        val frg = ContactFragmentNew()
        bundle.putBoolean("isSharing", true)
        frg.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.invite_contact_frg, frg, "invite_contact_frg").commit()
    }

    private fun showAddFriendFragment() {

        isShowInviteFrag = false
        val bundle = Bundle()
        val frg = AddFriendFragment()
        frg.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.invite_contact_frg, frg, "add_friend_frg").commit()
    }

    override fun onResume() {
        super.onResume()

        val bundle = this.arguments
        if (bundle?.getString("from") != null && bundle.getString("from") == "notification") {
            binding.txtAddFriend.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent_white_hex_10
                )
            )
            binding.txtMyFriend.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent_white_hex_10
                )
            )
            binding.txtInviteFriend.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent_white_hex_10
                )
            )
            showFriendRequestFragment()
        } else {
            if (isShowInviteFrag) {

            }

        }
    }

    private fun showMyFriendFragment() {
        val bundle = Bundle()
        val frg = MyFriendFragment()
        frg.arguments = bundle

        childFragmentManager.beginTransaction()
            .replace(R.id.invite_contact_frg, frg, "my_friend_frg").commit()
    }

    private fun showFriendRequestFragment() {
        val bundle = Bundle()
        val frg = FriendRequestFragment()
        frg.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.invite_contact_frg, frg, "friend_request_frg").commit()
    }

    override fun getViewModel() = FriendsFragmentViewModel::class.java

    override fun getFragmentRepository() = FriendsRepository(MyApi.getInstance())

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFriendsBinding.inflate(layoutInflater, container, false)
}