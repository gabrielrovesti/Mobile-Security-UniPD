package com.example.justlisten

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Simply call the register receiver class
        val br: BroadcastReceiver = MyBroadcastReceiver()
        this.registerReceiver(br, IntentFilter("victim.app.FLAG_ANNOUNCEMENT"))

    }

}
