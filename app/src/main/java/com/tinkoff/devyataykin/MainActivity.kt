package com.tinkoff.devyataykin

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tinkoff.devyataykin.ui.main.SectionsPagerAdapter
import com.tinkoff.devyataykin.databinding.ActivityMainBinding
import com.tinkoff.devyataykin.ui.main.GifRequester

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gifRequester = GifRequester()
    private val buttonIndexes = mutableListOf(0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, gifRequester)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        val nextGifButton = binding.nextButton
        val prevGifButton = binding.backButton

        nextGifButton.setOnClickListener {
            gifRequester.getGif(viewPager.currentItem)
            if(buttonIndexes[viewPager.currentItem]++ == 0) {
                prevGifButton.setBackgroundResource(R.drawable.back)
            }
        }

        prevGifButton.setOnClickListener {
            gifRequester.getPrev(viewPager.currentItem)
            if((buttonIndexes[viewPager.currentItem] - 1) == 0) {
                prevGifButton.setBackgroundResource(R.drawable.back_inactive)
            }
            if(buttonIndexes[viewPager.currentItem] > 0) {
                --buttonIndexes[viewPager.currentItem]
            }
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                gifRequester.getCurrentGif(position)
                if(buttonIndexes[position] == 0) {
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
}


