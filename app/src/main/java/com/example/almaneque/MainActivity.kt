package com.example.almaneque

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Elimina el código de cargar el fragment manualmente
        // Ya no es necesario porque el Navigation Component lo manejará
    }
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }
}