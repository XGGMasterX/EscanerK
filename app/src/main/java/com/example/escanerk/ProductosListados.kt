package com.example.escanerk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProductosListados : AppCompatActivity(){
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.productoslistados)

        val ListarI = Intent(this, Listar::class.java)
        val MainActivityI = Intent(this, MainActivity::class.java)
        val StockI = Intent(this, Stock::class.java)

        val btnWeb: Button = findViewById(R.id.btnWebProductosListados)
        val btnStock: Button = findViewById(R.id.btnStockProductosListados)
        val btnListar: Button = findViewById(R.id.btnListarProductosListados)

        btnWeb.setOnClickListener {

            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    startActivity(MainActivityI)

                }//android.os.NetworkOnMainThreadException
            }
        }
        btnStock.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    startActivity(StockI)

                }//android.os.NetworkOnMainThreadException
            }
        }
        btnListar.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    startActivity(ListarI)

                }//android.os.NetworkOnMainThreadException
            }
        }
    }
}