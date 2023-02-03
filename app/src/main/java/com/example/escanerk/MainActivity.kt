package com.example.escanerk

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.escanerk.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection



class MainActivity : AppCompatActivity(){



    private val objPut = PutAPI()
    private var flash = false
    private lateinit var binding:ActivityMainBinding
    private var firstScann = false
    private var scannResult = false
    private var thisIntegrator = IntentIntegrator(this)



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //blue 0 , 182 , 255
        //red  252,99,134
        //yellow 229,218,1

        binding.TextNombre.setBackgroundColor(Color.WHITE)
        binding.TextNombre.setTextColor(Color.rgb(229,218,1))
        binding.TextCode.setBackgroundColor(Color.WHITE)
        binding.TextCode.setTextColor(Color.rgb(229,218,1))
        binding.TextCantidad.setBackgroundColor(Color.WHITE)
        binding.TextCantidad.setTextColor(Color.rgb(229,218,1))

        //Cuando se presione el Botton = Flash se ejecuta:
        binding.btnLigth.setTextColor(Color.rgb(0 , 182 , 255))
        binding.btnLigth.setOnClickListener{
            thisIntegrator = Flash(camera)

        }

        //Cuando se presiones el Botton = Escanear se ejecuta:
        binding.btnScanner.setTextColor(Color.rgb(0 , 182 , 255))
        binding.btnScanner.setOnClickListener{

            if(firstScann)
            {
                alertaDeseaEscannear()
            }
            else{
                initScanner()
            }
        }
        binding.up.setTextColor(Color.rgb(0 , 182 , 255))
        binding.up.setOnClickListener{
            if((Integer.parseInt(cantidad)) < 100)
            {
                upCantidad()
            }
        }
        binding.down.setTextColor(Color.rgb(0 , 182 , 255))
        binding.down.setOnClickListener{
            if((Integer.parseInt(cantidad)) > 0)
            {
               downCantidad()
            }
        }
        binding.Put.setTextColor(Color.rgb(252,99,134))
        binding.Put.setOnClickListener{
               if(firstScann && Integer.parseInt(cantidad) > 0 && scannResult)
               {
                   println("====================ANTES DE PUT====================")
                   GlobalScope.launch(Dispatchers.Main) {
                       withContext(Dispatchers.IO) {


                           objPut.Start()
                           println("====================FINAL DE PUT====================")
                       }//android.os.NetworkOnMainThreadException

                   }
                   alertaEnviado()
                   resetValues()
               }
               else if(!firstScann){

                   alertaScanneoMissing()

               }
               else if(Integer.parseInt(cantidad) <= 0)
               {
                   alertaCantidadNull()
               }
               else if(!scannResult)
               {
                   alertaErrorDeEscanneo()
               }
        }

            fun abrirLista(view: View) {

                val intent = Intent(this, ListaProductos::class.java).apply {  }
                startActivity(intent)
            }


    }



    private fun getIsScann(b : Boolean){
        this.firstScann = b;
    }



    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertaScanneoMissing(){
        val builder = AlertDialog.Builder(this)
            builder.setTitle("Atencion !!")
            builder.setMessage("Falta Realizar Escanneo")
        builder.setPositiveButton("Escanear",DialogInterface.OnClickListener{
            dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            initScanner()
        })
        builder.setNegativeButton("Cancelar",DialogInterface.OnClickListener{
            dialog,witch -> Toast.makeText(this,"Envio Cancelado",Toast.LENGTH_LONG).show()
            var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
            thisIntegrator = FlashApagado(camera)
        })

        builder.show()
    }

    private fun alertaCantidadNull(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("No Declaro Cantidad De Productos")
        builder.setPositiveButton("Aceptar",DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Agregando",Toast.LENGTH_LONG).show()

        })
        builder.setNegativeButton("Cancelar",DialogInterface.OnClickListener{
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
        builder.setPositiveButton("Escanear",DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            initScanner()
        })
        builder.setNegativeButton("Cancelar",DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Escanneo Cancelado",Toast.LENGTH_LONG).show()
                var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
                thisIntegrator = FlashApagado(camera)
        })

        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alertaErrorDeEscanneo(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atencion !!")
        builder.setMessage("Ah Ocurrido Un Error En El Escanneo , Debe Volver A Realizarlo , De Lo Contrario No Podra Continuar")
        builder.setPositiveButton("Escannear",DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Realizando Escanneo",Toast.LENGTH_LONG).show()
            initScanner()
        })
        builder.setNegativeButton("Cancelar",DialogInterface.OnClickListener{
                dialog,witch -> Toast.makeText(this,"Escanneo Cancelado",Toast.LENGTH_LONG).show()
                var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
                thisIntegrator = FlashApagado(camera)
        })

        builder.show()
    }


    //Iniciando el Scanner
    private fun initScanner() {
        val integrator = thisIntegrator
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_8)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setTorchEnabled(flash)
        integrator.initiateScan()

    }
    private var nombre = "null"
    private var producto = "null"

    private var defProducto = "Producto: "

    private fun setProducto(nombre :String , producto : String){
        this.producto = producto
        this.nombre = nombre


        binding.TextNombre.text = this.nombre
        binding.TextCode.text = defProducto + this.producto

        println(binding.TextNombre.text)
        println(binding.TextCode.text)
        println(binding.TextCantidad.text)

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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun Flash(c : CameraManager): IntentIntegrator {
        val integrator = IntentIntegrator(this)
        var cameraId: Array<String>? = arrayOf("")


        try {

            if (!flash) {
                binding.btnLigth.setBackgroundColor(Color.rgb(0 , 182 , 255))
                binding.btnLigth.setTextColor(Color.WHITE)
                flash = true
                integrator.setTorchEnabled(flash)
                cameraId?.set(0, c.getCameraIdList()[0])
                c.setTorchMode(cameraId?.get(0).toString(), flash)

                return integrator
            }


            //.rgb(187, 134, 252)



            binding.btnLigth.setBackgroundColor(Color.WHITE)
            binding.btnLigth.setTextColor(Color.rgb(0 , 182 , 255))
            flash = false
            integrator.setTorchEnabled(flash)
            cameraId?.set(0, c.getCameraIdList()[0])
            c.setTorchMode(cameraId?.get(0).toString(), flash)

            return integrator

        } catch (e: Exception) {
            e.printStackTrace()
            return integrator
        }

    }

        @RequiresApi(Build.VERSION_CODES.M)
        private fun FlashApagado(c : CameraManager) : IntentIntegrator {
            val integrator = IntentIntegrator(this)
            var cameraId : Array<String>?   = arrayOf("")


            try {

                //.rgb(187, 134, 252)
                binding.btnLigth.setBackgroundColor(Color.WHITE)
                binding.btnLigth.setTextColor(Color.rgb(0 , 182 , 255))
                flash = false
                integrator.setTorchEnabled(flash)
                cameraId?.set(0, c.getCameraIdList()[0])
                c.setTorchMode(cameraId?.get(0).toString(),flash)

                return integrator

            }


        catch (e : Exception){
            e.printStackTrace()
            return integrator
        }


    }

    private var ScannCod = "null"

    private fun setScann(resultado: IntentResult)
    {
        this.ScannCod = resultado.contents.toString()
    }

    private fun getScann():String{
        return this.ScannCod
    }

    private var cantidad = "0"
    private var defCantidad = "Cantidad: "

    private fun upCantidad(){
        if(cantidad != "null")
        {
            this.cantidad =  (Integer.parseInt(this.cantidad) + 1).toString()
            binding.TextCantidad.text = defCantidad + cantidad
            objPut.setCantidad(this.cantidad)
            println(binding.TextCantidad.text)
        }
        return
    }

    private fun downCantidad(){
        if(cantidad != "null")
        {
            this.cantidad = (Integer.parseInt(this.cantidad) - 1).toString()
            binding.TextCantidad.text = defCantidad + cantidad
            objPut.setCantidad(this.cantidad)
            println(binding.TextCantidad.text)
        }
        return
    }

    private fun resetValues(){
        binding.TextCantidad.text = defCantidad + "0"
        cantidad = "0"
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result != null)
        {
            if(result.contents == null)
            {
                Toast.makeText(this,"Cancelado",Toast.LENGTH_SHORT).show()
                var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
                thisIntegrator = FlashApagado(camera)
            }
            else
            {
                var camera : CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
                thisIntegrator = FlashApagado(camera)
                Toast.makeText(this,"El Valor Escaneado Es: ${result.contents}",Toast.LENGTH_SHORT).show()
                resetValues()
                setScann(result)
                var api = ConsumoAPI()
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.IO) {
                        println("====================ANTES DE OBJETO====================")
                        api.searchByCode(result)
                    }//android.os.NetworkOnMainThreadException
                    getIsScann(true)
                    setProducto(api.getProductoNombre(),api.getProductoCodigo())
                    objPut.setCodigo(producto)
                    objPut.setCantidad("0")
                }



            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)

    }

}

class ConsumoAPI {

    private var codigo = "null"
    private var nombre = "null"


    fun searchByCode(query: IntentResult) {
        val taskk = Objecto()


        val comprobation = taskk.ejecutarTarea(query.contents.toString())
        if(comprobation != null)
        {
            try {
                var jsonArray = comprobation
                var jsonObject = jsonArray.getJSONObject(0)
                println("=============================IMPRIMIENDO=================================")
                this.codigo = jsonObject.getString("codigo")
                this.nombre = jsonObject.getString("nombre")

                println("Codigo: $codigo")
                println("Nombre: $nombre")


            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }

    }

    fun getProductoNombre():String{
        return this.nombre
    }
    fun getProductoCodigo():String{
        return this.codigo
    }
}

private class Objecto : AsyncTask<String, Void, JSONArray>(){
    private var execute = ExecuteTaskThread()


    fun ejecutarTarea(p : String) : JSONArray? {
        val Json = doInBackground(p)
        return Json
    }

    override fun doInBackground(vararg p: String?): JSONArray? {
        if(p[0] != null) {


            val resultado = p[0]

            try {
                if (resultado != null) {
                    execute.getResultado(resultado)
                }
                println("============================EJECUTANDO RUN==========================")
                execute.run()
                println("=============================TRY=================================")


                println("=============================CREANDO JSON=================================")
                var jsonArrays = JSONArray()


                if (!execute.getStateJson()) {
                    println("=============================JSON=================================")
                    jsonArrays = execute.getJson()
                    return jsonArrays
                }


                return null



            } catch (e: Exception) {
                println("==================E============================================")
                e.printStackTrace()
                return null
            }
        }
        return null
    }


    override fun onPostExecute(result: JSONArray?) {
        //TODO
        //TODO
    }
}

class ExecuteTaskThread : Thread() {
    private var resultado = "null"
    private var conn: HttpsURLConnection? = null
    private var jsonArray = JSONArray()
    private var isJsonNull = true

    public fun getStateJson():Boolean{
        return this.isJsonNull
    }

    public fun getJson(): JSONArray {
        println("=======================GET JSON=======================================")
        return this.jsonArray
    }

    private fun setJson(serializado: String) {
        println("========================SET JSON======================================")
        jsonArray = JSONArray(serializado)
        isJsonNull = false
    }

    public fun getResultado(p : String){
        this.resultado = p
    }

    public override fun run() {
        try{
            var responseCode: Int? = null
            //
            val url = URL("https://api.idealonline.com.ar/articulos/get_single.php?empresa=xxxxxx&numero=${resultado}")
            conn = url.openConnection() as HttpsURLConnection
            conn!!.requestMethod = "GET"
            println("=========================CONNECT INITIAL=====================================")
                    conn!!.connect()
                    responseCode = conn!!.responseCode
                    println("=========================CONNECT FINALITIAN====================================")

                    if (responseCode != 200) {
                        throw RuntimeException("ocurrio un error del tipo: $responseCode")
                    } else {
                        println("====================================CONEXION EXITOSA FUNCIONANDO=============================")
                        val informationString = StringBuilder()
                        val scanner = Scanner(url.openStream())
                        while (scanner.hasNext()) {
                            informationString.append(scanner.nextLine())
                        }

                        scanner.close()


                        setJson(informationString.toString())
                        super.run()
                    }



        } catch (e: Exception) {
            println("==================E============================================")
            e.printStackTrace()

        } finally {
            println("===============================FINALLY===============================")

            conn?.disconnect()
            println("===============================DESCONEXION EXITOSA=====================")
        }

    }
}


//Quizas terminar

class PutAPI {

    private var cantidad = "null"
    private var codigo = "null"
    private var url = URL("https://api.idealonline.com.ar/articulos/stock/set.php?sucursal=xxxxxx&codigo=${codigo}&cantidad=${cantidad}")
    private var httpCon : HttpURLConnection = url.openConnection() as HttpURLConnection
    private var objAPI = ConsumoAPI()



    fun Start() {
        try {

            val row = JSONObject()
            row.put("codigo", Integer.parseInt(codigo))
            row.put("cantidad", Integer.parseInt(cantidad))


            //JSON :
            var jsonArrayPut = JSONArray() //Para Mayor Compatibilidad , NO QUITAR
            var formatPutJson = jsonArrayPut.put(0, row)
            var JsonArrayString: String = formatPutJson.toString()






            //Apartir de aca , codigo para el PUT

            //Realizamos Put a URL Conexion:
            url = URL("https://api.idealonline.com.ar/articulos/stock/set_single.php?sucursal=xxxxxx&codigo=${codigo}&cantidad=${cantidad}")
            httpCon  = url.openConnection() as HttpURLConnection
            httpCon.setDoOutput(true)
            httpCon.setRequestMethod("PUT")

            


            val out = OutputStreamWriter(httpCon.getOutputStream())
            out.write(JsonArrayString)
            out.close()
            httpCon.getInputStream()


            //Visualizamos conexion:

            System.out.println(httpCon.responseCode);
            //System.out.println(respuestaEstado)
            System.out.println(JsonArrayString);


        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
              httpCon.disconnect()
        }
    }
    fun setCantidad(c: String) {
        this.cantidad = c
    }

    fun setCodigo(c: String) {
        this.codigo = c
    }
}


