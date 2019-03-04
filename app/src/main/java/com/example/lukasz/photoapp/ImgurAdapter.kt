package com.example.lukasz.photoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.imgur_item.view.*

class ImgurAdapter (val items : ArrayList<Photo>, val context: Context) : RecyclerView.Adapter<ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.imgur_item, parent, false))

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.get().load("https://i.imgur.com/" +
                items[position].id + ".jpg").into(holder.photo)
        holder.title.text = items[position].title
    }

}
class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val photo = view.imgur_photo
    val title = view.imgur_title
}