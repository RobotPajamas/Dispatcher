package com.robotpajamas.android.dispatcher.ui

import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
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
