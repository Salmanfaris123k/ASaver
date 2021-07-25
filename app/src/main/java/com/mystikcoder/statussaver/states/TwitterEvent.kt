package com.mystikcoder.statussaver.states

sealed class TwitterEvent {
    class Success(val fileName: String, val mediaUrl: String) : TwitterEvent()
    class Failure(val errorText: String) : TwitterEvent()
    object Loading : TwitterEvent()
    object Empty : TwitterEvent()
}