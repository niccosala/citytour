package it.niccolo.citytour

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    lateinit var spot : Spot
    private lateinit var db : DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        db = DatabaseHandler(this)
        val name = intent.getStringExtra("spotName")!!
        txtSpotName.text = name
        spot = db.getSpecificSpot(name)!!
        setImage(db.getImage(spot))
        setDescritpion(spot.description)
        Log.d("dev-info", "received '$name' from MainActivity")
    }

    private fun setImage(image : Bitmap?) {
        if(image == null)
            StorageHandler.instance.downloadImage(this, imgSpot, spot)
        else
            imgSpot.setImageBitmap(image)
    }

    fun setPosition(lat: String, lgt: String, address: String) {
        "$lat, $lgt".also { txtCoordsValue.text = it }
        txtAddressValue.text = address
    }

    private fun setDescritpion(description: String) {
        txtDescriptionValue.text = description
    }

    @Suppress("UNUSED_PARAMETER")
    fun goBack(view : View) =
        finish()

}