package com.tinkoff.devyataykin.ui.main

import android.net.Uri.Builder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tinkoff.devyataykin.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.*

class GifRequester {
    private val client: OkHttpClient = OkHttpClient()
    val gifUrl = MutableLiveData<String?>()
    val description = MutableLiveData<String?>()

    private var currentGifIndexes = mutableListOf(-1, -1, -1)
    private val gifCurrentPages = mutableListOf(0, 0, 0)
    val gifs: MutableMap<Int, MutableList<Pair<String, String>>> = mutableMapOf()

    init {
        gifs[0] = mutableListOf()
        gifs[1] = mutableListOf()
        gifs[2] = mutableListOf()

        getGif(0)
    }

    fun getCurrentGif(category: Int) {
        gifUrl.postValue("R.drawable.loading")
        description.postValue("")
        if (gifs[category]!!.isNotEmpty() && currentGifIndexes[category] >= 0) {
            val gif = gifs[category]!![currentGifIndexes[category]]
            gifUrl.postValue(gif.first)
            description.postValue(gif.second)
        } else {
            getGif(category)
        }
    }

    fun getPrev(category: Int) {
        if (gifs[category]!!.isNotEmpty() && currentGifIndexes[category] > 0) {
            val gif = gifs[category]!![--currentGifIndexes[category]]
            gifUrl.postValue(gif.first)
            description.postValue(gif.second)
        }
    }

    fun getGif(category: Int) {
        if (currentGifIndexes[category] < gifs[category]!!.size - 1) {
            val gif = gifs[category]!![++currentGifIndexes[category]]
            gifUrl.postValue(gif.first)
            description.postValue(gif.second)
        } else {
            val gifCategory = when (category) {
                0 -> "latest"
                1 -> "top"
                else -> "hot"
            }

            val urlRequest =
                Builder().scheme(URL_SCHEME).authority(URL_AUTHORITY).appendPath(gifCategory)
                    .appendPath("${gifCurrentPages[category]++}")
                    .appendQueryParameter("json", "true").build().toString()

            val request = Request.Builder().url(urlRequest).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body!!.string()
                        val jsonResult = JSONTokener(responseBody).nextValue() as JSONObject
                        val jsonArray = jsonResult.getJSONArray("result")

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val url = jsonObject.getString("gifURL")
                            val desc = jsonObject.getString("description")

                            if (i == 0) {
                                gifUrl.postValue(url)
                                description.postValue(desc)
                                ++currentGifIndexes[category]
                            }

                            if(url.isNotEmpty() && desc.isNotEmpty()) {
                                gifs[category]!!.add(Pair(url, desc))
                            }
                        }
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