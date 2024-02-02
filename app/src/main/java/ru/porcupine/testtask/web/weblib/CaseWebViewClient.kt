package ru.porcupine.testtask.web.weblib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi


class CaseWebViewClient(private val clientImpl: ClientImpl) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        clientImpl.onProgressStart()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        clientImpl.onProgressFinish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
    ) {
        if (request!!.isForMainFrame) {
            when (error?.errorCode) {
                ERROR_HOST_LOOKUP -> {
                    clientImpl.onProgressError(ERROR_HOST_LOOKUP_QUINZ)
                }
                ERROR_CONNECT -> {
                    clientImpl.onProgressError(ERROR_CONNECT_QUINZ)
                }
                ERROR_TIMEOUT -> {
                    clientImpl.onProgressError(ERROR_TIMEOUT_QUINZ)
                }
            }
        }
        super.onReceivedError(view, request, error)
    }


    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return if ((request?.url?.scheme.equals("market")) || (request?.url?.host.equals("play.google.com"))) {
            request?.let { (view as Activity).openLinkMarket(it) }
            view?.requestFocus()
            true
        } else
            false
    }

    private fun Context.openLinkMarket(request: WebResourceRequest) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString())))
    }


    companion object{
        /**-------------------- WEB VIEW ERROR  -------------------------------*/

        const val ERROR_HOST_LOOKUP_QUINZ =
            "Error searching server hostname or proxy server"
        const val ERROR_CONNECT_QUINZ = "Failed to connect to server"
        const val ERROR_TIMEOUT_QUINZ = "Expired connection"

    }
}