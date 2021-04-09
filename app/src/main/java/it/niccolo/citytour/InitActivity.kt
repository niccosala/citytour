package it.niccolo.citytour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class InitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show()
    }

}