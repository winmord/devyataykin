package com.tinkoff.devyataykin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.tinkoff.devyataykin.R
import com.tinkoff.devyataykin.databinding.FragmentMainBinding


class PlaceholderFragment(private val gifRequester: GifRequester) : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(
            this,
            PageViewModelFactory(gifRequester)
        )[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val textView: TextView = binding.sectionLabel
        pageViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val imageView = binding.gifImageView
        imageView.clipToOutline = true

        val circularProgressDrawable = CircularProgressDrawable(this.requireContext())
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        pageViewModel.gifUrl.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                Glide.with(this).load(it).placeholder(circularProgressDrawable).centerCrop()
                    .into(imageView)
            } else {
                Glide.with(this).load(R.color.white).into(imageView)
            }
        }

        return root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int, gifRequester: GifRequester): PlaceholderFragment {
            return PlaceholderFragment(gifRequester).apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}