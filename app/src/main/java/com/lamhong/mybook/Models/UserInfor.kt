package com.lamhong.mybook.Models

class UserInfor  {
    private var bio : String= ""
    private var coverImage : String =""
    private var education : String = ""
    private var education1 : String = ""
    private var education2 : String = ""
    private var job: String = ""
    private var home : String = ""
    private var homeTown : String = ""
    private var relationship :String = ""
    private var workPlace : String = ""

    constructor()
    constructor(
        bio: String,
        coverImage: String,
        education: String,
        education1: String,
        education2: String,
        job: String,
        home: String,
        homeTown: String,
        relationship: String,
        workPlace: String
    ) {
        this.bio = bio
        this.coverImage = coverImage
        this.education = education
        this.education1 = education1
        this.education2 = education2
        this.job = job
        this.home = home
        this.homeTown = homeTown
        this.relationship = relationship
        this.workPlace = workPlace
    }

    fun getBio(): String{
        return bio
    }
    fun setBio(bio: String){
        this.bio=bio
    }
    fun getCoverImage(): String {
        return coverImage
    }
    fun setCoverImage(coverImage: String){
        this.coverImage=coverImage
    }
    fun getEducation(): String{
        return education
    }
    fun setEducation(education: String){
        this.education=education
    }
    fun getEducation1(): String{
        return education1
    }
    fun setEducation1(education1: String){
        this.education1=education1
    }
    fun getEducation2(): String{
        return education2
    }
    fun setEducation2(education2: String){
        this.education2=education2
    }
    fun getJob() :String {
        return job
    }
    fun setJob(job: String){
        this.job=job
    }
    fun getHome(): String {
        return this.home
    }
    fun setHome(home: String){
        this.home=home
    }
    fun getHomeTown(): String{
        return homeTown
    }
    fun setHomeTown(homeTown: String){
        this.homeTown=homeTown
    }
    fun getRelationship() : String {
        return relationship
    }
    fun setRelationship(relationship: String){
        this.relationship= relationship
    }
    fun getWorkPlace() : String{
        return workPlace
    }
    fun setWorkPlace(workPlace: String){
        this.workPlace= workPlace
    }


}