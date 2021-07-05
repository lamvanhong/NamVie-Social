package com.lamhong.viesocial.Models

class SharePost {
    private var shareID : String = ""
    private var postID: String = ""
    private var content : String = ""
    private var type : String = ""
    private var typeshare : String = ""
    private var publisher: String = ""
    private var postOwner: String = ""

    constructor()
    constructor(shareID: String, postID: String, content: String, type: String, typeshare: String , publisher: String
            , postOwner : String) {
        this.shareID = shareID
        this.postID = postID
        this.content = content
        this.type = type
        this.typeshare = typeshare
        this.publisher=publisher
        this.postOwner= postOwner
    }
    fun getShareID() : String {
        return shareID
    }
    fun setShareID(shareID: String){
        this.shareID = shareID
    }
    fun getPostID(): String {
        return postID
    }
    fun setPostID(postID: String) {
        this.postID= postID
    }
    fun getContent() : String{
        return content
    }
    fun setContent(content: String){
        this.content= content
    }
    fun getType() : String{
        return type
    }
    fun setType(type: String){
        this.type=type
    }
    fun getTypeShare(): String{
        return typeshare
    }
    fun setTypeShare(typeshare: String){
        this.typeshare = typeshare
    }
    fun getPublisher(): String {
        return publisher
    }
    fun setPubliher (publisher: String){
        this.publisher= publisher
    }
    fun getPostOwner(): String{
        return postOwner
    }
    fun setPostOwner(postOwner: String){
        this.postOwner= postOwner
    }
}