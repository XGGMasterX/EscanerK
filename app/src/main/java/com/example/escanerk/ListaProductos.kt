package com.example.escanerk

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.escanerk.databinding.ActivityMainBinding

class ListaProductos : AppCompatActivity(){



     override fun onCreate(savedInstanceState: Bundle?){
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)



             fun cerrarLista(view: View){
                 val intent = Intent(this,MainActivity::class.java).apply {

                 }
                 startActivity(intent)
             }
         }
     }

