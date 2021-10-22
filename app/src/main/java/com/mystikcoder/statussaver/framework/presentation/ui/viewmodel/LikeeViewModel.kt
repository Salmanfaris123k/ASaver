package com.mystikcoder.statussaver.framework.presentation.ui.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mystikcoder.statussaver.core.data.repository.likee.abstraction.LikeeDownloadRepository
import com.mystikcoder.statussaver.framework.events.common.DownloadRequestEvent
import com.mystikcoder.statussaver.framework.extensions.getFileName
import com.mystikcoder.statussaver.framework.utils.LIKEE
import com.mystikcoder.statussaver.framework.utils.NetworkState
import com.mystikcoder.statussaver.framework.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeeViewModel @Inject constructor(
    private val app: Application,
    private val repository: LikeeDownloadRepository
) : AndroidViewModel(app) {

    private val _stateEvent = MutableStateFlow<DownloadRequestEvent>(DownloadRequestEvent.Idle)
    val downloadEvent: StateFlow<DownloadRequestEvent> = _stateEvent

    fun download(url: String) {

        when {
            url.isEmpty() -> {
                _stateEvent.value = DownloadRequestEvent.Error("Empty Url")
                return
            }
            !url.contains("like") || !Patterns.WEB_URL.matcher(url).matches() -> {
                _stateEvent.value = DownloadRequestEvent.Error("Enter Valid Url")
                return
            }
            !NetworkState.isNetworkAvailable() -> {
                _stateEvent.value = DownloadRequestEvent.Error("No Internet Connection")
                return
            }
        }

        _stateEvent.value = DownloadRequestEvent.Loading

        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.downloadLikeeFile(url)

            if (response.isSuccess) {

                val downloadUrl = response.downloadLink!!

                Utils.startDownload(
                    downloadUrl, Utils.DIRECTORY_LIKEE, app, downloadUrl.getFileName(
                        LIKEE
                    )
                )

                _stateEvent.value = DownloadRequestEvent.Success

            } else {
                _stateEvent.value = DownloadRequestEvent.Error(response.errorMessage!!)
            }
        }
    }
}