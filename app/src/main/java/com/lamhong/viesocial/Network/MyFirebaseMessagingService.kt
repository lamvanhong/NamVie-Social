package com.lamhong.viesocial.Network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lamhong.viesocial.IncomingInvitationActivity
import com.lamhong.viesocial.MainActivity
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Utilities.Constants
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.lamhong.viesocial.R
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(@NonNull s: String) {
        super.onNewToken(s)
        Log.d("TAG", "Token " + s)
        sendFCMTokenToDatabase(s)
        token = s
    }

    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("TAG", "RECEIVED")

        //Video Call
        val intent = Intent(applicationContext, IncomingInvitationActivity ::class.java )
        val user : User = User()
        val type = remoteMessage.data.get(Constants.REMOTE_MSG_TYPE)
        if(type!=null){
            if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                user.setUid(remoteMessage.data.get(Constants.KEY_UID)!!)
                user.setEmail(remoteMessage.data.get(Constants.KEY_EMAIL)!!)
                user.setName(remoteMessage.data.get(Constants.KEY_FULLNAME)!!)
                user.setAvatar(remoteMessage.data.get(Constants.KEY_AVATAR)!!)
                user.setToken(remoteMessage.data.get(Constants.KEY_FCM_TOKEN)!!)

                intent.putExtra("UserInfor", user)
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_TYPE))
                intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_ROOM))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                val tmpIntent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                tmpIntent.putExtra(
                    Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data.get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(tmpIntent)
            }
        }else {

            var meToken : String?= null

            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if(it.isComplete){
                    val fbToken = it.result.toString()
                    // DO your thing with your firebase token
                    meToken = fbToken
                }
            }

            //if (remoteMessage.data["token"].equals(meToken)) {
                //Notification
                val notiIntent = Intent(this, MainActivity::class.java)
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notificationID = Random.nextInt()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(notificationManager)
                }

                notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(this, 0, notiIntent, FLAG_ONE_SHOT)
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(remoteMessage.data["title"])
                    .setContentText(remoteMessage.data["message"])
                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()
                notificationManager.notify(notificationID, notification)
            //}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    var meUid : String? = FirebaseAuth.getInstance().uid
    private fun sendFCMTokenToDatabase(token : String){
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .child(meUid.toString())
            .child(Constants.KEY_FCM_TOKEN)
            .setValue(token)
    }

    companion object{
        var sharedPref :SharedPreferences?=null

        var token: String?
        get() {
            return sharedPref?.getString("token","")
        }
        set(value){
            sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }

}