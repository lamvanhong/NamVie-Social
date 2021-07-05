package com.lamhong.mybook.Models

class GroupMessage {
    private var message : String = ""
    private var sender : String = ""
    private var timestamp : String = ""
    private var type : String = ""

    constructor()

    constructor(message: String, sender: String, timestamp: String, type: String) {
        this.message = message
        this.sender = sender
        this.timestamp = timestamp
        this.type = type
    }

    fun getMessageG() : String {
        return message
    }

    fun setMessageG(message: String){
        this.message=message
    }

    fun getsenderIDG() : String {
        return sender
    }

    fun setSenderIDG(sender: String){
        this.sender=sender
    }

    fun gettimestampG() : String {
        return timestamp
    }

    fun setTimestampG(timestamp: String){
        this.timestamp=timestamp
    }

    fun getTypeG() : String {
        return type
    }

    fun setTypeG(type: String){
        this.type=type
    }




}