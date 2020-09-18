package com.example.recyclemachine

import android.content.Context
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*




class MyAdapter(val items : ArrayList<Image>, val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlideApp.with(context)
            .load(items[position].imageUrl)
            .into(holder?.imageView)

         holder?.textDate.text = items[position].fileName


        if (itemClick != null) {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
    }


    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.user_imageview
        val textDate = view.user_textDate

        fun bind(item: ArrayList<Image>, context: Context) {
        }

    }

}