package com.instaconnect.android.ui.contact

import android.provider.ContactsContract

interface SelectContactCallback {
    fun onContactSelected(contactsList: List<ContactsContract.Contacts?>?)
}