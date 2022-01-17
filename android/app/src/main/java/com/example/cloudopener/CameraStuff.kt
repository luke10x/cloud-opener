package com.example.cloudopener;

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloudopener.databinding.FragmentSecondBinding
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException

class CameraStuff(
    private val activity: MainActivity,
    private val binding: FragmentSecondBinding?,
) {
    val observers: ArrayList<ScannerObserver> = ArrayList()

    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private var scanned = false

    fun setupControls() {
        System.out.println("🐤 Setup camera controls")

        barcodeDetector =
            BarcodeDetector.Builder(activity).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(activity, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding?.cameraSurfaceView?.getHolder()?.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                System.out.println("🚮 Barcode Scanner released")
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    if (!scanned) {
                        scannedValue = barcodes.valueAt(0).rawValue

                        observers.forEach { it.onScanned(scannedValue) }

                        scanned = true
                    }
                }
            }
        })
    }

    fun stop() {
        cameraSource.stop()
    }

    fun add(observer: ScannerObserver) {
        observers.add(observer)
    }

    fun remove(observer: ScannerObserver) {
        observers.remove(observer)
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 1001

        fun askForCameraPermissionIfNeeded(context: AppCompatActivity) {
            val cancam = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (!cancam) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
        }

        fun isCameraPermissionsResultGranted(
            requestCode: Int,
            grantResults: IntArray
        ): Boolean {
            if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return true
                }
            }
            return false;
        }

        fun isCameraPermissionsResultDenied(
            requestCode: Int,
            grantResults: IntArray
        ): Boolean {
            if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    return true
                }
            }
            return false
        }
    }
}
