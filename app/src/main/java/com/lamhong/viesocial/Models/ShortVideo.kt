package com.lamhong.viesocial.Models

class ShortVideo {
   private var id: String =""
   private var content : String =""
   private  var timestamp : String=""
   private var video : String=""
   private var thumb: String =""
    private var publisher: String =""
   private  var views: Int =0

    constructor()
    constructor(
        id: String,
        content: String,
        timestamp: String,
        video: String,
        thumb: String,
        publisher: String,
        views: Int
    )
    {
        this.id = id
        this.content = content
        this.timestamp = timestamp
        this.video = video
        this.thumb = thumb
        this.publisher = publisher
        this.views = views
    }
    fun getID() : String {
        return id
    }
    fun setID(id: String){
        this.id=id
    }
    fun getContent() : String{
        return content
    }
    fun setContent(content: String){
        this.content=content
    }
    fun getTimeStamp(): String{
        return timestamp
    }
    fun setTimeStamp(timestamp: String){
        this.timestamp= timestamp
    }
    fun getVideo(): String{
        return video
    }
    fun setVideo(video: String) {
        this.video= video
    }
    fun getThumb() : String{
        return thumb
    }
    fun setThumb(thumb: String){
        this.thumb= thumb
    }
    fun getPublisher() : String{
        return publisher
    }
    fun setPublisher(publisher: String){
        this.publisher= publisher
    }
    fun getViews(): Int{
        return views
    }
    fun setView(views: Int){
        this.views=views
    }




}