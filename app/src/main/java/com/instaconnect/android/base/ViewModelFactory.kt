package com.instaconnect.android.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.instaconnect.android.ui.fragment.explore.ExploreRepository
import com.instaconnect.android.ui.fragment.explore.ExploreViewModel
import com.instaconnect.android.ui.friends.contact_fragment.ContactFragmentViewModel
import com.instaconnect.android.ui.friends.contact_fragment.ContactRepository

class ViewModelFactory(private val repository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> ExploreViewModel(repository as ExploreRepository) as T
            modelClass.isAssignableFrom(ContactFragmentViewModel::class.java) -> ContactFragmentViewModel(repository as ContactRepository) as T

            else -> throw IllegalAccessException("ViewModel Class Not Found!")
        }
    }

}