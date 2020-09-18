package com.example.recyclemachine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

data class Image(val imageUrl: String, val fileName : String)  : Comparable<Image> {
    override fun compareTo(other: Image): Int = this.fileName.compareTo(other.fileName)
}


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main)

        imageButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

    }
}