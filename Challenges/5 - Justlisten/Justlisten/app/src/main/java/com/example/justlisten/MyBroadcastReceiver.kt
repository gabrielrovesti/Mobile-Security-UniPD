package com.example.justlisten

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MyBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "MOBIOTSEC"

    override fun onReceive(context: Context?, intent: Intent?) {
        //Simply check whether the intent gets called via the correct broadcast call
        val intent_action = intent!!.action
        if (intent_action == "victim.app.FLAG_ANNOUNCEMENT") {
            // Get data from extras and possibly flag
            val bundle = intent!!.extras
            val flag = bundle!!.getString("flag")
            Log.i(TAG, flag!!)
        }
    }

}
