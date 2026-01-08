package com.weather.app.ui.auth

import android.content.Context
import android.content.Intent
import com.weather.app.R
import com.weather.app.databinding.ActivityAuthBinding
import com.weather.app.ui.MainActivity
import com.weather.app.ui.base.BaseActivity

class AuthActivity : BaseActivity<ActivityAuthBinding>() {

    override fun getViewBinding(): ActivityAuthBinding = ActivityAuthBinding.inflate(layoutInflater)
    
    override fun getScreenTitle(): String = "Authentication"

    override fun setupUI() {
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
    
    override fun setupObservers() {}

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateToLogin() {
        supportFragmentManager.popBackStack()
    }

    fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AuthActivity::class.java))
        }
    }
}
