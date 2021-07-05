package com.lamhong.mybook.Models

class Comment {
    private var content : String = ""
    private var ownerComment : String = ""
    private var idComment : String =""
    // private var imageContent ://update later

    constructor()
    constructor(content: String, ownerComment: String, idComment: String) {
        this.content = content
        this.ownerComment = ownerComment
        this.idComment= idComment
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
    fun getIdComment(): String{
        return idComment
    }
    fun setIdComment(idComment: String){
        this.idComment= idComment
    }

}