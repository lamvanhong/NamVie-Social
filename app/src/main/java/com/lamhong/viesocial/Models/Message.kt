package com.lamhong.viesocial.Models

class Message {
    private var messageID : String = ""
    private var message : String = ""
    private var senderID : String = ""
    private var timestamp : Long = 0
    private var feeling : Int = 0



    constructor(message: String, senderID: String, timestamp: Long) {
        this.message = message
        this.senderID = senderID
        this.timestamp = timestamp
    }

    constructor()


    fun getMessageID() : String {
        return messageID
    }

    fun setMessageID(messageID:String){
        this.messageID=messageID
    }

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


    fun getTimestamp() : Long {
        return timestamp
    }

    fun setTimestamp(timestamp:Long){
        this.timestamp=timestamp
    }

    fun getFeeling() : Int {
        return feeling
    }

    fun setFeeling(feeling:Int){
        this.feeling=feeling
    }
}