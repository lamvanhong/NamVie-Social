package com.lamhong.viesocial.Models

class Notify {
    private var userID : String=""
    private var notify : String =""
    private var postID: String =""
    private var type: String=""
    private var notifyID: String=""

    constructor()
    constructor(userID: String, notify: String, postID: String, type: String, notifyID: String) {
        this.userID = userID
        this.notify = notify
        this.postID = postID
        this.type = type
        this.notifyID=notifyID
    }
    fun getUserID(): String{
        return userID
    }
    fun setUserID(userID: String){
        this.userID=userID
    }
    fun getNotify(): String{
        return notify
    }
    fun setNOtify(notify: String){
        this.notify= notify
    }
    fun gePostID():String{
        return postID
    }
    fun setPostID(postID: String){
        this.postID=postID
    }
    fun getType(): String{
        return type
    }
    fun setType(type: String){
        this.type=type
    }
    fun getnotifyID():String{
        return notifyID
    }
    fun setnotifyID(notifyID: String){
        this.notifyID=notifyID
    }


}