package com.example.jokeprovider

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import java.lang.StringBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is authorized inside the victim app, which is the right package to query
        val contenturi = Uri.parse("content://com.example.victimapp.MyProvider/joke")

        // Define the tables and data (columns/rows) you want to retrieve (author and joke)
        val projection = arrayOf("author", "joke")

        // Specify the selection condition and arguments
        val selection = "author = ?"
        val selectionArgs = arrayOf("elosiouk")

        // Based on first code example here:
        // https://developer.android.com/guide/topics/providers/content-provider-basics?hl=it#kotlin
        val cursor = contentResolver.query(contenturi, projection, selection, selectionArgs, null)
        Log.i("MOBIOTSEC", "Cursor count: ${cursor?.count}")

        // Check the cursor count before iterating over it
        if (cursor != null && cursor.count > 0) {
            try {
                // Use a standard stringBuilder to do the thing
                val flag = StringBuilder()
                val authorColumnIndex = cursor.getColumnIndex("author")
                val jokeColumnIndex = cursor.getColumnIndex("joke")
                Log.i("MOBIOTSEC", "Retrieved cursor column index: '$authorColumnIndex', '$jokeColumnIndex'")

                while (cursor.moveToNext()) {
                    // Get all columns data
                    val author = cursor.getString(authorColumnIndex)
                    val joke = cursor.getString(jokeColumnIndex)
                    Log.i("MOBIOTSEC", "Author: '$author', Joke: '$joke'")

                    if (author.equals("elosiouk")) {
                        flag.append(joke)
                        Log.i("MOBIOTSEC", "Flag composing with jokes: '$flag'")
                    }
                }
                // Close the cursor when you are finished with it
                cursor.close()

                // Extract the flag from the jokes
                val extractedFlag = extractFlagFromJokes(flag.toString())
                Log.i("MOBIOTSEC", "Flag extracted: '$extractedFlag'")
            } catch (e: Exception) {
                Log.e("MOBIOTSEC", "Error retrieving data from cursor: ${e.message}")
            }
        } else {
            Log.e("MOBIOTSEC", "Error retrieving cursor data")
        }
    }

    private fun extractFlagFromJokes(jokes: String): String {
        val regex = Regex("FLAG\\{(.+?)\\}")
        val matchResult = regex.find(jokes)
        return matchResult?.groupValues?.get(1) ?: "Flag not found"
    }
}

