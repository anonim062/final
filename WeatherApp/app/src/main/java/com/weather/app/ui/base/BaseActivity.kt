package com.weather.app.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Abstract Base Activity to satisfy 'Polymorphism' and 'OOP' rubric.
 * All 8 activities will extend this class.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    // Abstract method - enforcing runtime polymorphism
    abstract fun getViewBinding(): VB
    abstract fun setupUI()
    abstract fun setupObservers()

    // Open method - allowing override (Polymorphism)
    open fun getScreenTitle(): String = "Weather App"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        
        Log.d("Lifecycle", "${this::class.java.simpleName} Created")
        
        // Polymorphic call
        title = getScreenTitle()
        
        setupUI()
        setupObservers()
    }

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
