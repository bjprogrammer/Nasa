package com.image.nasa.home.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.image.nasa.R
import com.image.nasa.model.Media
import com.image.nasa.utils.ConnectivityReceiver
import com.image.nasa.utils.ConnectivityReceiver.ConnectivityReceiverListener
import com.image.nasa.utils.MediaType
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity(), ConnectivityReceiverListener {
    private lateinit var intentFilter: IntentFilter
    private lateinit var receiver: ConnectivityReceiver
    private var isConnected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_main)
        intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        receiver = ConnectivityReceiver()

        addFragment()
    }

    private fun addFragment() {
        val ft = supportFragmentManager.beginTransaction()
        var homeFragment = supportFragmentManager.findFragmentByTag("data") as HomeFragment?

        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }

        if (!homeFragment.isAdded) {
            ft.add(R.id.frameLayout, homeFragment, "data")
            ft.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    //Checking internet isConnected using broadcast receiver
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (this.isConnected != isConnected) {
            if (isConnected) {
                Toasty.success(this, getString(R.string.connected_internet), Toast.LENGTH_SHORT, true).show()
            } else {
                Toasty.error(applicationContext, getString(R.string.not_connected_internet), Toast.LENGTH_LONG, true).show()
            }
        }
        this.isConnected = isConnected

        EventBus.getDefault().post(this.isConnected)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) { }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(media: Media?) {
        media?.let { openImage(it) }
    }

    fun openImage(media: Media) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (media.mediaType == MediaType.image.toString()) {
            val fragment: Fragment = DetailFragment.newInstance(media.url)

            if (!fragment.isAdded) {
                fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left, R.anim.pull_in_left, R.anim.push_out_right)
                fragmentTransaction.replace(R.id.frameLayout, fragment, media.mediaType).addToBackStack(null)
                fragmentTransaction.commit()
            }
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(media.url)))
        }
    }

    fun closeImageFragment() {
        onBackPressed()
    }

    override fun onBackPressed() {
        val detailFragment = supportFragmentManager.findFragmentByTag(MediaType.image.toString())
        if (detailFragment != null) {
            if (detailFragment.isVisible)
                supportFragmentManager.popBackStack()
            else
                super.onBackPressed()

        } else
            super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
}