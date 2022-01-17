package com.example.cloudopener

interface ScannerObserver {
    fun onScanned(scannedValue: String)
}