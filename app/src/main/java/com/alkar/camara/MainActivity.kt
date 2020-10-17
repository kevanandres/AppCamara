package com.alkar.camara

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val FILE_NAME = "photo.jpg"
private  const val REQUEST_CODE=42
private lateinit var photoFile: File

class MainActivity : AppCompatActivity() {
    private val REQUEST_GALERY=1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        abreGaleria_Click()

        record_video.setOnClickListener{
            var i = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(i,101)
        }

        btnTakePicture.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            val fileProvider = FileProvider.getUriForFile(this,"com.alkar.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager)!=null){
                startActivityForResult(takePictureIntent,REQUEST_CODE)
            }else{
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //detectamos cuando se pulse el boton para abrir la galeria
    private fun abreGaleria_Click(){
        btnGaleria.setOnClickListener(){
            //verificamos la version de android instala en el telefono
            //si es igual o mayor que marshmello
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                //preguntamos si tiene permisos
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //pedir permiso al usuario
                    val permisoArchivo = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permisoArchivo,REQUEST_GALERY)
                }else{
                    //si tiene permiso
                    muestraGaleria()
                }
            }else{
                //tiene version de lollipop hacia abajo por default tiene permiso
                muestraGaleria()
            }
        }
    }

    //abre la ventana donde se muestra la galeria de fotos
    private fun muestraGaleria(){
        //Todo muestra galeria de imagenes
        val intentGaleria = Intent(Intent.ACTION_PICK)
        intentGaleria.type = "image/*"
        startActivityForResult(intentGaleria,REQUEST_GALERY)
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALERY){
            imageView.setImageURI(data?.data)
        }
        if (requestCode== REQUEST_CODE && resultCode==Activity.RESULT_OK){
            //val takenImage=data?.extras?.get("data") as Bitmap
            val takenImage=BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView.setImageBitmap(takenImage)
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101){
            video_view.setVideoURI(data?.data)
            video_view.start()
        }
    }

    //si el usuario dio permiso a la aplicacion
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_GALERY -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    muestraGaleria()
                else
                    Toast.makeText(applicationContext,"No puedes acceder a tus imagenes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}