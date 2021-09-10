package com.example.tarkovcompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_trader_screen.view.*

class TradersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traders)
        supportActionBar!!.title = "Торговцы"
        Resources.CURRENT_ACTIVITY = this

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view_2)
        val navController = findNavController(R.id.fragment_bottom_navhost)
        bottomNavigationView.setupWithNavController(navController)
    }
}