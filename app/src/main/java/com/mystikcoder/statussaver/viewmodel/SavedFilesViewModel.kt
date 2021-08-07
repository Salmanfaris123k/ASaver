package com.mystikcoder.statussaver.viewmodel

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mystikcoder.statussaver.model.SavedPhotos
import com.mystikcoder.statussaver.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SavedFilesViewModel : ViewModel() {

    private val _savedFiles = MutableLiveData<List<SavedPhotos>>()
    val savedFiles: LiveData<List<SavedPhotos>> = _savedFiles
    private val tempListFiles = mutableListOf<SavedPhotos>()

    suspend fun getSavedFiles(context: Context, name: String) {
        Log.e("Saved files", name)
        if (tempListFiles.isNotEmpty()) {
            tempListFiles.clear()
        }
        withContext(Dispatchers.Main) {

            if (Build.VERSION.SDK_INT >= 29) {

                val tempColl = MediaStore.Downloads.EXTERNAL_CONTENT_URI

                val collection = when(name) {
                    "All" -> "$tempColl/Asaver"
                    "Instagram" -> "$tempColl/Asaver/Instagram"
                    "Facebook" -> "$tempColl/Asaver/Facebook"
                    "Twitter" -> "$tempColl/Asaver/Twitter"
                    "WhatsApp" -> "$tempColl/Asaver/WhatsApp"
                    "Roposo" -> "$tempColl/Asaver/Roposo"
                    "Chingari" -> "$tempColl/Asaver/Chingari"
                    "Moj" -> "$tempColl/Asaver/Moj"
                    "Josh" -> "$tempColl/Asaver/Josh"
                    "Likee" -> "$tempColl/Asaver/Likee"
                    "TikTok" -> "$tempColl/Asaver/TikTok"
                    "MxTakaTak" -> "$tempColl/Asaver/MxTakaTak"
                    "ShareChat" -> "$tempColl/Asaver/ShareChat"
                    "Mitron" -> "$tempColl/Asaver/Mitron"
                    else -> null
                }

                Log.e("Collection", collection.toString())

                val projection = arrayOf(
                    MediaStore.Downloads._ID,
                    MediaStore.Downloads.MIME_TYPE
                )

                try {
                    context.contentResolver.query(
                        Uri.parse(collection),
                        projection,
                        null,
                        null,
                        "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
                    )?.use { cursor ->
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
                        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.MIME_TYPE)

                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idColumn)
                            val mimeType = cursor.getString(mimeTypeColumn)
                            val contentUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                            tempListFiles.add(SavedPhotos(contentUri.toString(), mimeType))
                            Log.e("Content  uri", contentUri.toString())
                        }
                        _savedFiles.value = tempListFiles.toList()
                    }
                }catch (e: Exception){
                    Log.e("Saved view model" , e.message ?: "")
                    _savedFiles.value = tempListFiles.toList()
                }

            } else {
                val path: String = when (name) {
                    "All" -> Utils.PATH_ROOT_DIRECTORY_APP
                    "Instagram" -> Utils.PATH_ROOT_DIRECTORY_INSTAGRAM.toString()
                    "Facebook" -> Utils.PATH_ROOT_DIRECTORY_FACEBOOK.toString()
                    "Josh" -> Utils.PATH_ROOT_DIRECTORY_JOSH.toString()
                    "Chingari" -> Utils.PATH_ROOT_DIRECTORY_CHINGARI.toString()
                    "ShareChat" -> Utils.PATH_ROOT_DIRECTORY_SHARE_CHAT.toString()
                    "Mitron" -> Utils.PATH_ROOT_DIRECTORY_MITRON.toString()
                    "MxTakaTak" -> Utils.PATH_ROOT_DIRECTORY_MX_TAKA_TAK.toString()
                    "WhatsApp" -> Utils.PATH_ROOT_DIRECTORY_WHATSAPP.toString()
                    "Moj" -> Utils.PATH_ROOT_DIRECTORY_MOJ.toString()
                    "Roposo" -> Utils.PATH_ROOT_DIRECTORY_ROPOSSO.toString()
                    "Twitter" -> Utils.PATH_ROOT_DIRECTORY_TWITTER.toString()
                    "TikTok" -> Utils.PATH_ROOT_DIRECTORY_TIK_TOK.toString()
                    "Likee" -> Utils.PATH_ROOT_DIRECTORY_LIKEE.toString()
                    else -> null
                } ?: return@withContext

                val directory = File(path)
                val files = directory.listFiles()
                addFileToList(files)
            }
        }
    }

    private fun addFileToList(files: Array<out File>?) {
        if (!files.isNullOrEmpty()) {
            for (file in files) {
                if (file.isDirectory) {
                    addFileToList(file.listFiles())
                } else {
                    val contentUri = file.path
                    val mimeType = if (file.path.endsWith(".mp4")) "video/*" else "image/*"
                    tempListFiles.add(SavedPhotos(contentUri, mimeType))
                    Log.e("Mime type", mimeType)
                }
            }
            _savedFiles.value = tempListFiles.toList()
        } else {
            _savedFiles.value = listOf()
        }
    }
}
