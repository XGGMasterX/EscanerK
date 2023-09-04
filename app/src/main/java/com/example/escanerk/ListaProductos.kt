package com.example.escanerk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaProductos : AppCompatActivity() {
    
    private var api : ConsumoAPI = ConsumoAPI.getInstance()
    override fun onResume() {
        super.onResume()
        val productListView: ListView = findViewById(R.id.productListView)
        val productList: MutableList<String> = mutableListOf()

        // Utiliza CustomArrayAdapter en lugar de ArrayAdapter
        val adapter = CustomArrayAdapter(this, android.R.layout.simple_list_item_1, productList)

        productListView.adapter = adapter

        for (nombre in api.getProductListNombre()!!){
            productList.add(nombre)
            println(nombre)
        }

        adapter.notifyDataSetChanged()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listadeproductos)


        val spinner: Spinner = findViewById(R.id.OptionsListaProductos)
        val opciones = listOf("Opción 1", "Opción 2", "Opción 3")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val ListarI = Intent(this, Listar::class.java)
        val MainActivityI = Intent(this, MainActivity::class.java)
        val StockI = Intent(this, Stock::class.java)

        val btnWeb: Button = findViewById(R.id.btnWebListaProductos)
        val btnStock: Button = findViewById(R.id.btnStockListaProductos)
        val btnListar: Button = findViewById(R.id.btnListarListaProductos)

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


class CustomArrayAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.setTextColor(Color.BLACK) 
        return view
    }
}
