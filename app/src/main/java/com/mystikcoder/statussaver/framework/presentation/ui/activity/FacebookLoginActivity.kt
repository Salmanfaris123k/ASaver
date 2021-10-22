package com.mystikcoder.statussaver.framework.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mystikcoder.statussaver.R
import com.mystikcoder.statussaver.databinding.ActivityLoginBinding
import com.mystikcoder.statussaver.framework.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("SetJavaScriptEnabled")
class FacebookLoginActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: Preferences

    private lateinit var binding: ActivityLoginBinding
    private var cookies: String? = null
    private var webUrl1: String? = null
    private var webUrl2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loadPage()
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadPage()
        }
    }

    private fun loadPage() {
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.clearCache(true)

        val settings: WebSettings = binding.webView.settings.apply {
            builtInZoomControls = true
            displayZoomControls = true
            useWideViewPort = true
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            setAppCacheEnabled(true)
            databaseEnabled = true
            allowContentAccess = true
            setSupportMultipleWindows(true)
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            javaScriptCanOpenWindowsAutomatically = true
        }

        binding.webView.addJavascriptInterface(this, "Android")
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.binding.webView, true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        settings.pluginState = WebSettings.PluginState.ON
        settings.loadWithOverviewMode = true
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView, i: Int) {
                binding.swipeRefreshLayout.isRefreshing = i != 100
            }
        }
        binding.webView.webViewClient = MyBrowser()
        binding.webView.loadUrl("https://www.facebook.com/")
    }

    private inner class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): Boolean {

            webView.loadUrl(webResourceRequest.url.toString())
            cookies =
                CookieManager.getInstance().getCookie(webResourceRequest.url.toString())
            cookies
            if (Utils.isNullOrEmpty(cookies) || !cookies?.contains(
                    "c_user"
                )!!
            ) {
                return true
            }
            preferences.putString(FB_COOKIES, cookies!!)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, str: String): Boolean {
            webView.loadUrl(str)
            cookies = CookieManager.getInstance().getCookie(str)
            cookies
            if (Utils.isNullOrEmpty(cookies) || !cookies?.contains(
                    "c_user"
                )!!
            ) {
                return true
            }
            preferences.putString(FB_COOKIES, cookies!!)
            return true
        }

        override fun onPageFinished(webView: WebView, str: String) {
            super.onPageFinished(webView, str)
            cookies = CookieManager.getInstance().getCookie(str)
            webUrl1 = "javascript:Android.resultOnFinish();"
            webView.loadUrl("javascript:Android.resultOnFinish();")
            webView.loadUrl("javascript:var el = document.querySelectorAll('input[name=fb_dtsg]');Android.resultOnFinish(el[0].value);")
        }
    }

    @JavascriptInterface
    fun resultOnFinish(aString: String) {
        if (aString.length >= 15) {
            try {
                if (!Utils.isNullOrEmpty(cookies) && cookies?.contains("c_user")!!) {
                    preferences.putBoolean(IS_FB_LOGGED_IN, true)
                    preferences.putString(FB_KEY, aString)
                    Intent().also {
                        it.putExtra("result", "result")
                        setResult(RESULT_OK, it)
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
