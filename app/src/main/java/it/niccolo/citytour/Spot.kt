package it.niccolo.citytour

import android.graphics.drawable.Drawable

class Spot {

    var name = ""
    var snippet : String? = ""
    var lat : Double = 0.0
    var lgt : Double = 0.0
    var description = ""
    var imagePath : String = ""

    constructor()

    constructor(name : String, snippet : String?, lat : Double, lgt : Double, description : String, imagePath : String) {
        this.name = name
        this.snippet = snippet ?: ""
        this.lat = lat
        this.lgt = lgt
        this.description = description
        this.imagePath = imagePath
    }

    override fun toString() : String {
        return "$name ($lat, $lgt) \n$snippet \n$description"
    }

}