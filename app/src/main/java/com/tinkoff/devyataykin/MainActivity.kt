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
        nextGifButton.setOnClickListener {
            gifRequester.getGif(viewPager.currentItem, "")
        }

        val prevGifButton = binding.backButton
        prevGifButton.setOnClickListener {
            gifRequester.getPrev()
        }
    }
}