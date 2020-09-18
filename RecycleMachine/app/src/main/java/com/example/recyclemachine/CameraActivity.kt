package com.example.recyclemachine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.image
import org.jetbrains.anko.toast

class CameraActivity : AppCompatActivity() {


    lateinit var storage: FirebaseStorage
    var images : ArrayList<Image> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)


        val storage = FirebaseStorage.getInstance()
        val  storageRef = storage.reference.child("captureImages")

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEachIndexed { index,  item ->
                    //순차적으로 List에 데이터를 추가한다.
                    item.downloadUrl.addOnSuccessListener {
                        Log.d("MainActivity",  "result: " + it)
                        images.add(Image(it.toString(),item.name))

                        images.sort()

                        //recyclerView 어답터와 연결
                        camera_recyclerView.layoutManager = LinearLayoutManager(this)
                        val adapter = MyAdapter(images, this)
                        adapter.itemClick = object : MyAdapter.ItemClick{
                            override fun onClick(view: View, position: Int) {
                                Log.d("Adapter", "result: " + position.toString())
                                GlideApp.with(view)
                                    .load(images[position].imageUrl)
                                    .fitCenter()
                                    .into(camera_imageView)

                            }
                        }
                        //recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
                        camera_recyclerView.adapter = adapter
                    }
                }
            }
            .addOnFailureListener {
                //Error
                Log.d("MainActivity",  "result: Erorr가 발생했습니다." + it)
            }

    }
}