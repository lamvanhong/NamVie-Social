package com.lamhong.viesocial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Network.ApiClient
import com.lamhong.viesocial.Network.ApiService
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_incoming_invitation.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URL

class IncomingInvitationActivity : AppCompatActivity() {

    var sender : User = User()
    var meetingType : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_invitation)

        meetingType = intent.getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE)
        sender = intent.getSerializableExtra("UserInfor") as User

        if(meetingType!=null){
            if(meetingType.equals("video")){
                MeetingTypeInComingimg.setImageResource(R.drawable.ic_baseline_videocam_24)
            }
        }

        Picasso.get().load(sender!!.getAvatar()).placeholder(R.drawable.duongtu1)
            .into(AvartarIncomingimg)
        userNameIncomingtxt.text = sender!!.getName()

        acceptInvitationimg.setOnClickListener(){
            sendInvitationRespone(
                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                sender.getToken()
            )
        }
        rejectInvitationimg.setOnClickListener(){
            sendInvitationRespone(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                sender.getToken()
            )
        }

    }

    private fun sendInvitationRespone(type: String, receverToken: String){
        try {

            val tokens:JSONArray = JSONArray()
            tokens.put(receverToken)

            val body:JSONObject = JSONObject()
            val data:JSONObject = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type)

            body.put(Constants.REMOTE_MSG_DATA,data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(),type)

        }catch (e: Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody:String, type: String){

        val v = ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody
        )
        v.enqueue(object : Callback<String> {
            override fun onResponse(@NonNull call: Call<String>, @NonNull response: Response<String>) {
                if(response.isSuccessful){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        Toast.makeText(this@IncomingInvitationActivity,"Invitation Accepted",Toast.LENGTH_SHORT).show()

                        try {

                            val serverURL: URL = URL("https://meet.jit.si")
                            val conferenceOptions : JitsiMeetConferenceOptions = JitsiMeetConferenceOptions
                                .Builder()
                                .setServerURL(serverURL)
                                .setWelcomePageEnabled(false)
                                .setRoom(intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                                .build()
                            JitsiMeetActivity.launch(this@IncomingInvitationActivity,conferenceOptions)
                            finish()

                        }catch (e : Exception){
                            Toast.makeText(this@IncomingInvitationActivity,e.message,Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(this@IncomingInvitationActivity,"Invitation Rejected", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }else{
                    Toast.makeText(this@IncomingInvitationActivity,response.message(), Toast.LENGTH_SHORT).show()
                    finish()
                }

            }

            override fun onFailure(@NonNull call: Call<String>, @NonNull t: Throwable) {
                Toast.makeText(this@IncomingInvitationActivity,t.message, Toast.LENGTH_SHORT).show()
                finish()
            }

        })
    }
    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type: String  = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)!!
            if(type!=null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(this@IncomingInvitationActivity,"Invitation Cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            invitationResponseReceiver
        )
    }
}