package com.shaahid.alibi

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime


class attendanceLocation : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_location)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)




        findViewById<Button>(R.id.getLocation).setOnClickListener(){
            logAttendanceLoaction()
        }


    }


    /*function which determines user location*/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun logAttendanceLoaction() {


        val loaction = fusedLocationProviderClient.lastLocation


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
            /*Checks if we have users permission to use their location*/
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),101)
            return
        }
        loaction.addOnSuccessListener {
            if(it != null){
                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show();
                storeData(it.longitude.toString(), it.latitude.toString())
            } else {
                Toast.makeText(applicationContext, ": ${it}", Toast.LENGTH_SHORT).show();

            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    private fun storeData(long:String, lats:String) {
        val firestore = FirebaseFirestore.getInstance()
        val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        /*requires api 26+*/
        val currentDateTime = LocalDateTime.now().toString();


        val attendee: MutableMap<String, Any> = HashMap()
        attendee["studentNum"] = "218000437"
        attendee["dateTime"] = currentDateTime
        attendee["studentLong"] = long
        attendee["studentLat"] = lats
        attendee["deviceID"] = deviceID




        firestore.collection("attendance").add(attendee).addOnSuccessListener{
            Toast.makeText(applicationContext, "Logged", Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "issue", Toast.LENGTH_SHORT).show()
            }
    }


}