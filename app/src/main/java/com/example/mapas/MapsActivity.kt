package com.example.mapas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mapas.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Para cargar mapa
    private var mMap: GoogleMap? = null
    private lateinit var binding: ActivityMapsBinding

    //Sensor de luz
    private lateinit var mSensorManager: SensorManager
    private lateinit var mSensor: Sensor
    private lateinit var mSensorEventListener: SensorEventListener

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(mSensorEventListener, mSensor,
            SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(mSensorEventListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mSensorEventListener = object : SensorEventListener {
            //Que va a pasar cuando cambie el valor del sensor
            override fun onSensorChanged(event: SensorEvent?) {
                if (mMap != null) {
                    if (event!!.values[0] < 5000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0])
                        mMap!!.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@MapsActivity,
                                R.raw.night_map_style
                            )
                        )
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0])
                        mMap!!.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                this@MapsActivity,
                                R.raw.retro_map_style
                            )
                        )
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Configurar estilo mapa
        mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro_map_style))

        //Para deshabilitar gestos
        // mMap!!.uiSettings.isZoomGesturesEnabled = false

        // Add a marker in Sydney and move the camera
        val jave = LatLng(4.6287, -74.0636)
        val mkr1 = LatLng(4.7287, -74.0636)
        val mkr2 = LatLng(4.5287, -74.0636)
        val plazaSimonBolivar = LatLng(4.59806, -74.0758)
        //Hacer zoom
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(15F))
        //Mover la camara para que enfoque el pin
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(jave))
        //Poner el pin
        mMap!!.addMarker(MarkerOptions().position(jave)
            .title("Marker in Jave")
            .snippet("Hola Adrian")
            .icon(bitmapDescriptorFromVector(this,R.drawable.ic_launcher_foreground)))
        mMap!!.addMarker(MarkerOptions().position(plazaSimonBolivar).title("Plaza Simón Bolivar").snippet("Inicio de construcción: 27 de abril de 1539"))
        mMap!!.addMarker(MarkerOptions().position(mkr1).title("Marker in mkr1").snippet("Hola Adrian"))
        val mkrmkr2 = mMap!!.addMarker(MarkerOptions().position(mkr2).title("Marker in mkr2").snippet("Hola Adrian"))

        //Preguntar a IA como agregar un icono
        //Para convertir un SVG a Bitmapdecriptor, toca crear un método. Preguntar a IA

        // No mostrar
        mkrmkr2!!.isVisible =false

        //Eliminar un punto
        mkrmkr2.remove()

        //Limpiar mapa
        //mMap!!.clear()

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}