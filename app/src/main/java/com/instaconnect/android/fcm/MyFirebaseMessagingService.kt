package com.instaconnect.android.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.instaconnect.android.R
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Prefrences.Companion.savePreferencesString
import org.json.JSONObject
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        savePreferencesString(this, Constants.PREF_DEVICE_TOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "DataPayload1: $remoteMessage")
        Log.d(TAG, "DataPayload2: " + remoteMessage.getFrom().toString() + "")
        Log.d(TAG, "DataPayload3: $remoteMessage")
        System.out.println("In Messsage Received" + remoteMessage.getData())
        //
        // Check if message contains a data payload. /// before implementing new design (only for chat)
        /*if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String msg = remoteMessage.getData().get("data") + "";
            System.out.println("Handle Message data: " + msg);

            if(remoteMessage.getData().get("notify_type") != null && remoteMessage.getData().get("notify_type").equals("send_request")){

                handleDataMessage(remoteMessage.getData().toString());
            } else {
                try {
                    JSONObject obj = new JSONObject(msg);
                    if (obj.has("isCall")) {
                        System.out.println("Handle Message  in isCall");
                        try {
                            ActivityManager am = (ActivityManager) getApplicationContext()
                                    .getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningTaskInfo> alltasks = am.getRunningTasks(1);

                            boolean shouldBeCalled = true;
                            for (ActivityManager.RunningTaskInfo aTask : alltasks) {


                                if (aTask.topActivity.getClassName().equals("instaconnect.app.ui.calling.IncomingCallActivity")) {

                                    System.out.println("INCOMING ACTIVITY IS LIVE");
                                    shouldBeCalled = false;

                                    CallingUtil.disconnectCalling(obj.getString("fromPhone"), null, false, "", "", false, "2", ((AppPreferencesHelper) preferencesHelper), getBaseContext());
                                } else {
                                    System.out.println("INCOMING ACTIVITY IS NOT LIVE");

                                }

                                if (aTask.topActivity.getClassName().equals("instaconnect.app.ui.call.videocall.VideoActivity")) {
                                    System.out.println("VIDEO ACTIVITY IS LIVE");
                                    shouldBeCalled = false;
                                    CallingUtil.disconnectCalling(obj.getString("fromPhone"), null, false, "", "", false, "2", ((AppPreferencesHelper) preferencesHelper), getBaseContext());
                                } else {
                                    System.out.println("VIDEO ACTIVITY IS NOT LIVE");

                                }
                            }
                            if (shouldBeCalled) {
                                handleCallDataMessate(msg);
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    } else if (obj.has("isDisconnected")) {
                        if (obj.has("isAccepted")) {
                            //if(!obj.getBoolean("isAccepted")) {
                            handleCalleRespondedDataMessage(msg, obj.getBoolean("isAccepted"), obj.getString("disconnection_msg"));
                            //}
                        } else if (obj.has("isInitiator")) {
                            handleCallerRespondedDataMessage(msg, obj.getBoolean("isInitiator"));
                        }
                    } else {
                        System.out.println("Handle Message  in notIsCall");
                        handleDataMessage(remoteMessage.getData().toString());
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Exception one: " + e.getMessage());
                }
            }

        }*/

        // new implementation with new design
        val data = remoteMessage.getData()
        val obj = JSONObject(remoteMessage.getData() as Map<*, *>?)
        var postObject = JSONObject()
        if (data.get("notify_type").equals("send_invitation") || data.get("notify_type")
                .equals("post_liked")
        ) {
            try {
                postObject = JSONObject(java.lang.String.valueOf(data.get("post_detail_arr")))
            } catch (t: Throwable) {
                Log.e(
                    "My App",
                    "Could not parse malformed JSON: \"" + data.get("post_detail_arr")
                        .toString() + "\""
                )
            }
        }
        try {
            sendNotification(
                Objects.requireNonNull(data.get("title")).toString(),
                Objects.requireNonNull(data.get("body")).toString(), data,
                Objects.requireNonNull(data.get("notify_type")).toString(), postObject
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        data: Map<String, String>,
        type: String,
        postObject: JSONObject
    ) {
        val intent: Intent
        intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("from", "notification")
        intent.putExtra("type", type)
        intent.putExtra("title", data.get("title").toString())
        intent.putExtra("noti_body", data.get("body").toString())
        if (type == "send_invitation" || type == "post_liked") {
            intent.putExtra("post_data", postObject.toString())
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = applicationContext.packageName
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)

        //notificationBuilder.setUsesChronometer(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_top_friend_logo_glass)
            .setColor(ResourcesCompat.getColor(resources, R.color.white, null))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(defaultSoundUri)
//            .setStyle(Notification.BigTextStyle().bigText(messageBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, Constants.APP_NAME + "Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setDescription("Notifications for " + Constants.APP_NAME)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_COUNT, notificationBuilder.build())
        NOTIFICATION_COUNT = NOTIFICATION_COUNT + 1
    }


    private fun sendRegistrationToServer(token: String) {
        Log.e(TAG, "sendRegistrationToServer: $token")
    }

    /*private fun handleCallerRespondedDataMessage(message: String, isInitiator: Boolean) {
        var message = message
        message = message.replace("\\{\\}".toRegex(), "\"\"")
        println("Handle Message$message")
        val callnotificationBundle: CallNotificationBundle =
            CommonUtil.getInstance().fromJson(message, CallNotificationBundle::class.java)
        //System.out.println("Handle Message Call: " + callnotificationBundle.getRoomName());
        val thread = Thread { broadcastInitiatorDisconnectionNotification(isInitiator) }
        thread.start()
    }*/

    /*private fun handleCalleRespondedDataMessage(
        message: String,
        isAccepted: Boolean,
        disconnection_msg: String
    ) {
        var message = message
        message = message.replace("\\{\\}".toRegex(), "\"\"")
        println("Handle Message$message")
        val callnotificationBundle: CallNotificationBundle =
            CommonUtil.getInstance().fromJson(message, CallNotificationBundle::class.java)
        System.out.println("Handle Message Call: " + callnotificationBundle.getRoomName())
        val thread = Thread {
            broadcastCalleRespondedNotification(
                isAccepted,
                callnotificationBundle.getFromPhone(),
                disconnection_msg
            )
        }
        thread.start()
    }*/

    /*private fun handleCallDataMessate(message: String) {
        var message = message
        message = message.replace("\\{\\}".toRegex(), "\"\"")
        println("Handle Message$message")
        val callnotificationBundle: CallNotificationBundle =
            CommonUtil.getInstance().fromJson(message, CallNotificationBundle::class.java)
        System.out.println("Handle Message Call: " + callnotificationBundle.getRoomName())
        //RxBus.getInstance().publish(BusMessage.MESSAGE_NOTIFICATION.name(), callnotificationBundle);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
        //CallingUtil.callIncoming(callnotificationBundle,getApplicationContext());
        val alarmIntent = Intent()
        alarmIntent.setClass(baseContext, IncomingCallActivity::class.java)
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        alarmIntent.putExtra("noti_data", callnotificationBundle)
        startActivity(alarmIntent)

        // }
//        });
//        thread.start();
    }

    private fun handleDataMessage(message: String) {
        var message = message
        if (!notificationUtils.isAppIsInBackground()) {
            // app is in foreground, broadcast the push message
            message = message.replace("\\{\\}".toRegex(), "\"\"")
            val notificationBundle: NotificationBundle =
                CommonUtil.getInstance().fromJson(message, NotificationBundle::class.java)
            RxBus.getInstance().publish(BusMessage.MESSAGE_NOTIFICATION.name(), notificationBundle)
        } else {
            try {
                message = message.replace("\\{\\}".toRegex(), "\"\"")
                val notificationBundle: NotificationBundle =
                    CommonUtil.getInstance().fromJson(message, NotificationBundle::class.java)
                if (notificationBundle.getData().getType()
                        .equalsIgnoreCase(Message.Type.chat.name())
                ) {
                    getUser(notificationBundle)
                } else {
                    findRoomById(notificationBundle)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    fun broadcastCalleRespondedNotification(
        isAccepted: Boolean,
        calle: String?,
        disconnection_msg: String?
    ) {
        val intent = Intent()
        intent.putExtra("message", "Calle Disconnected")
        intent.putExtra("isAccepted", isAccepted)
        intent.putExtra("calle", calle)
        intent.putExtra("disconnectionMsg", disconnection_msg)
        intent.setAction("instaconnect.app.calle.response")
        sendBroadcast(intent)
    }

    fun broadcastInitiatorDisconnectionNotification(isInitiator: Boolean) {
        val intent = Intent()
        intent.putExtra("message", "Initiator Disconnected")
        intent.putExtra("isInitiator", isInitiator)
        intent.setAction("instaconnect.app.initiator.response")
        sendBroadcast(intent)
    }

    /*private fun findRoomById(notificationBundle: NotificationBundle) {
        dbHelper.getChatRoomById(
            notificationBundle.getData().getFrom().substring(
                0,
                notificationBundle.getData().getFrom().indexOf("@")
            )
        ).subscribeOn(Schedulers.io()).subscribe(object : Consumer<ChatRooms?>() {
            @Throws(Exception::class)
            fun accept(chatRooms: ChatRooms) {
                val contacts = Contacts()
                contacts.setName(chatRooms.getRoomName())
                sendIntent(notificationBundle, contacts)
            }
        })
    }

    fun getUser(notificationBundle: NotificationBundle) {
        dbHelper.findContactByNumber(
            notificationBundle.getData().getFrom().substring(
                0,
                notificationBundle.getData().getFrom().indexOf("@")
            )
        ).subscribeOn(Schedulers.io())
            .subscribe(object : Consumer<Contacts?>() {
                @Throws(Exception::class)
                fun accept(contacts: Contacts?) {
                    sendIntent(notificationBundle, contacts)
                }
            })
    }

    private fun sendIntent(notificationBundle: NotificationBundle, contacts: Contacts?) {
        val from: String = if (contacts != null && !contacts.getName()
                .isEmpty()
        ) contacts.getName() else notificationBundle.getData().getFrom().substring(
            0,
            notificationBundle.getData().getFrom().indexOf("@")
        )
        val message: String = if (notificationBundle.getData().getMessage().getDatatype()
                .equalsIgnoreCase(TEXT_TYPE)
        ) notificationBundle.getData().getBody() else notificationBundle.getData().getMessage()
            .getDatatype()
        val baseIntent: BaseIntent =
            BaseIntent(this, TermsActivity::class.java).setModel(notificationBundle)
        showNotificationMessage(this, from, message, baseIntent)
    }
*/
    /**
     * Showing notification with text only
     */
    /*private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        intent: Intent
    ) {
        notificationUtils = NotificationUtils(context)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        notificationUtils.showNotificationMessage(title, message, intent)
    }*/

    /**
     * Showing notification with text and image
     */
    /*private fun showNotificationMessageWithBigImage(
        context: Context,
        title: String,
        message: String,
        timeStamp: String,
        intent: Intent,
        imageUrl: String
    ) {
        notificationUtils = NotificationUtils(context)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl)
    }*/

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        var NOTIFICATION_COUNT = 1
    }
}