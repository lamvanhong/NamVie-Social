package com.lamhong.viesocial.Models

class Comment {
    private var content : String = ""
    private var ownerComment : String = ""
    // private var imageContent ://update later

    constructor()
    constructor(content: String, ownerComment: String) {
        this.content = content
        this.ownerComment = ownerComment
    }
    fun getContent() : String{
        return content
    }
    fun setContent(content: String){
        this.content=content
    }
    fun getOwner(): String{
        return ownerComment
    }
    fun setOwner(ownerComment: String){
        this.ownerComment=ownerComment
    }

}