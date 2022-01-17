package com.example.cloudopener

import android.content.Intent
import android.net.Uri
import android.os.Parcelable

class IntentActionHandler(val intent: Intent) {
    private lateinit var function: (msg: String) -> Unit

    fun handle() {
        when {
            intent.action == Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText() // Handle text being sent
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage() // Handle single image being sent
                } else {
                    function("🍄 unknown intent type" + intent.type)
                }
            }
            else -> {
                function("🍄 unknown intent action" + intent.action)
            }
        }
    }

    private fun handleSendText() {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            function("🍄 Shared string: " + it)
        }
    }

    private fun handleSendImage() {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            // Update UI to reflect image being shared
            function("🍄 Image URI: " + it.toString())
        }
    }

    fun setWhatHappenedListener(function: (msg: String) -> Unit) {
        this.function = function
    }
}
