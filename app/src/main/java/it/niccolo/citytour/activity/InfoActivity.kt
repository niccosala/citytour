package it.niccolo.citytour.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import it.niccolo.citytour.R
import it.niccolo.citytour.entity.Spot
import it.niccolo.citytour.handler.DatabaseHandler
import it.niccolo.citytour.handler.StorageHandler
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    lateinit var spot : Spot
    private lateinit var db : DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        db = DatabaseHandler(this)
        val name = intent.getStringExtra("spotName")!!
        txtSpotName.text = formatName(name)
        spot = db.getSpecificSpot(name)!!
        StorageHandler.instance.downloadImage(imgSpot, spot)
        setDescritpion(spot.description)
        Log.d("dev-info", "received '$name' from MainActivity")
    }

    fun setPosition(lat: String, lgt: String, address: String) {
        "$lat, $lgt".also { txtCoordsValue.text = it }
        txtAddressValue.text = address
    }

    private fun formatName(name : String) : String {
        if(name.length > 24)
            return name.substring(0, 22) + "..."
        return name
    }

    private fun setDescritpion(description : String) {
        txtDescriptionValue.text = description
    }

    @Suppress("UNUSED_PARAMETER")
    fun goBack(view : View) =
        finish()

}