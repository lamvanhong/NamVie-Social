package com.lamhong.viesocial.Models


class ActivityManager {
    private var post_type : String = ""
    private var id : String = ""
    private var active: Boolean = true
    private var type: String = ""
    private var timestamp: String = ""

    constructor()
    constructor(post_type: String, id: String, active: Boolean , type: String, timestamp: String) {
        this.post_type = post_type
        this.id = id
        this.active = active
        this.type = type
        this.timestamp = timestamp
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
    fun getType(): String{
        return type
    }
    fun setType(type: String ){
        this.type=type
    }
    fun getTimeStamp(): String{
        return timestamp
    }
    fun setTimeStamp(timestamp: String ){
        this.timestamp=timestamp
    }

}