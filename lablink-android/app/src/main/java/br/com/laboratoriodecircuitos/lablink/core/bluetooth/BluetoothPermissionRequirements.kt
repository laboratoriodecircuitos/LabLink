package br.com.laboratoriodecircuitos.lablink.core.bluetooth

import android.Manifest
import android.os.Build

object BluetoothPermissionRequirements {
    fun connectionPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            emptyArray()
        }
    }

    fun discoveryPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun allBluetoothRuntimePermissions(): Array<String> {
        return (connectionPermissions() + discoveryPermissions()).distinct().toTypedArray()
    }
}
