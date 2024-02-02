package ru.porcupine.testtask.web.weblib

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class CaseChromeClient(private val clientImpl: ClientImpl) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        clientImpl.onProgress(newProgress)
        if (newProgress == 100) {
            clientImpl.onProgressFinish()
        }
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        clientImpl.onShowFileChooser(filePathCallback)
        clientImpl.onPermission(true)
        return true
    }


}