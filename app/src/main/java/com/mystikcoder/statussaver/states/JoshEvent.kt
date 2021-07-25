package com.mystikcoder.statussaver.states

sealed class JoshEvent {
    class Success(val fileName: String, val videoUrl: String) : JoshEvent()
    class Failure(val errorText: String) : JoshEvent()
    object Empty : JoshEvent()
}
