package it.niccolo.citytour

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    lateinit var spot: Spot
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        db = DatabaseHandler(this)
        val name = intent.getStringExtra("spotName")
        txtSpotName.text = name
        spot = db.getSpecificSpot(name)!!
        StorageHandler.instance.downloadImage(imgSpot, spot.imagePath)
        setDescritpion(spot.description)
    }

    fun setPosition(lat: String, lgt: String, address: String) {
        txtLatitudeValue.text = lat
        txtLongitudeValue.text = lgt
        txtAddressValue.text = address
    }

    fun setDescritpion(description: String) {
        txtDescriptionValue.text = description
    }

    fun goBack(view : View) =
        finish()

}