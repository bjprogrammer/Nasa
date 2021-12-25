package com.image.nasa.home.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ProgressBar
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.image.nasa.R
import com.image.nasa.databinding.FragmentMainBinding
import com.image.nasa.home.viewmodel.HomeViewModel
import com.image.nasa.model.Media
import com.image.nasa.utils.Status
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: View
    private lateinit var dialog: DatePickerDialog
    private lateinit var actionSet: Group

    private var isLoading = true
    private var isConnected = true
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = homeViewModel

        attachViews()
        attachObserver()
        getData()

        return binding.getRoot()
    }

    private fun attachViews() {
        progressBar = binding.progressBar
        emptyView = binding.emptyView
        actionSet = binding.actionSet
        binding.desc.movementMethod = ScrollingMovementMethod()
    }

    private fun attachObserver() {
        dialog = DatePickerDialog(context!!, OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            getDataForSelectedDate(year.toString() + "-"
                    + (if (month < 10) "0$month" else month) + "-"
                    + if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth)
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis

        homeViewModel.observeChangeDate().observe(viewLifecycleOwner, Observer {
            aBoolean: Boolean? -> dialog.show()
        })

        homeViewModel.observeUserAction().observe(viewLifecycleOwner, Observer {
            media: Media? -> EventBus.getDefault().post(media)
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: Boolean) {
        if (isConnected != result) {
            isConnected = result
            if (isLoading && isConnected)
                getData()
        }
    }

    fun getData(){
        getDataForSelectedDate(null);
    }


    fun getDataForSelectedDate(date:String?){
        progressBar.visibility = View.VISIBLE

        homeViewModel.getDataByDate(date).observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { responseResource ->

                    progressBar.visibility= View.GONE
                    if(responseResource.status == Status.ERROR){
                        if(isConnected)
                            isLoading = false

                        emptyView.visibility == View.VISIBLE
                    }else {
                        binding.response = responseResource.data
                        isLoading = false
                        actionSet.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    }
                })
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.clearActions()
    }
}