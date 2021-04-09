package it.niccolo.citytour

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_init.*

class InitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        if(checkUpPermissions())
            goToMainActivity()
    }

    fun clickRequirePermissions(view : View) = checkUpPermissions()

    private fun goToMainActivity() {
        prgBar.visibility = View.VISIBLE
        //TODO Firebase connection and download methods
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /***** PERMISSIONS *****/

    private fun availableFineLocation() : Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun availableCoarseLocation() : Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun availableInternet() : Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestFineLocation() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionCode.FINE_LOCATION
        )

    private fun requestCoarseLocation() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PermissionCode.COARSE_LOCATION
        )

    private fun requestInternet() =
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.INTERNET),
            PermissionCode.INTERNET
        )

    private fun checkUpPermissions() : Boolean {
        var everythingOk = true

        if(!availableFineLocation()) {
            requestFineLocation()
            everythingOk = false
        } else
            Log.d("dev-permissions", "Permissions: Fine Location OK")

        if(!availableCoarseLocation()) {
            requestCoarseLocation()
        everythingOk = false
        } else
            Log.d("dev-permissions", "Permissions: Coarse Location OK")

        if(!availableInternet()) {
            requestInternet()
            everythingOk = false
        } else
            Log.d("dev-permissions", "Permissions: Internet OK")

        return everythingOk
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionOk = true

        when(requestCode) {
            101 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                )
                    Log.d("dev-permissions", "Permissions: Fine Location OK")
                else
                    permissionOk = false
            }
            102 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                )
                    Log.d("dev-permissions", "Permissions: Coarse Location OK")
                else
                    permissionOk = false
            }
            103 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                )
                    Log.d("dev-permissions", "Permissions: Internet OK")
                else
                    permissionOk = false
            }
        }

        if(!permissionOk) {
            if(btnPermissions.visibility == View.VISIBLE) {
                Log.d("dev-permissions", "Permissions: Missing permissions")
                Toast.makeText(
                    this,
                    getString(R.string.err_required_permissions),
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnPermissions.visibility = View.VISIBLE
        } else {
            btnPermissions.visibility = View.INVISIBLE
            goToMainActivity()
        }
    }

    /***** FIREBASE REALTIME DATABASE > SQLITE *****/
    //TODO [IT] gestione connessione e download contenuti da Firebase a SQLite

}