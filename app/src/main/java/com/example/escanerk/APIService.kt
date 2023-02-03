package com.example.escanerk

import kotlin.Throws
import kotlin.jvm.JvmStatic
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

object APIService {
    @Throws(MalformedURLException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val hiloOne = Thread()
        try {

            //https://api.idealonline.com.ar/articulos/get_single.php?empresa=xxxxxx&numero=7790040872202
            val url = URL("https://api.chucknorris.io/jokes/random")
            val conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.connect()
            val responseCode = conn.responseCode
            if (responseCode != 200) {
                throw RuntimeException("ocurrio un error del tipo: $responseCode")
            } else {
                val informationString = StringBuilder()
                val scanner = Scanner(url.openStream())
                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine())
                }
                hiloOne.start()
                scanner.close()

                //System.out.println(informationString);
                //JSONArray jsonArray = new JSONArray(informationString.toString());
                val jsonObject = JSONObject(informationString.toString())
                println(jsonObject.getString("icon_url"))
                println(jsonObject.getString("url"))
                println(jsonObject.getString("created_at"))
                println(jsonObject.getString("id"))
                hiloOne.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}