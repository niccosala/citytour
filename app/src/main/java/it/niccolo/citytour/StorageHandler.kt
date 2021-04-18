package it.niccolo.citytour

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class StorageHandler private constructor() {

    private var storage : FirebaseStorage = FirebaseStorage.getInstance()
    private var storageRef : StorageReference = storage.reference
    private lateinit var imageRef : StorageReference

    private object HOLDER {
        val INSTANCE = StorageHandler()
    }

    companion object {
        val instance : StorageHandler by lazy { HOLDER.INSTANCE }
    }

    fun downloadImage(view: ImageView, spot: Spot) {
        imageRef = storageRef.child("images/${spot.imagePath}")

        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            val image = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(image)
        }.addOnFailureListener {
            view.setImageResource(R.drawable.noimage)
            Log.d(
                "dev-storage",
                "Error downloading the image (path passed: '${spot.imagePath}'): $it"
            )
        }
    }

}