package ru.porcupine.testtask.web.weblib

import android.net.Uri
import android.webkit.ValueCallback

interface ClientImpl {

    fun onProgressStart()
    fun onProgressFinish()
    fun onProgressError(error: String)
    fun onProgress(progress: Int)
    fun onShowFileChooser(filePathCallback: ValueCallback<Array<Uri>>?)
    fun onPermission(boolean: Boolean)
    fun saveCurrentPage()

}