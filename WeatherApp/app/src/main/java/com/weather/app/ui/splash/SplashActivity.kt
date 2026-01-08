package com.weather.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.weather.app.R
import com.weather.app.ui.MainActivity
import com.weather.app.ui.auth.AuthActivity
import com.weather.app.util.PreferencesManager

import com.weather.app.databinding.ActivitySplashBinding
import com.weather.app.ui.base.BaseActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }
    
    override fun getScreenTitle(): String = "Welcome"

    override fun setupUI() {
        // UI is static
    }
    
    override fun setupObservers() {
        // No observers
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // BaseActivity sets content view

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, 1500) // 1.5 seconds delay
    }

    private fun checkAuthAndNavigate() {
        if (PreferencesManager.isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }
}
