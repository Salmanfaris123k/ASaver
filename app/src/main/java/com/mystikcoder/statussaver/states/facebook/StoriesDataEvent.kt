package com.mystikcoder.statussaver.states.facebook

import com.mystikcoder.statussaver.model.facebook.FacebookNode

sealed class StoriesDataEvent {
    class Success(val list: ArrayList<FacebookNode>?) : StoriesDataEvent()
    class Failure(val errorText: String) : StoriesDataEvent()
    object Empty : StoriesDataEvent()
}