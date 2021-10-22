package com.mystikcoder.statussaver.core.data.repository.chingari.implementation

import com.mystikcoder.statussaver.core.data.repository.chingari.abstraction.ChingariDownloadRepository
import com.mystikcoder.statussaver.core.domain.model.response.DownloadRequestResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ChingariDownloadRepositoryImpl : ChingariDownloadRepository {

    override suspend fun downloadChingariFile(url: String): DownloadRequestResponse {

        kotlin.runCatching {

            val document: Document = Jsoup.connect(url).get()

            val videoUrl =
                document.select("meta[property=\"og:video:secure_url\"]")
                    .last()
                    ?.attr("content")
            return if (!videoUrl.isNullOrEmpty()) {
                DownloadRequestResponse(isSuccess = true, downloadLink = videoUrl)
            } else {
                DownloadRequestResponse(errorMessage = "No data found")
            }
        }.getOrElse {
            return DownloadRequestResponse(errorMessage = it.message ?: "Something Went Wrong")
        }
    }
}