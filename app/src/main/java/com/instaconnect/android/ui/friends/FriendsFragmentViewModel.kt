package com.instaconnect.android.ui.friends

import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.utils.RegexUtil
import com.instaconnect.android.utils.helper_classes.ContactUtil
import gun0912.tedimagepicker.util.ToastUtil.context
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class FriendsFragmentViewModel(private val repository: FriendsRepository) : BaseViewModel(repository) {

    private val contact = MutableLiveData<List<Contacts>>()
    var rawContact = ArrayList<Contacts>()

    private val _getWatchlistResponse: MutableLiveData<Resource<Post>> = MutableLiveData()
    val getWatchlistResponse: LiveData<Resource<Post>>
        get() = _getWatchlistResponse

    suspend fun getWatchList(
        userId: String,
        category: String,
        page: String,
        country: String,
        radius: String,
        lat: String,
        lng: String
    ) = viewModelScope.launch {
        _getWatchlistResponse.value = Resource.Loading
        _getWatchlistResponse.value = repository.getWatchList(userId, category, page, country, radius, lat, lng)
    }

    fun getRawContact(): List<Contacts> {
        return rawContact
    }

    fun getContact(): MutableLiveData<List<Contacts>> {
        return contact
    }

    fun load() {
        val loadNewContacts = loadNewContacts()
        refresh(loadNewContacts)
    }


    fun refresh(contactsList: List<Contacts>) {
        if (contactsList.isNotEmpty()) {
            contact.value = ContactUtil.addContactLabel(contactsList)
            rawContact.clear()
            rawContact.addAll(contact.value!!)
        }
    }

    fun filterContacts(filter: String) {
        val filterList: MutableList<Contacts> = java.util.ArrayList()
        for (contacts in rawContact) {
            if (contacts.name.lowercase(Locale.getDefault()).contains(filter.lowercase(Locale.getDefault()))
                || contacts.name.lowercase(Locale.getDefault()).contains(
                    filter.lowercase(Locale.getDefault())
                ))
                filterList.add(contacts)
        }
        contact.value = filterList
    }

    private fun loadNewContacts(): ArrayList<Contacts> {

        var contacts = ArrayList<Contacts>()

        return try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
            )
            val cursor: Cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC"
            )!!
            try {
                val normalizedNumbersAlreadyFound = HashSet<String>()
                val indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                val indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val normalizedNumber = cursor.getString(indexOfNormalizedNumber)
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        //haven't seen this number yet: do something with this contact!
                        val displayName = cursor.getString(indexOfDisplayName)
                        val displayNumber = cursor.getString(indexOfDisplayNumber)
                        val contact = Contacts()
                        contact.name = displayName
                        contact.phone = RegexUtil.extractDigits(displayNumber)
                        contact.isStatus = false
                        contacts.add(contact)
                    } else {
                        //don't do anything with this contact because we've already found this number
                        Log.d("GetContacts", "duplicate contact $normalizedNumber")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            cursor.close()
            contacts
        } catch (e: Exception) {
            e.printStackTrace()
            contacts
        }
    }


}