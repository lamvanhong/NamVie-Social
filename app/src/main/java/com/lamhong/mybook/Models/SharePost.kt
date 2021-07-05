package com.lamhong.mybook.Models

class SharePost {
    private var shareID : String = ""
    private var postID: String = ""
    private var content : String = ""
    private var type : String = ""
    private var typeshare : String = ""
    private var publisher: String = ""

    constructor()
    constructor(shareID: String, postID: String, content: String, type: String, typeshare: String , publisher: String) {
        this.shareID = shareID
        this.postID = postID
        this.content = content
        this.type = type
        this.typeshare = typeshare
        this.publisher=publisher
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
    fun setType(typeshare: String){
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
}