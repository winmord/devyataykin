package com.tinkoff.devyataykin.ui.main

import android.net.Uri.Builder
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException

class GifRequester {
    private val client: OkHttpClient = OkHttpClient()
    val gifUrl = MutableLiveData<String?>()
    val description = MutableLiveData<String?>()
    var online = true

    private var currentGifIndexes = mutableListOf(-1, -1, -1)
    private val gifCurrentPages = mutableListOf(0, 0, 0)
    val gifs: MutableMap<Int, MutableList<Pair<String, String>>> = mutableMapOf()

    init {
        gifs[0] = mutableListOf()
        gifs[1] = mutableListOf()
        gifs[2] = mutableListOf()

        getNextGif(0)
    }

    fun getCurrentGif(category: Int) {
        gifUrl.postValue("")
        description.postValue("")
        if(online) {
            if (gifs[category]!!.isNotEmpty() && currentGifIndexes[category] >= 0) {
                val gif = gifs[category]!![currentGifIndexes[category]]
                gifUrl.postValue(gif.first)
                description.postValue(gif.second)
            } else {
                getNextGif(category)
            }
        }
    }

    fun getPrevGif(category: Int) {
        if (gifs[category]!!.isNotEmpty() && currentGifIndexes[category] > 0) {
            val gif = gifs[category]!![--currentGifIndexes[category]]
            gifUrl.postValue(gif.first)
            description.postValue(gif.second)
        }
    }

    fun getNextGif(category: Int) {
        if (isThereNextGif(category)) {
            getNextGifFromCache(category)
        } else {
            getNextGifFromNet(category)
        }
    }

    private fun isThereNextGif(category: Int): Boolean {
        return currentGifIndexes[category] < (gifs[category]!!.size - 1)
    }

    private fun getNextGifFromCache(category: Int) {
        val gif = gifs[category]!![++currentGifIndexes[category]]
        gifUrl.postValue(gif.first)
        description.postValue(gif.second)
    }

    private fun buildRequest(category: Int): Request {
        val gifCategory = when (category) {
            0 -> "latest"
            1 -> "top"
            else -> "hot"
        }

        val urlRequest =
            Builder().scheme(URL_SCHEME).authority(URL_AUTHORITY).appendPath(gifCategory)
                .appendPath("${gifCurrentPages[category]}")
                .appendQueryParameter("json", "true").build().toString()

        return Request.Builder().url(urlRequest).build()
    }

    private fun parseResponse(response: Response, category: Int) {
        val responseBody = response.body!!.string()
        val jsonResult = JSONTokener(responseBody).nextValue() as JSONObject
        val jsonArray = jsonResult.getJSONArray("result")

        if (jsonArray.length() == 0) {
            gifUrl.postValue("empty")
            description.postValue("")
            return
        }

        var gifReceived = false

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val url = jsonObject.getString("gifURL")
            val desc = jsonObject.getString("description")

            if (url.isNotEmpty() && desc.isNotEmpty()) {
                if (!gifReceived) {
                    gifUrl.postValue(url)
                    description.postValue(desc)
                    ++currentGifIndexes[category]
                    gifReceived = true
                    gifCurrentPages[category]++
                }
                gifs[category]!!.add(Pair(url, desc))
            }
        }
    }

    private fun getNextGifFromNet(category: Int) {
        val request = buildRequest(category)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    parseResponse(response, category)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun clearGif() {
        gifUrl.postValue("")
        description.postValue("")
    }

    companion object {
        private const val URL_SCHEME = "https"
        private const val URL_AUTHORITY = "developerslife.ru"
    }
}