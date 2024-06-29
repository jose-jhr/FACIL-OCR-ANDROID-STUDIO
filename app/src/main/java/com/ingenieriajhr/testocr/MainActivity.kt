package com.ingenieriajhr.testocr

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ingenieriajhr.testocr.databinding.ActivityMainBinding
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.google.mlkit.vision.text.Text


lateinit var cameraJhr: CameraJhr

lateinit var vb: ActivityMainBinding

lateinit var textRecognizer: TextRecognizer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        vb = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(vb.root)

        //Inicializamos el objeto camara
        cameraJhr = CameraJhr(this)
        //Iniciamos el reconocimiento de texto
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        //adicion el atributo scroll a textConsol
        vb.txtConsola.movementMethod = ScrollingMovementMethod()


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!cameraJhr.ifStartCamera && cameraJhr.allpermissionsGranted()){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    private fun startCameraJhr() {
        //tiempo antes de iniciar el proceso de reconocimiento
        var timeCurrent = System.currentTimeMillis()
        //tiempo de espera entre reconocimiento
        val timeWait = 1000L
        cameraJhr.addlistenerBitmap(object:BitmapResponse{
            override fun bitmapReturn(bitmap: Bitmap?) {

                //si ha pasado 1 segundo entonces, entramos a reconocer el bitmap
                if (System.currentTimeMillis()-timeCurrent >timeWait){
                    //convertimos el bitmap a InputImage
                    val image =  InputImage.fromBitmap(bitmap!!,0)
                    //reconocemos el texto que esta en la imagen
                    textRecognizer.process(image).addOnSuccessListener {
                        //mostramos el resultado en el textView
                       vb.txtConsola.text = it.text
                    }.addOnFailureListener {
                    }
                    timeCurrent = System.currentTimeMillis()
                }

            }
        })

        cameraJhr.initBitmap()
        cameraJhr.start(1,0, vb.cameraPreview,true,false,true)
    }



}