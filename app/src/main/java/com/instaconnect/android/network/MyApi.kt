package com.instaconnect.android.network

import com.google.gson.GsonBuilder
import com.instaconnect.android.data.model.BlockUser
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.data.model.PublicPost
import com.instaconnect.android.data.model.ReportPost
import com.instaconnect.android.ui.fragment.add_post.WebLinkResponse
import com.instaconnect.android.ui.fragment.worldwide.InvitePeopleForWatchTogether
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.ui.friends.my_friends.UnfriendResponse
import com.instaconnect.android.ui.login.SendOtp
import com.instaconnect.android.ui.profile.ProfileResponse
import com.instaconnect.android.ui.trending_websites.models.TrendingWebsiteResponseModel
import com.instaconnect.android.ui.watch_together_room.PostReaction
import com.instaconnect.android.ui.youtube_webview.YoutubeVideoDetails
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {
    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_SEND_SOCIAL_ID)
    suspend fun sendSocialId(
        @Field("phone") phone: String, @Field("code") code: String,
        @Field("device_token") device_token: String, @Field("device_type") device_type: String,
        @Field("username") userName: String, @Field("user_profile_url") userProfileUrl: String,
    ): SendOtp

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_GET_MY_FRIEND)
    suspend fun getMyFriendList(
        @Field("user_id") user_id: String?,
        @Field("search") search: String?,
        @Field("page") page: Int,
    ): FriendListModel

    @Multipart
    @POST(ApiEndPoint.ENDPOINT_USER_PROFILE)
    suspend fun userProfile(
        @Part files: MultipartBody.Part,
        @QueryMap params: Map<String, String>,
    ): ProfileResponse

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_ADD_FRIEND_LIST)
    suspend fun getAddFriendList(
        @Field("user_id") user_id: String?,
        @Field("search") search: String?,
        @Field("page") page: Int,
    ): FriendListModel

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_WATCH_LIST)
    suspend fun getWatchList(
        @Field("user_id") user_id: String?,
        @Field("category") category: String?,
        @Field("page") page: String?,
        @Field("country") country: String?,
        @Field("radius") radius: String?,
        @Field("lat") lat: String?,
        @Field("lng") lng: String?,
    ): Post

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_INVITE_PEOPLE_TO_WATCH_VIDEO)
    suspend fun invitePeopleToWatchVideo(
        @Field("user_id") user_id: String?,
        @Field("other_user_id") other_user_id: String?,
        @Field("post_id") post_id: String?,
    ): InvitePeopleForWatchTogether

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_ADD_POST_REACTION)
    suspend fun addPostReaction(
        @Field("postId") postId: String?,
        @Field("reaction") reaction: String?,
        @Field("userId") userId: String?,
    ): PostReaction

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_REPORT_POST)
    suspend fun reportPost(
        @Field("post_id") post_id: String?,
        @Field("reason") reason: String?,
        @Field("user_id") userId: String?,
    ): ReportPost

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_BLOCK_USER)
    suspend fun blockUser(
        @Field("block_user_id") block_user_id: String?,
        @Field("status") status: String?,
        @Field("user_id") userId: String?,
    ): BlockUser

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_FRIEND_REQUEST_RESPONSE)
    suspend fun getFriendRequestResponse(
        @Field("user_id") user_id: String?,
        @Field("other_user_id") other_user_is: String?,
        @Field("response_type") response_type: String?,
    ): FriendListModel

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_FRIEND_REQUEST)
    suspend fun getFriendRequest(
        @Field("user_id") user_id: String?,
        @Field("search") search: String?,
        @Field("page") page: Int,
    ): FriendListModel

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_SEND_FRIEND_REQUEST)
    suspend fun sendFriendRequest(
        @Field("user_id") user_id: String?,
        @Field("other_user_id") other_user_is: String?,
    ): FriendListModel

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_UNFRIEND_USER)
    suspend fun makeUnfriendUser(
        @Field("user_id") user_id: String?,
        @Field("unfriend_user_id") unfriend_user_id: String?,
    ): UnfriendResponse

    @GET(ApiEndPoint.ENDPOINT_GET_TRENDING_WEBSITES)
    suspend fun getTrendingWebsites(): TrendingWebsiteResponseModel

    @FormUrlEncoded
    @POST(ApiEndPoint.ENDPOINT_GET_WEB_LINKS)
    suspend fun getWebLinks(@Field("device_type") device_type: String?):
            WebLinkResponse

    @GET("embed")
    suspend fun getYoutubeVideoDetails(@Query("url") url: String?): YoutubeVideoDetails

    @Multipart
    @POST(ApiEndPoint.ENDPOINT_UPLOADS_PUBLIC_POST)
    suspend fun publicPostWatchTogether(
        @Part("user_id") user_id: RequestBody?,
        @Part("media") media: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lng") lng: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("datatype") datatype: RequestBody?,
        @Part("hyperlink") hyperlink: RequestBody?,
        @Part("mediaType") mediaType: RequestBody?,
        @Part("media_ratio") mediaRatio: RequestBody?,
        @Part("youTubeVideoId") youTubeVideoId: RequestBody?,
        @Part("is_video_link") isVideoLink: RequestBody?,
        @Part("group_password") groupPassword: RequestBody?,
        @Part("group_name") groupName: RequestBody?,
        @Part("uniquecode") uniquecode: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): PublicPost

    @Multipart
    @POST(ApiEndPoint.ENDPOINT_UPLOADS_PUBLIC_POST)
    suspend fun publicPostYoutube(
        @Part("user_id") user_id: RequestBody?,
        @Part("media") media: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part("lat") lat: RequestBody?,
        @Part("lng") lng: RequestBody?,
        @Part("caption") caption: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("datatype") datatype: RequestBody?,
        @Part("hyperlink") hyperlink: RequestBody?,
        @Part("mediaType") mediaType: RequestBody?,
        @Part("media_ratio") mediaRatio: RequestBody?,
        @Part("youTubeVideoId") youTubeVideoId: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): PublicPost

    companion object {

        var myApi: MyApi? = null

        fun getInstanceToken(authToken: String): MyApi {
            val client = OkHttpClient.Builder().apply {
                connectTimeout(100, TimeUnit.SECONDS)
                readTimeout(100, TimeUnit.SECONDS)
                addInterceptor(AuthenticationInterceptor(authToken))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }.build()

            val gson = GsonBuilder()
                .setLenient()
                .create()


            val retrofit = Retrofit.Builder()
            retrofit.baseUrl(ApiEndPoint.SERVER_BASE_URL)
            retrofit.client(client)
            retrofit.addConverterFactory(GsonConverterFactory.create(gson))
            retrofit.addConverterFactory(ScalarsConverterFactory.create())
            myApi = retrofit.build().create(MyApi::class.java)

            return myApi!!
        }

        fun getInstance(): MyApi {

            val client = OkHttpClient.Builder().apply {
                connectTimeout(100, TimeUnit.SECONDS)
                readTimeout(100, TimeUnit.SECONDS)
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }.build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (myApi == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(ApiEndPoint.SERVER_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                myApi = retrofit.create(MyApi::class.java)
            }
            return myApi!!
        }
    }

}