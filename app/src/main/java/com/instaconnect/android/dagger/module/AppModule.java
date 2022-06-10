package com.instaconnect.android.dagger.module;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.datatransport.runtime.dagger.Module;
import com.google.android.datatransport.runtime.dagger.Provides;
import com.instaconnect.android.fileHelper.DataManager;
import com.instaconnect.android.fileHelper.FileHelper;
import com.instaconnect.android.fileHelper.FileUtils;
import com.instaconnect.android.network.ApiEndPoint;
import com.instaconnect.android.utils.AppFileHelper;
import com.instaconnect.android.utils.ShareUtil;
import com.instaconnect.android.utils.dialog_helper.SchedulerProvider;
import com.instaconnect.android.utils.rx.AppSchedulerProvider;

import javax.inject.Singleton;

@Module
public abstract  class AppModule {

    @Provides
    @Singleton
    static Context provideContext(Application application) {
        return application;
    }


    @Provides
    @Singleton
    static SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    /*@Provides
    @Singleton
    static SmackManager provideSmack(Context context, DataManager dataManager, SchedulerProvider schedulerProvider) {
        return new AppSmackManager(context,dataManager,schedulerProvider,AppConstants.smackServiceName, AppConstants.smackServerHost,
                AppConstants.smackServerPort);
    }

    @Provides
    @Singleton
    static ContactManger provideContactManger(DataManager dataManager,SmackManager smackManager,ContactUtil contactUtil) {
        return new ContactManger(dataManager,smackManager,contactUtil);
    }

    @Provides
    @Singleton
    static ContactUtil provideContactUtil(Context context) {
        return new ContactUtil(context);
    }

    @Provides
    @Singleton
    static DataManager provideDataManager(Context context, DbHelper dbHelper, PreferencesHelper preferencesHelper, ApiHelper apiHelper, FileHelper fileHelper) {
        return new AppDataManager(context,dbHelper,preferencesHelper, apiHelper,fileHelper);
    }

    @Provides
    @DatabaseInfo
    static String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @Singleton
    static AppDatabase provideAppDatabase(@DatabaseInfo String dbName, Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, dbName)
                .fallbackToDestructiveMigration().build();
    }

    @Provides
    @Singleton
    static DbHelper provideDbHelper(AppDatabase database,PreferencesHelper helper) {
        return new AppDbHelper(database,helper);
    }

    @Provides
    @PreferenceInfo
    static String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    static PreferencesHelper providePreferencesHelper(Context context,@PreferenceInfo String prefName) {
        return new AppPreferencesHelper(context,prefName);
    }

    @Provides
    @Singleton
    static FileHelper provideFileDatabase(Context context) {
        return new AppFileHelper(context);
    }

    @Provides
    @Singleton
    static RestClient provideRestClient(){
        return new RestClient();
    }

    @Provides
    @Singleton
    static ApiHelper provideApiHelper(RestClient restClient, Context context){
        return (ApiHelper) restClient.configRestClient(context,ApiHelper.class, ApiEndPoint.SERVER_BASE_URL);
    }

    @Provides
    @Singleton
    static DownloadUtil provideDownloadManager() {
        return new DownloadUtil();
    }

    @Provides
    @Singleton
    static NetworkUtil provideNetworkUtil(Context context) {
        return new NetworkUtil(context);
    }

    @Provides
    @Singleton
    static NotificationUtils provideNotificationUtils(Context context) {
        return new NotificationUtils(context);
    }

    @Provides
    @Singleton
    static ValidationUtil provideValidationUtil() {
        return new ValidationUtil();
    }*/

    @Provides
    @Singleton
    static FileUtils provideFileUtils(Context context) {
        return new FileUtils(context);
    }

    @Provides
    @Singleton
    static ShareUtil provideShareUtil(Context context) {
        return new ShareUtil(context);
    }

    @Provides
    @Singleton
    static HttpProxyCacheServer httpProxyCacheServer (Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(500000000)// 500 Mb for cache
                .build();
    }

}