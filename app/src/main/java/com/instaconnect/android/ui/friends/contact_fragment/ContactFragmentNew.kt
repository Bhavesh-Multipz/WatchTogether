package com.instaconnect.android.ui.friends.contact_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.instaconnect.android.base.BaseFragment
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.databinding.FragmentContactNewBinding
import com.instaconnect.android.fileHelper.DataManager
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.ui.friends.contact_fragment.adapter.ContactNewAdapter
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.PermissionUtil
import com.instaconnect.android.utils.Utils.visible
import com.instaconnect.android.utils.ViewUtil


class ContactFragmentNew : BaseFragment<ContactFragmentViewModel, FragmentContactNewBinding, ContactRepository>(), TextWatcher,
    SwipeRefreshLayout.OnRefreshListener {

    private var isSharing = false
    var contactAdapter: ContactNewAdapter? = null
    lateinit var permissionUtil: PermissionUtil
    lateinit var viewUtil: ViewUtil
    lateinit var dataManager: DataManager
    private val contactObserver: Observer<List<Contacts>> =
        Observer<List<Contacts>> { contactsArrayList -> // Update the UI
            binding.swipeRefresh.isRefreshing = false
            contactAdapter!!.clear()
            val resultList: MutableList<Contacts> = ArrayList<Contacts>()
            if (contactsArrayList != null) {
                for (element in contactsArrayList) {
                    if (!element.isStatus) resultList.add(element)
                }
            }

            if(resultList.isEmpty()){
                binding.relNoData.visible(true)
            } else {
                binding.relNoData.visible(false)
            }
            contactAdapter!!.data = resultList
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionUtil = PermissionUtil(requireActivity())
        viewUtil = ViewUtil(requireActivity())
        contactAdapter = ContactNewAdapter(requireContext(), viewUtil)

        binding.recyclerContacts.layoutManager = LinearLayoutManager(context)
        binding.recyclerContacts.adapter = contactAdapter

        if (permissionUtil.hasPermissionsGroup(Constants.appPermissionsForContacts)) {
            viewModel.load()
            getData()
        } else {
            permissionUtil.requestPermissionsGroup(
                Constants.appPermissionsForContacts,
                PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE
            )
        }

        setListeners()
    }

    private fun getData() {
        binding.swipeRefresh.isRefreshing = true
        val bundle = arguments
        if (bundle != null) {
            isSharing = requireArguments().getBoolean("isSharing")
        }
        viewModel.getContact().observe(viewLifecycleOwner, contactObserver)
    }

    private fun setListeners() {
        binding.inputSearch.addTextChangedListener(this)
        binding.swipeRefresh.setOnRefreshListener(this)
        binding.linearOk.setOnClickListener { v: View? ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            requireContext().startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.PERMISSIONS_STORAGE_CAMERA_AUDIO_GROUP_CODE && PermissionUtil.isPermissionGranted(
                grantResults)
        )
            viewModel.load()
        else
            viewUtil.showPermissionSnack()
    }

    override fun getViewModel() = ContactFragmentViewModel::class.java

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentContactNewBinding.inflate(layoutInflater, container, false)

    override fun getFragmentRepository() = ContactRepository(MyApi.getInstance())

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isEmpty())
            viewModel.refresh(viewModel.getRawContact())
        else
            viewModel.filterContacts(s.toString())
    }

    override fun onRefresh() {
    }
}