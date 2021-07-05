package com.lamhong.mybook.Models

class Post {
    private var post_content : String = ""
    private var post_id  : String = ""
    private var post_image: String = ""
    private var publisher : String = ""
    constructor()
    constructor(post_content: String, post_id: String, post_image: String, publisher: String) {
        this.post_content = post_content
        this.post_id = post_id
        this.post_image = post_image
        this.publisher = publisher
    }
    fun getpostContent() : String{
        return post_content
    }
    fun setpostContent(post_content: String) {
        this.post_content=post_content
    }
    fun getpost_id() : String{
        return post_id
    }
    fun setpost_id(post_id: String ) {
        this.post_id=post_id
    }
    fun getpost_image() : String{
        return post_image
    }
    fun setpost_image(post_image: String) {
        this.post_image=post_image
    }
    fun getpublisher() : String{
        return publisher
    }
    fun setpublisher(publisher: String) {
        this.publisher=publisher
    }



//    private var post_content : String=""
//    private var post_id: String =""
//    private var post_image:String =""
//    private var publisher: String =""
//
//
//
//    constructor(post_content: String,post_id: String, post_image: String, publisher: String ) {
//        this.post_id = post_id
//        this.post_image = post_image
//        this.publisher = publisher
//        this.post_content = post_content
//    }
//    fun getPostID(): String{
//        return post_id
//    }
//    fun getPostImage(): String{
//        return post_image
//    }
//    fun getPubliser(): String{
//        return publisher
//    }
//    fun getDescription(): String{
//        return post_content
//    }
//
//    fun setPostId(post_id: String){
//        this.post_id=post_id
//    }
//    fun setPostImage(post_image: String){
//        this.post_image=post_image
//    }
//    fun setPublisher(publiser: String){
//        this.publisher=publiser
//    }
//    fun setDescription(post_content:  String){
//        this.post_content=post_content
//    }

}



