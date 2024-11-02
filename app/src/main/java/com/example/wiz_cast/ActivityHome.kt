package com.example.wiz_cast

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wiz_cast.Utils.PreferencesHelper
import com.example.wiz_cast.databinding.ActivityHomeBinding
import java.util.Locale

class ActivityHome : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbar) // set toolbar
            navView.bringToFront() // bring navigation view to front
        }
        navController = findNavController(R.id.fragment)
        binding.navView.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // to avoid the overlap of the menu bar
//        navController.addOnDestinationChangedListener { _, _, _ ->
//            binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.menu_open_24)
//        }

        // Set the locale on app start
        val preferencesHelper = PreferencesHelper(this)
        val language = preferencesHelper.getLanguage()
        setAppLocale(this, language)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    // Helper function to set the locale
    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

