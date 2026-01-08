package com.weather.app.ui.profile

import android.content.Context
import android.content.Intent
import com.weather.app.databinding.ActivityProfileBinding
import com.weather.app.ui.base.BaseActivity
import com.weather.app.ui.auth.AuthActivity
import com.weather.app.util.UserType

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {

    override fun getViewBinding(): ActivityProfileBinding {
        return ActivityProfileBinding.inflate(layoutInflater)
    }

    override fun getScreenTitle(): String = "User Profile"

    override fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Mock data populating
        binding.tvUsername.text = "Student User"
        binding.tvEmail.text = "student@university.edu"
        binding.tvUserType.text = "Type: ${UserType.REGISTERED.name}"

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
            finishAffinity()
        }
    }

    override fun setupObservers() {
        // Observe ViewModel if needed
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}
