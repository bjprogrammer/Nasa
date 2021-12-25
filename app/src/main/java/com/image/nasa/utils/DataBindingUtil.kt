package com.image.nasa.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.image.nasa.R
import com.wang.avi.AVLoadingIndicatorView

@BindingAdapter("bind:url", "bind:spinner")
fun loadImage(view: ImageView, url: String?, spinner: AVLoadingIndicatorView) {
    if(url != null) {
        spinner.show()
        spinner.visibility = View.VISIBLE

        Glide.with(view.context)
                .load(url)
                .thumbnail(0.1f)
                .placeholder(R.drawable.pexels)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        spinner.hide()
                        spinner.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        spinner.hide()
                        spinner.visibility = View.GONE
                        return false
                    }
                })
                .into(view)
    }
}

@BindingAdapter("bind:image")
fun loadDrawable(view: ImageView, drawable: Int) {
    view.setImageResource(drawable)
}
