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

    fun downloadImage(view : ImageView, imagePath : String) {
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        imageRef = storageRef.child("images/$imagePath")

        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            Log.d("dev-storage-downloadImage", "OK")
            val image = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(image)
        }.addOnFailureListener {
            Log.d("dev-storage-downloadImage", "Error (path passed: '$imagePath'): $it")
        }
    }

}