package com.example.escanerk

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Listar : AppCompatActivity() {
    private val objEscaner : Escaner = Escaner()
    private val objPut = PutAPI()
    private var flash = false
    private var firstScann = false
    private var scannResult = false
    private var integrator :IntentIntegrator? = null
    private var btnUp : Button? = null
    private var btnDown : Button? = null
    private var btnAdd : Button? = null
    private var btnEscanear : Button? = null
    private var btnFlash : Button? = null
    private var textNombre : TextView? = null
    private var textCode : TextView? = null
    private var textCantidad : TextView? = null

    private fun getTextCode(): TextView? {
        return textCode;
    }
    private fun getTextNombre(): TextView? {
        return textNombre;
    }
    private fun getTextCantidad(): TextView? {
        return textCantidad;
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listar)

        btnUp  = findViewById(R.id.btnUpListar)
        btnDown  = findViewById(R.id.btnDownListar)
        btnAdd = findViewById(R.id.btnAddListar)
        btnEscanear = findViewById(R.id.btnEscanearListar)
        btnFlash = findViewById(R.id.btnFlashListar)
        textNombre = findViewById(R.id.TextNombreListar)
        textCode = findViewById(R.id.TextCodeListar)
        textCantidad  = findViewById(R.id.TextCantidadListar)
        //blue 0 , 182 , 255
        //red  252,99,134
        //yellow 229,218,1

        textNombre!!.setBackgroundColor(Color.WHITE)
        textNombre!!.setTextColor(Color.BLACK)
        textNombre!!.setTextSize(1, 20.0F);
        textCode!!.setBackgroundColor(Color.WHITE)
        textCode!!.setTextColor(Color.BLACK)
        textCode!!.setTextSize(1, 20.0F);
        textCantidad!!.setBackgroundColor(Color.WHITE)
        textCantidad!!.setTextColor(Color.BLACK)
        textCantidad!!.setTextSize(1, 20.0F);

        //Cuando se presione el Botton = Flash se ejecuta:
        btnFlash!!.setTextColor(Color.rgb(0 , 182 , 255))
        btnFlash!!.setOnClickListener{

            if (!flash) {
                btnFlash!!.setBackgroundColor(Color.rgb(0 , 182 , 255))
                btnFlash!!.setTextColor(Color.WHITE)
                flash = true
                integrator = IntentIntegrator(this)
                integrator = objEscaner.Flash(camera, flash, integrator!!)
            }
            else if(flash){
                //.rgb(187, 134, 252)
                btnFlash!!.setBackgroundColor(Color.WHITE)
                btnFlash!!.setTextColor(Color.rgb(0 , 182 , 255))
                flash = false
                integrator = IntentIntegrator(this)
                integrator = objEscaner.Flash(camera, flash, integrator!!)
            }

        }

        //Cuando se presiones el Botton = Escanear se ejecuta:
        btnEscanear!!.setTextColor(Color.rgb(0 , 182 , 255))
        btnEscanear!!.setOnClickListener{

            integrator = IntentIntegrator(this)
            if(firstScann)
            {
                alertaDeseaEscannear()
            }
            else{
                objEscaner.initScanner(flash, integrator!!)
            }
        }
        btnUp!!.setTextColor(Color.rgb(0 , 182 , 255))
        btnUp!!.setOnClickListener{
            if((Integer.parseInt(cantidad)) < 100 && (Integer.parseInt(cantidad)) >= 0)
            {
                upCantidad(textCantidad!!)
            }
            else if((Integer.parseInt(cantidad)) < 0)
            {
                this.cantidad = "0";
                textCantidad!!.text = defCantidad + cantidad
                objPut.setCantidad(this.cantidad)
                println(textCantidad!!.text)
            }
        }
        btnDown!!.setTextColor(Color.rgb(0 , 182 , 255))
        btnDown!!.setOnClickListener{
            if((Integer.parseInt(cantidad)) > 0)
            {
                downCantidad(textCantidad!!)
            }
            else if((Integer.parseInt(cantidad)) <= 0)
            {
                this.cantidad = "-99999";
                textCantidad!!.text = defCantidad + cantidad
                objPut.setCantidad(this.cantidad)
                println(textCantidad!!.text)
            }
        }
        btnAdd!!.setTextColor(Color.rgb(252,99,134))
        btnAdd!!.setOnClickListener{
            if(firstScann && Integer.parseInt(cantidad) != 0 && scannResult)
            {
                println("====================ANTES DE PUT====================")
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        objPut.Start()
                        println("====================FINAL DE PUT====================")
                    }//android.os.NetworkOnMainThreadException
                }
                alertaEnviado()
                resetValues(textCantidad!!)
            }
            else if(firstScann == false){
                alertaScanneoMissing(btnFlash!!)
            }
            else if(Integer.parseInt(cantidad) <= 0)
            {
                alertaCantidadNull()
            }
            else if(scannResult == false)
            {
                alertaErrorDeEscanneo()
            }
        }


        val ListarI = Intent(this, Listar::class.java)
        val MainActivityI = Intent(this, MainActivity::class.java)
        val StockI = Intent(this, Stock::class.java)

        val btnWeb: Button = findViewById(R.id.btnWebListar)
        val btnStock: Button = findViewById(R.id.btnStockListar)
        val btnListar: Button = findViewById(R.id.btnListar)

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

    private fun getIsScann(b : Boolean){
        this.firstScann = b;
    }



    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertaScanneoMissing(btnFlash : Button){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("Falta Realizar Escanneo")
        builder.setPositiveButton("Escanear", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            objEscaner.initScanner(flash, integrator!!)
        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Envio Cancelado",Toast.LENGTH_LONG).show()
            var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            //.rgb(187, 134, 252)
            btnFlash.setBackgroundColor(Color.WHITE)
            btnFlash.setTextColor(Color.rgb(0 , 182 , 255))
            flash = false
            integrator = objEscaner.Flash(camera,flash, integrator!!)
        })

        builder.show()
    }

    private fun alertaCantidadNull(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("No Declaro Cantidad De Productos")
        builder.setPositiveButton("Aceptar", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Agregando",Toast.LENGTH_LONG).show()

        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Envio Cancelado",Toast.LENGTH_LONG).show()
        })

        builder.show()
    }

    private fun alertaEnviado(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exelente!")
        builder.setMessage("Se ha cargado correctamente su producto")

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertaDeseaEscannear()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("Esta Por Realizar Otro Escanneo , Los Datos Actuales Se Perderan")
        builder.setPositiveButton("Escanear", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            objEscaner.initScanner(flash, integrator!!)
        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Escanneo Cancelado",Toast.LENGTH_LONG).show()
            var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            integrator = objEscaner.Flash(camera,flash, integrator!!)
        })

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertaErrorDeEscanneo(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("Ah Ocurrido Un Error En El Escanneo , Debe Volver A Realizarlo , De Lo Contrario No Podra Continuar")
        builder.setPositiveButton("Escannear", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            objEscaner.initScanner(flash, integrator!!)
        })
        builder.setNegativeButton("Cancelar", DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Escanneo Cancelado",Toast.LENGTH_LONG).show()
            var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            integrator = objEscaner.Flash(camera,flash, integrator!!)
        })

        builder.show()
    }


    //Iniciando el Scanner

    private var nombre = "null"
    private var producto = "null"

    private var defProducto = ""

    private fun setProducto(nombre :String , producto : String,textNombre:TextView,textCode:TextView,textCantidad:TextView){
        this.producto = producto
        this.nombre = nombre


        textNombre.text = this.nombre
        textCode.text = defProducto + this.producto

        println(textNombre.text)
        println(textCode.text)
        println(textCantidad.text)

        if(nombre == "null")
        {
            scannResult = false
        }
        else if(nombre != "null")
        {
            scannResult = true
        }
    }
    // #6200EE "@color/purple_500"
    // private var buttonColorPurpel = "#6200EE"


    //Encender Flash

    private var ScannCod = "null"

    private fun setScann(resultado: IntentResult)
    {
        this.ScannCod = resultado.contents.toString()
    }

    private fun getScann():String{
        return this.ScannCod
    }

    private var cantidad = "0"
    private var defCantidad = ""

    private fun upCantidad(textCantidad:TextView){
        if(cantidad != "null")
        {
            this.cantidad =  (Integer.parseInt(this.cantidad) + 1).toString()
            textCantidad.text = defCantidad + cantidad
            objPut.setCantidad(this.cantidad)
            println(textCantidad.text)
        }
        return
    }

    private fun downCantidad(textCantidad: TextView){
        if(cantidad != "null")
        {
            this.cantidad = (Integer.parseInt(this.cantidad) - 1).toString()
            textCantidad.text = defCantidad + cantidad
            objPut.setCantidad(this.cantidad)
            println(textCantidad.text)
        }
        return
    }

    private fun resetValues(textCantidad: TextView){
        textCantidad.text = defCantidad + "0"
        cantidad = "0"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var camera: CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)

        if(result != null)
        {
            if(result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this,"El Valor Escaneado Es: ${result.contents}", Toast.LENGTH_SHORT).show()
                resetValues(textCantidad!!)
                setScann(result)
                var api = ConsumoAPI.getInstance()
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        println("====================ANTES DE OBJETO====================")
                        api.searchByCode(result)
                    }//android.os.NetworkOnMainThreadException
                    getIsScann(true)
                    setProducto(api.getProductoNombre(),api.getProductoCodigo(),textNombre!!,textCode!!,textCantidad!!)
                    objPut.setCodigo(producto)
                    objPut.setCantidad("0")
                }
            }
            if (!flash) {
                //.rgb(187, 134, 252)
                btnFlash!!.setBackgroundColor(Color.WHITE)
                btnFlash!!.setTextColor(Color.rgb(0, 182, 255))
                integrator = objEscaner.Flash(camera, flash, integrator!!)
            }
            else if(flash){
                //.rgb(187, 134, 252)
                btnFlash!!.setBackgroundColor(Color.rgb(0 , 182 , 255))
                btnFlash!!.setTextColor(Color.WHITE)
                integrator = objEscaner.Flash(camera, flash, integrator!!)
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}