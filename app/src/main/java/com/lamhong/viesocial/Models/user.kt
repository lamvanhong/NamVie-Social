package com.lamhong.viesocial.Models

import java.io.Serializable


    class User : Serializable {
    private var fullname : String=""
    private var email: String=""
    private var uid: String=""
    private var avatar : String=""
    private var token : String=""
        private var timeChat : String  = ""
  //  private var imageUrl : String=""
    constructor()
    constructor( fullname : String,email: String, uid: String , avatar : String){
        this.fullname=fullname
        this.email=email
        this.uid=uid
        this.avatar=avatar
       // this.imageUrl=imageUrl
    }
    fun getAvatar(): String{
        return avatar
    }
    fun setAvatar(avatar: String){
        this.avatar=avatar
    }
    fun getName(): String{
        return fullname
    }
    fun setName(fullname: String){
        this.fullname=fullname
    }
    fun getEmail(): String{
        return email;
    }
    fun setEmail(email : String){
        this.email=email;
    }
    fun getUid(): String{
        return uid
    }
    fun setUid(uid : String){
        this.uid=uid
    }
    fun getToken():String{
        return token
    }
    fun setToken(token :String ){
        this.token = token
    }
        fun getTimeChat():String{
            return timeChat
        }
        fun setTimeChat(timeChat :String ){
            this.timeChat =timeChat
        }
//    fun setImageurl(imageUrl: String){
//        this.imageUrl=imageUrl
//    }
//    fun getImageurl() : String{
//        return imageUrl
//    }

}