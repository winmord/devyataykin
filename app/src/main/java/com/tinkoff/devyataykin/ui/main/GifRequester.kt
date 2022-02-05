package com.tinkoff.devyataykin.ui.main

import android.net.Uri.Builder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.*

class GifRequester {
    private val client: OkHttpClient = OkHttpClient()
    val gifUrl = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    var currentGifIndex = -1
    val gifs: MutableList<Pair<String, String>> = mutableListOf()

    init {
        getGif(0, "")
    }

    fun getPrev() {
        if(gifs.isNotEmpty() && currentGifIndex > 0) {
            val gif = gifs[--currentGifIndex]
            gifUrl.postValue(gif.first!!)
            description.postValue(gif.second!!)
        }
    }

    fun getGif(category: Int, pageNumber: String) {
        if(currentGifIndex < gifs.size - 1) {
            val gif = gifs[++currentGifIndex]
            gifUrl.postValue(gif.first!!)
            description.postValue(gif.second!!)
        } else {
            val gifCategory = when(category) {
                0 -> "latest"
                1 -> "top"
                else -> "hot"
            }

            val urlRequest =
                Builder().
                scheme(URL_SCHEME).
                authority(URL_AUTHORITY).
                appendPath("random"). //appendPath(pageNumber).
                appendQueryParameter("json", "true").
                build().toString()
            val request = Request.Builder().url(urlRequest).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body!!.string()
                        val jsonObject = JSONTokener(responseBody).nextValue() as JSONObject
                        val url = jsonObject.getString("gifURL")
                        val desc = jsonObject.getString("description")
                        gifUrl.postValue(url)
                        description.postValue(desc)

                        gifs.add(Pair(url, desc))
                        ++currentGifIndex
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    companion object {
        private const val URL_SCHEME = "https"
        private const val URL_AUTHORITY = "developerslife.ru"

    }
}