package com.example.quiz

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quiz.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class FileParser {

    public fun parseQuestions(inputStream: InputStream) : JSONArray {
        var arr = arrayListOf<String>()
        var json:String? = null
        try {
            json = inputStream.bufferedReader().use { it.readText() }
            return JSONArray(json)
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
        return JSONArray()
    }
}