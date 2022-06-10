package com.instaconnect.android.ui.fragment.worldwide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.instaconnect.android.ui.login.LoginRepository
import com.instaconnect.android.ui.login.LoginViewModel


class WorldwideViewModelFactory(private val repository: WorldWideRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorldWideViewModel(repository) as T
    }
}