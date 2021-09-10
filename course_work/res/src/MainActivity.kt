package com.example.tarkovcompanion

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

//    private lateinit var sPref : SharedPreferences
//    private val CURRENT_USER_ITEM_LIST_FILE_PATH = ""
//    private val CURRENT_TRADER_ITEM_LIST_FILE_PATH = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity_main)
        Resources.CURRENT_ACTIVITY = this

        CoroutineScope(Dispatchers.IO).launch {

            Database.connect()
            Log.d("DByyy", "Успешное содеинение с БД")

            Resources.ITEM_RUBLES_ID = Database.getRublesID()
            Resources.ITEM_DOLLARS_ID = Database.getDollarsID()
            Resources.ITEM_EUROS_ID = Database.getEurosID()
        }

    }

}