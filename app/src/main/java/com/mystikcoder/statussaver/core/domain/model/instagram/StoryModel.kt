package com.mystikcoder.statussaver.core.domain.model.instagram

import com.google.gson.annotations.SerializedName

data class StoryModel(

    @SerializedName("tray")
    val tray: ArrayList<TrayModel>,

    @SerializedName("status")
    val status: String
)