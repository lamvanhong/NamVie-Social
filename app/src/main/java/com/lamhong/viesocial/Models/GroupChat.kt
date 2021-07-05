package com.lamhong.viesocial.Models

class GroupChat {
    private var groupID : String = ""
    private var groupTiTle : String = ""
    private var groupDescription : String = ""
    private var groupIcon : String = ""
    private var timestamp : String = ""
    private var createBy : String = ""

    constructor()

    constructor(
        groupID: String,
        groupTiTle: String,
        groupDescription: String,
        groupIcon: String,
        timestamp: String,
        createBy: String
    ) {
        this.groupID = groupID
        this.groupTiTle = groupTiTle
        this.groupDescription = groupDescription
        this.groupIcon = groupIcon
        this.timestamp = timestamp
        this.createBy = createBy
    }


    fun getGroupID() : String {
        return groupID
    }

    fun setGroupID(groupID: String){
        this.groupID=groupID
    }

    fun getGroupTitle() : String {
        return groupTiTle
    }

    fun setGroupTitle(groupTitle: String){
        this.groupTiTle=groupTitle
    }

    fun getGroupDescription() : String {
        return groupDescription
    }

    fun setGroupDescription(groupDescription: String){
        this.groupDescription=groupDescription
    }

    fun getGroupIcon() : String {
        return groupIcon
    }

    fun setGroupIcon(groupIcon: String){
        this.groupIcon=groupIcon
    }

    fun getTimestamp() : String {
        return timestamp
    }

    fun setTimestamp(timestamp: String){
        this.timestamp=timestamp
    }

    fun getCreateBy() : String {
        return createBy
    }

    fun setCreateBy(createBy: String){
        this.createBy=createBy
    }

}