package ru.porcupine.testtask.web

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.ValueCallback
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ru.porcupine.testtask.R
import ru.porcupine.testtask.databinding.WebViewActivBinding
import ru.porcupine.testtask.web.weblib.CaseChromeClient
import ru.porcupine.testtask.web.weblib.CaseWebViewClient
import ru.porcupine.testtask.web.weblib.ClientImpl


class WebViewActiv : AppCompatActivity(), ClientImpl {
    private var valueCallback: ValueCallback<Array<Uri>>? = null
    private var launcherString: ActivityResultLauncher<String>? = null
    private var launcherIntent: ActivityResultLauncher<Intent>? = null
    private val binding: WebViewActivBinding by lazy { WebViewActivBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private var timExit = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        launcherString = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requestPermission(it)
        }

        launcherIntent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                startActivityResult(it)
            }
        setContent()
    }


    @SuppressLint("SetJavaScriptEnabled")
    fun setContent() {
        binding.contentWebView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportMultipleWindows(false)
            }
            val lastUrl = sharedPreferences.getString("last_url", "https://www.google.com/")
            loadUrl(lastUrl!!)
            webViewClient = CaseWebViewClient(this@WebViewActiv)
            webChromeClient = CaseChromeClient(this@WebViewActiv)
        }
        binding.reset.setOnClickListener {
            binding.contentWebView.reload()
            binding.contentWebView.visibility = View.VISIBLE
            binding.linearLayout.visibility = View.GONE
        }

    }

    override fun onProgressStart() {
        binding.progress.show()
        binding.progress.progress = 0
    }

    override fun onProgressFinish() {
        binding.progress.hide()
    }

    override fun onProgressError(error: String) {
        binding.contentWebView.visibility = View.GONE
        binding.linearLayout.visibility = View.VISIBLE
        binding.textError.text = error
    }

    override fun onProgress(progress: Int) {
        binding.progress.setProgressCompat(progress, true)
    }

    override fun onShowFileChooser(filePathCallback: ValueCallback<Array<Uri>>?) {
        valueCallback = filePathCallback
    }

    override fun onPermission(boolean: Boolean) {
        launcherString?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (System.currentTimeMillis() - timExit > 800) {
            Toast.makeText(this, getString(R.string.exit_app), Toast.LENGTH_SHORT).show()
            timExit = System.currentTimeMillis()
        } else {
            saveCurrentPage()
            finish()
        }

        return true
    }

    override fun saveCurrentPage() {
        val editor = sharedPreferences.edit()
        editor.putString("last_url", binding.contentWebView.url)
        editor.apply()
    }

    fun requestPermission(boolean: Boolean) {
        when (boolean) {
            true -> {
                launcherIntent?.launch(chooser())
            }

            else -> {
                try {
                    valueCallback?.onReceiveValue(arrayOf())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun startActivityResult(activityRes: ActivityResult) {
        if (activityRes.resultCode != 0) valueCallback?.onReceiveValue(uris(activityRes))
        else valueCallback?.onReceiveValue(arrayOf())
    }


    private fun chooser(): Intent? {
        return Intent.createChooser(
            Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png")
                )
            }, "Image"

        )
    }

    fun uris(result: ActivityResult): Array<Uri>? {
        return if (result.resultCode == RESULT_OK) {
            val intent = result.data
            var arrayOfUris: Array<Uri>? = null
            if (intent != null) {
                val uriString = intent.dataString
                arrayOfUris = arrayOf(Uri.parse(uriString))
                val clipData = intent.clipData
                if (clipData != null) {
                    arrayOfUris = Array(clipData.itemCount) { clipData.getItemAt(it).uri }
                }
                if (uriString != null) arrayOfUris = arrayOf(Uri.parse(uriString))
            }
            arrayOfUris
        } else {
            arrayOf()
        }
    }
}