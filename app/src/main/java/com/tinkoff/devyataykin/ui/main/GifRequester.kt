package com.tinkoff.devyataykin.ui.main

import android.net.Uri.Builder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

class GifRequester {
    private val client: OkHttpClient = OkHttpClient()
    val gifUrl = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    init {
        getGif("random", "")
    }

    fun getGif(category: String, pageNumber: String) {
        val urlRequest = Builder().
                scheme(URL_SCHEME).
                authority(URL_AUTHORITY).
                appendPath(category).
                //appendPath(pageNumber).
                appendQueryParameter("json", "true").
                build().
                toString()

        val request = Request.Builder().url(urlRequest).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body!!.string()
                    val jsonObject = JSONTokener(responseBody).nextValue() as JSONObject
                    gifUrl.postValue(jsonObject.getString("gifURL"))
                    description.postValue(jsonObject.getString("description"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {
        private const val URL_SCHEME = "https"
        private const val URL_AUTHORITY = "developerslife.ru"

    }
}