package com.example.finallyy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class MainActivity3 : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var barcodeScanner: DecoratedBarcodeView
    private lateinit var tvScannedResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)


        val hey=findViewById<TextView>(R.id.loginTitle)
        val username=intent.getStringExtra("username")
        val firstName=intent.getStringExtra("firstName")
        val nameToShow = when {
            !firstName.isNullOrEmpty() -> firstName
            !username.isNullOrEmpty() -> username
            else -> "User"
        }

        hey.text = getString(R.string.hey_text, nameToShow)

        val savedBtn = findViewById<Button>(R.id.btnSavedProducts)

        savedBtn.setOnClickListener {
            startActivity(
                Intent(this, SavedProductsActivity::class.java)
            )
        }

        val summaryBtn = findViewById<Button>(R.id.btnHealthSummary)
        summaryBtn.setOnClickListener {
            startActivity(Intent(this, HealthSummaryActivity::class.java))
        }





        tvScannedResult = findViewById(R.id.tv_scanned_result)

        val scanButton = findViewById<Button>(R.id.btn_scan)
        scanButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                startScanning()
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()

    }

    private fun startScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan a barcode")
        integrator.setOrientationLocked(true)
        integrator.setBeepEnabled(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.captureActivity = com.journeyapps.barcodescanner.CaptureActivity::class.java
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                tvScannedResult.text = "Scanned: ${result.contents}"

                Toast.makeText(this, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()


                val intent = android.content.Intent(this, MainActivity4::class.java)
                intent.putExtra("SCANNED_ID", result.contents)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startScanning()
        }
    }
}