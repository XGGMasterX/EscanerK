package com.example.escanerk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class ListaProductos : AppCompatActivity() {

    private var api : ConsumoAPI?  = null
    private var listProductos : ArrayList<String>? = null
    private var listProductosImg : Array<String> = arrayOf("NULL")
    private var listProductosCodigo : Array<String>  = arrayOf("NULL")
    private var listProductosNombre : Array<String> = arrayOf("NULL")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listadeproductos)

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
            val i = Intent(this, Listar::class.java)

            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    startActivity(StockI)

                }//android.os.NetworkOnMainThreadException
            }
        }
        btnListar.setOnClickListener {
            val i = Intent(this, Stock::class.java)

            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    startActivity(ListarI)

                }//android.os.NetworkOnMainThreadException
            }
        }
    }

    fun setApi(consumoApi:ConsumoAPI){
        this.api = consumoApi
    }


    fun createListImage(img: String){
        if(img != null && img!= "0") {
            val imagen = "idealonline.com.ar/supermercado/assets/img/$img"
            listProductosImg += imagen
        }
        readImagen()
    }

    fun createListNombre(nam:String){
        if(nam != null && nam!= "0") {
            listProductosNombre += nam
        }
        readNombre()
    }

    fun createListCode(cod:String){
        if(cod != null && cod!= "0") {
            listProductosCodigo += cod
        }
        readCode()
    }

    fun readImagen(){
        var i = 0
        for( i : String in listProductosImg){
            if(i != "NULL"){
                println(i+"1")
            }
        }
    }
    fun readNombre(){
        var n = 0
        for( i : String in listProductosNombre){
            if(i != "NULL"){
                println(i+"1")
            }
        }
    }
    fun readCode(){
        var n = 0
        for( i : String in listProductosCodigo){
            if(i != "NULL"){
                println(i+"1")
            }
        }
    }
}
