package com.lamhong.mybook.Models

class Message {
    //private var messageID : String = ""
    private var message : String = ""
    private var senderID : String = ""
    private var timestamp : String = ""
    //private var feeling : Int = 0
    private var imageUrl : String = ""
    private var isSeen : Boolean = false
    private var type : String = ""



    constructor(message: String, senderID: String, timestamp: String, isSeen: Boolean, type: String) {
        this.message = message
        this.senderID = senderID
        this.timestamp = timestamp
        this.isSeen = isSeen
        this.type = type
    }

    constructor()


//    fun getMessageID() : String {
//        return messageID
//    }
//
//    fun setMessageID(messageID:String){
//        this.messageID=messageID
//    }

    fun getMessage() : String {
        return message
    }

    fun setMessage(message:String){
        this.message=message
    }

    fun getSenderID() : String {
        return senderID
    }

    fun setSenderID(senderID:String){
        this.senderID=senderID
    }


    fun getTimestamp() : String {
        return timestamp
    }

    fun setTimestamp(timestamp:String){
        this.timestamp=timestamp
    }

//    fun getFeeling() : Int {
//        return feeling
//    }
//
//    fun setFeeling(feeling:Int){
//        this.feeling=feeling
//    }
//
    fun getImageUrl() : String {
        return imageUrl
    }

    fun setImageUrl(imageUrl:String) {
        this.imageUrl=imageUrl
    }

    fun isSeen() : Boolean {
        return isSeen
    }

    fun setisSeen(isSeen: Boolean) {
        this.isSeen = isSeen
    }

    fun getType() : String {
        return type
    }

    fun setType(type: String){
        this.type=type
    }
}