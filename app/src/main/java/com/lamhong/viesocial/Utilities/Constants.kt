package com.lamhong.viesocial.Utilities

public class Constants {

    companion object {
        public final val REMOTE_MSG_AUTHORIZATION: String = "Authorization"
        public final val REMOTE_MSG_CONTENT_TYPE: String = "Content-Type"

        public final val REMOTE_MSG_TYPE: String = "type"
        public final val REMOTE_MSG_INVITATION: String = "invitation"
        public final val REMOTE_MSG_MEETING_TYPE: String = "meetingType"
        public final val REMOTE_MSG_INVITER_TOKEN: String = "inviterToken"
        public final val REMOTE_MSG_DATA: String = "data"
        public final val REMOTE_MSG_REGISTRATION_IDS: String = "registration_ids"

        public final val REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse"
        public final val REMOTE_MSG_INVITATION_ACCEPTED = "accepted"
        public final val REMOTE_MSG_INVITATION_REJECTED = "rejected"
        public final val REMOTE_MSG_INVITATION_CANCELLED = "cancelled"

        public final val REMOTE_MSG_MEETING_ROOM = "meetingRoom"

        public final val KEY_FULLNAME = "fullname"
        public final val KEY_UID = "uid"
        public final val KEY_EMAIL = "email"
        public final val KEY_AVATAR = "avatar"
        public final val KEY_FCM_TOKEN = "fcm_token"

        const val MESSAGING_SERVICE_TYPE = "messagingType"

        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAA12IGRrg:APA91bFukI15Blcri8wXmatTFUlnWCTpeOARwf2Co_ZAfWAkIzeo1ddpKVUkQLG9zp6-e896BTTgvUi-bpBvhUA1Z3Z-6LEhBiXuSOTYWYBDZs8g_OaUEd04qIHHEREeQbMccjyWgbrB"
        const val CONTENT_TYPE = "application/json"

        public fun getRemoteMessageHeaders(): HashMap<String, String> {
            val headers: HashMap<String, String> = HashMap()
            headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAA12IGRrg:APA91bFukI15Blcri8wXmatTFUlnWCTpeOARwf2Co_ZAfWAkIzeo1ddpKVUkQLG9zp6-e896BTTgvUi-bpBvhUA1Z3Z-6LEhBiXuSOTYWYBDZs8g_OaUEd04qIHHEREeQbMccjyWgbrB"
            )
            headers.put(Constants.REMOTE_MSG_CONTENT_TYPE, "application/json")
            return headers
        }
    }

}