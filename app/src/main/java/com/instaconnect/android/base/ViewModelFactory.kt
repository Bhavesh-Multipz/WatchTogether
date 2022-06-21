package com.instaconnect.android.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.instaconnect.android.ui.fragment.add_post.CaptureFragmentRepository
import com.instaconnect.android.ui.fragment.add_post.CaptureFragmentViewModel
import com.instaconnect.android.ui.fragment.explore.ExploreRepository
import com.instaconnect.android.ui.fragment.explore.ExploreViewModel
import com.instaconnect.android.ui.friends.FriendsFragmentViewModel
import com.instaconnect.android.ui.friends.FriendsRepository
import com.instaconnect.android.ui.friends.add_friend_fragment.AddFriendFragmentViewModel
import com.instaconnect.android.ui.friends.add_friend_fragment.AddFriendRepository
import com.instaconnect.android.ui.friends.contact_fragment.ContactFragmentViewModel
import com.instaconnect.android.ui.friends.contact_fragment.ContactRepository
import com.instaconnect.android.ui.friends.friend_request_fragment.FriendsRequestRepository
import com.instaconnect.android.ui.friends.friend_request_fragment.FriendsRequestViewModel
import com.instaconnect.android.ui.friends.my_friends.MyFriendsRepository
import com.instaconnect.android.ui.friends.my_friends.MyFriendsViewModel

class ViewModelFactory(private val repository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> ExploreViewModel(repository as ExploreRepository) as T
            modelClass.isAssignableFrom(ContactFragmentViewModel::class.java) -> ContactFragmentViewModel(repository as ContactRepository) as T
            modelClass.isAssignableFrom(MyFriendsViewModel::class.java) -> MyFriendsViewModel(repository as MyFriendsRepository) as T
            modelClass.isAssignableFrom(FriendsFragmentViewModel::class.java) -> FriendsFragmentViewModel(repository as FriendsRepository) as T
            modelClass.isAssignableFrom(FriendsRequestViewModel::class.java) -> FriendsRequestViewModel(repository as FriendsRequestRepository) as T
            modelClass.isAssignableFrom(AddFriendFragmentViewModel::class.java) -> AddFriendFragmentViewModel(repository as AddFriendRepository) as T
            modelClass.isAssignableFrom(CaptureFragmentViewModel::class.java) -> CaptureFragmentViewModel(repository as CaptureFragmentRepository) as T

            else -> throw IllegalAccessException("ViewModel Class Not Found!")
        }
    }

}