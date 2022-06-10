package com.instaconnect.android.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(
    private val repository: BaseRepository
) : ViewModel() {

        var profilePic = MutableLiveData("imagePath")
        var userName = MutableLiveData("userName")

   /* val userImage = MutableLiveData<String>()
    var image: String by Delegates.observable("") { _, _, newValue ->
        userImage.postValue(newValue)
    }*/

}