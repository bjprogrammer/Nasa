package com.image.nasa.home.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.image.nasa.R
import com.image.nasa.databinding.FragmentImageBinding
import com.image.nasa.utils.Constants

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding
    private lateinit var url: String
    private lateinit var activity: Activity

    companion object {
        fun newInstance(url: String): DetailFragment {
            val detailFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putString(Constants.IMAGE_DATA, url)
            detailFragment.arguments = bundle
            return detailFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = if (context is Activity)
            context
        else
            this.getActivity()!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments!!.containsKey(Constants.IMAGE_DATA)) {
                url = arguments!!.getString(Constants.IMAGE_DATA)!!
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)
        binding.imageUrl = url
        binding.activity = activity as HomeActivity?
        return binding.getRoot()
    }
}