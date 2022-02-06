package com.tinkoff.devyataykin

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tinkoff.devyataykin.databinding.ActivityMainBinding
import com.tinkoff.devyataykin.ui.main.GifRequester
import com.tinkoff.devyataykin.ui.main.SectionsPagerAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gifRequester = GifRequester()
    private val buttonIndexes = mutableListOf(0, 0, 0)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noInternetTextView = binding.noInternetTextView
        val noInternetImageView = binding.noInternetImageView
        val noInternetTextViewRetry = binding.noInternetTextViewRetry

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, gifRequester)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        val nextGifButton = binding.nextButton
        val prevGifButton = binding.backButton

        if (!isOnline(this)) {
            noInternetTextView.visibility = View.VISIBLE
            noInternetImageView.visibility = View.VISIBLE
            noInternetTextViewRetry.visibility = View.VISIBLE
            nextGifButton.visibility = View.GONE
            prevGifButton.visibility = View.GONE
            gifRequester.online = false
        }

        noInternetTextViewRetry.setOnClickListener {
            if (isOnline(this)) {
                noInternetTextView.visibility = View.GONE
                noInternetImageView.visibility = View.GONE
                noInternetTextViewRetry.visibility = View.GONE
                nextGifButton.visibility = View.VISIBLE
                prevGifButton.visibility = View.VISIBLE
                gifRequester.online = true
                gifRequester.getCurrentGif(viewPager.currentItem)
            }
        }

        nextGifButton.setOnClickListener {
            if (!isOnline(this)) {
                gifRequester.clearGif()
                noInternetTextView.visibility = View.VISIBLE
                noInternetImageView.visibility = View.VISIBLE
                noInternetTextViewRetry.visibility = View.VISIBLE
                nextGifButton.visibility = View.GONE
                prevGifButton.visibility = View.GONE
                gifRequester.online = false
            } else {
                gifRequester.getNextGif(viewPager.currentItem)
                if (buttonIndexes[viewPager.currentItem]++ == 0) {
                    prevGifButton.setBackgroundResource(R.drawable.back)
                }
            }
        }

        prevGifButton.setOnClickListener {
            gifRequester.getPrevGif(viewPager.currentItem)
            if ((buttonIndexes[viewPager.currentItem] - 1) == 0) {
                prevGifButton.setBackgroundResource(R.drawable.back_inactive)
            }
            if (buttonIndexes[viewPager.currentItem] > 0) {
                --buttonIndexes[viewPager.currentItem]
            }
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                gifRequester.getCurrentGif(position)
                if (buttonIndexes[position] == 0) {
                    prevGifButton.setBackgroundResource(R.drawable.back_inactive)
                } else {
                    prevGifButton.setBackgroundResource(R.drawable.back)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}


