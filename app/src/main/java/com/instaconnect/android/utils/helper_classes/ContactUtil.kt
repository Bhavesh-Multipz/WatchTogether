package com.instaconnect.android.utils.helper_classes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences.Companion.getPreferences
import com.instaconnect.android.utils.RegexUtil.Companion.extractDigits
import io.reactivex.Observable
import java.util.concurrent.Callable

class ContactUtil(private val context: Context) {

    fun GetContacts(): Observable<ArrayList<Contacts>> {
        return Observable.fromCallable(Callable {
            val contacts = ArrayList<Contacts>()
            try {
                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                )
                val cursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC"
                )
                try {
                    val normalizedNumbersAlreadyFound = HashSet<String>()
                    val indexOfNormalizedNumber =
                        cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
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
                            contact.phone = extractDigits(displayNumber)
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
                cursor!!.close()
                return@Callable contacts
            } catch (e: Exception) {
                e.printStackTrace()
                return@Callable contacts
            }
        })
    }

    companion object {
        fun addContactLabel(contactsList: List<Contacts>): List<Contacts> {
            val filteredList: MutableList<Contacts> = ArrayList()
            println("Contact List...: " + contactsList.size)
            var letter = "#"
            try {
                letter = contactsList[0].name[0].toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //set initial header
            val contact = Contacts()
            /*contact.setHeader(letter);
        filteredList.add(contact);*/
            // montu

            //find rest of the header
            for (contacts in contactsList) {
                if (contactsList.size > 0) {
                    try {
                        /* if (!String.valueOf(contacts.getName().charAt(0)).equalsIgnoreCase(letter)) {
                        Contacts contactLabel = new Contacts();
                        letter = String.valueOf(contacts.getName().charAt(0));
                        contactLabel.setHeader(letter);
                        filteredList.add(contactLabel);
                        filteredList.add(contacts);
                    } else {*/
                        // montu
                        filteredList.add(contacts)
                        /*}*/
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return filteredList
        }

        fun addAsContactConfirmed(context: Context, person: Contacts) {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = ContactsContract.Contacts.CONTENT_TYPE
            intent.putExtra(ContactsContract.Intents.Insert.NAME, person.name)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.phone)
            context.startActivity(intent)
        }

        fun isMe(from: String, context: Context?): Boolean {
            return from.contains(getPreferences(context!!, Constants.PREF_USER_ID)!!)
        }

        fun getContactName(phoneNumber: String?, context: Context): String {
            val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            var contactName = ""
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0)
                }
                cursor.close()
            }
            return contactName
        }

        fun getContactImageUri(phoneNumber: String?, context: Context): String {
            val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
            val projection = arrayOf(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)
            var contactImageUri = ""
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactImageUri = cursor.getString(0)
                }
                cursor.close()
            }
            return contactImageUri
        }
    }
}