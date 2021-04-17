package it.niccolo.citytour

import android.graphics.Bitmap

class Spot(var name: String, snippet: String?, var lat: Double, var lgt: Double, var description: String, var imagePath: String, var image: Bitmap?) {

    var snippet : String? = ""

    init {
        this.snippet = snippet ?: ""
    }

    override fun toString() : String {
        return "$name ($lat, $lgt) \n$snippet \n$description"
    }

}