package com.mystikcoder.statussaver.framework.presentation.ui.activity

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.mystikcoder.statussaver.R
import com.mystikcoder.statussaver.databinding.ActivitySharechatBinding
import com.mystikcoder.statussaver.framework.events.common.DownloadRequestEvent
import com.mystikcoder.statussaver.framework.extensions.*
import com.mystikcoder.statussaver.framework.presentation.ui.viewmodel.ShareChatViewModel
import com.mystikcoder.statussaver.framework.utils.DialogUtil
import com.mystikcoder.statussaver.framework.utils.NetworkState
import com.mystikcoder.statussaver.framework.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ShareChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharechatBinding
    private lateinit var clipboard: ClipboardManager
    val viewModel: ShareChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sharechat)
        initViews()
    }

    private fun initViews() {

        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.imageBack.setOnClickListener {
            onBackPressed()
        }
        binding.imageAppLogo.setOnClickListener {
            startAnotherApp("in.mohalla.sharechat", "in.mohalla.sharechatlite")
        }
        binding.buttonPasteLink.setOnClickListener {
            binding.inputLink.setText(clipboard.getClipboardText(applicationContext))
        }

        binding.imageInfo.setOnClickListener {
            DialogUtil.openBottomSheetDialog(this)
        }

        binding.buttonDownload.setOnClickListener {
            if (NetworkState.isNetworkAvailable()) {
                if (Build.VERSION.SDK_INT >= 29) {
                    setupListeners()
                } else {
                    if (Utils.hasWritePermission(applicationContext)) {
                        setupListeners()
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            binding.root.showSettingsSnackbar(this)
                        } else {
                            binding.root.showRequestPermissionSnackbar(this)
                        }
                    }
                }
            } else {
                applicationContext.showShortToast("No Internet connection available")
            }
        }

        lifecycleScope.launchWhenStarted {

            viewModel.downloadEvent.collect { event ->
                when (event) {
                    is DownloadRequestEvent.Success -> {
                        hideProgressBar()
                    }
                    is DownloadRequestEvent.Error -> {
                        hideProgressBar()
                        applicationContext.showShortToast(event.errorMessage)
                    }
                    is DownloadRequestEvent.Loading -> {
                        showProgressBar()
                    }
                    else -> hideProgressBar()
                }
            }
        }

    }

    private fun setupListeners() {
        viewModel.download(binding.inputLink.text.toString())
    }

    override fun onBackPressed() {
        if (DialogUtil.isSheetShowing()){
            DialogUtil.hideSheet()
            return
        }else{
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        clipboard.getClipboardText(applicationContext)?.let {
            if (it.contains("sharechat")) {
                binding.inputLink.setText(it)
            }
        }
    }

    private fun showProgressBar() {
        binding.buttonDownload.visibility = View.GONE
        binding.buttonPasteLink.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.buttonDownload.visibility = View.VISIBLE
        binding.buttonPasteLink.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }
}