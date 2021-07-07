package com.lamhong.viesocial

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lamhong.viesocial.Models.User
import com.lamhong.viesocial.Network.ApiClient
import com.lamhong.viesocial.Network.ApiService
import com.lamhong.viesocial.Utilities.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_incoming_invitation.*
import kotlinx.android.synthetic.main.activity_outgoing_invitation.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URL
import java.util.*


class OutgoingInvitationActivity : AppCompatActivity(){

    var senderInfor :User ? = null
    var receiInfor: User ? = null
    var meetingType: String ? = null
    var meetingRoom: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outgoing_invitation)

        senderInfor = intent.getSerializableExtra("senderInfor") as User
        receiInfor = intent.getSerializableExtra("receiInfor") as User
        meetingType = intent.getStringExtra("type")

        Picasso.get().load(receiInfor!!.getAvatar()).placeholder(R.drawable.duongtu1)
            .into(avatarUserimg)
        userNametxt.text = receiInfor!!.getName()

        stopInvitationimg.setOnClickListener(View.OnClickListener {
            if(senderInfor!=null && receiInfor!=null) {
                cancelInvitation(receiInfor!!.getToken())
            }
        })

        if(meetingType!=null&& receiInfor!=null){

            initiateMeeting(meetingType!!, receiInfor!!.getToken())
        }
    }

    private fun initiateMeeting(meetingType: String, receiverToken: String){

        try{
            val tokens: JSONArray = JSONArray()
            tokens.put(receiverToken)
            val body: JSONObject = JSONObject()
            val data: JSONObject = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION)
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType)
            data.put(Constants.KEY_AVATAR, senderInfor!!.getAvatar())
            data.put(Constants.KEY_EMAIL,senderInfor!!.getEmail())
            data.put(Constants.KEY_FULLNAME, senderInfor!!.getName())
            data.put(Constants.KEY_UID, senderInfor!!.getUid())
            data.put(Constants.KEY_FCM_TOKEN, senderInfor!!.getToken())

            meetingRoom = senderInfor!!.getUid()+ "_" +
                    UUID.randomUUID().toString().substring(0,5)
            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom)

            body.put(Constants.REMOTE_MSG_DATA, data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION)

        }catch (e : Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendRemoteMessage(remoteMessageBody:String, type: String){

        val v = ApiClient.getClient().create(ApiService::class.java).sendRemoteMessage(
            Constants.getRemoteMessageHeaders(), remoteMessageBody
        )
        v.enqueue(object : Callback<String>{
            override fun onResponse(@NonNull call: Call<String>, @NonNull response: Response<String>) {
                if(response.isSuccessful){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(this@OutgoingInvitationActivity, "Invitation successfully",Toast.LENGTH_SHORT).show()
                    }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(this@OutgoingInvitationActivity, "Invitation Cancelled",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }else{
                    Toast.makeText(this@OutgoingInvitationActivity,response.message(),Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(@NonNull call: Call<String>,@NonNull t: Throwable) {
                Toast.makeText(this@OutgoingInvitationActivity,t.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun cancelInvitation(receiverToken: String){
        try {

            val tokens:JSONArray = JSONArray()
            tokens.put(receiverToken)

            val body:JSONObject = JSONObject()
            val data:JSONObject = JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED)

            body.put(Constants.REMOTE_MSG_DATA,data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE)

        }catch (e: Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type: String  = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)!!
            if(type!=null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    Toast.makeText(this@OutgoingInvitationActivity,"Invitation Accepted", Toast.LENGTH_SHORT).show()
                    try{

                        val server = URL("https://meet.jit.si")

                        val builder = JitsiMeetConferenceOptions.Builder()
                        builder.setServerURL(server)
                        builder.setWelcomePageEnabled(false)
                        builder.setRoom(meetingRoom)
                        if(meetingType.equals("audio")){
                            builder.setVideoMuted(true)
                        }

                        JitsiMeetActivity.launch(this@OutgoingInvitationActivity,builder.build())
                        finish()

                    }catch (e :Exception){
                        Toast.makeText(this@OutgoingInvitationActivity,e.message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(this@OutgoingInvitationActivity,"Invitation Rejected", Toast.LENGTH_SHORT).show()
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
