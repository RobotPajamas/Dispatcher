package com.robotpajamas.android.dispatcher.ui

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.robotpajamas.android.dispatcher.R
import com.robotpajamas.dispatcher.SerialDispatcher
import com.robotpajamas.android.dispatcher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val dispatcher = SerialDispatcher()
    private val vm by lazy { MainViewModel() }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
    }
}
