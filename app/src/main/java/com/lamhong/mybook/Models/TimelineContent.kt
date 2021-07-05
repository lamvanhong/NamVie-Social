package com.lamhong.mybook.Models

class TimelineContent {
    private var post_type : String = ""
    private var id : String = ""
    private var active: Boolean = true

    constructor()
    constructor(post_type: String, id: String, active: Boolean) {
        this.post_type = post_type
        this.id = id
        this.active = active
    }
    fun getPostType(): String{
        return post_type
    }
    fun setPostType(post_type: String){
        this.post_type=post_type
    }
    fun getId(): String{
        return id
    }
    fun setId(id: String){
        this.id=id
    }
    fun getActive(): Boolean{
        return active
    }
    fun setActive(active: Boolean ){
        this.active=active
    }
}