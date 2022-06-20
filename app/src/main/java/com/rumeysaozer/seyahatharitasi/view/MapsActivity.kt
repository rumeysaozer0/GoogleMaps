package com.rumeysaozer.seyahatharitasi.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.rumeysaozer.seyahatharitasi.R
import com.rumeysaozer.seyahatharitasi.database.TravelDao
import com.rumeysaozer.seyahatharitasi.database.TravelDatabase
import com.rumeysaozer.seyahatharitasi.databinding.ActivityMapsBinding
import com.rumeysaozer.seyahatharitasi.model.Travel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var resultLauncher: ActivityResultLauncher<String>
    private lateinit var preferences: SharedPreferences
    private var boolean: Boolean? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private lateinit var db: TravelDatabase
    private lateinit var travelDao: TravelDao
    val disposable = CompositeDisposable()
    var placeFromMainActivity : Travel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        preferences = this.getSharedPreferences("com.rumeysaozer.seyahatharitam", MODE_PRIVATE)
        boolean = false
        latitude = 0.0
        longitude = 0.0
        db = Room.databaseBuilder(applicationContext, TravelDatabase::class.java, "Places")
            .build()
        travelDao = db.travelDao()
        binding.save.setOnClickListener {
            val travel = Travel(
                binding.name.text.toString(),
                binding.explanation.text.toString(),
                latitude!!,
                longitude!!
            )
            disposable.add(
                travelDao.insert(travel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }
        binding.delete.setOnClickListener {

            disposable.add(
                travelDao.delete(placeFromMainActivity!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }
        binding.save.isEnabled = false
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        val intent = intent
        val info = intent.getStringExtra("info")

        if(info == "new"){
            binding.delete.visibility = View.GONE
            binding.save.visibility = View.VISIBLE
            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(p0: Location) {
                    boolean = preferences.getBoolean("boolean", false)
                    if (boolean == false) {
                        val userLocation = LatLng(p0.latitude, p0.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        preferences.edit().putBoolean("boolean", true).apply()
                    }

                }

            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Snackbar.make(
                        binding.root,
                        "Konuma Erişmek İçin İzin Gerekli",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("İzin Ver") {
                            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }.show()
                } else {
                    resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    5f,
                    locationListener
                )
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    val lstLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLocation, 15f))
                }
                mMap.isMyLocationEnabled = true
            }
        }else{
            mMap.clear()
            placeFromMainActivity = intent.getSerializableExtra("travel") as? Travel
            placeFromMainActivity?.let {
               val latlng = LatLng(it.latittude,it.longtitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))

                binding.name.setText(it.name)
                binding.explanation.setText(it.explanation)
                binding.save.visibility = View.GONE
                binding.delete.visibility = View.VISIBLE
            }
        }

    }

    private fun registerLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        5f,
                        locationListener
                    )
                    val lastLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null) {
                        val lstLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLocation, 15f))
                    }
                    mMap.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(this, "İzin Gereli!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        latitude = p0.latitude
        longitude = p0.longitude
        binding.save.isEnabled = true
    }
    private fun handleResponse(){
        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}