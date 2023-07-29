package com.example.escanerk

import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class Escaner : AppCompatActivity(){
     private var flash : Boolean = false
     fun initScanner(flash : Boolean,integrator: IntentIntegrator) {
             this.flash = flash
             integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13)
             integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_8)
             integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
             integrator.setTorchEnabled(flash)
             integrator.initiateScan()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun Flash(c : CameraManager,flash: Boolean,integrator: IntentIntegrator): IntentIntegrator {
        var cameraId: Array<String>? = arrayOf("")
        try {
            cameraId?.set(0, c.cameraIdList[0])
            c.setTorchMode(cameraId?.get(0).toString(), flash)
            return integrator
        } catch (e: Exception) {
            e.printStackTrace()
            return integrator
        }
    }
}