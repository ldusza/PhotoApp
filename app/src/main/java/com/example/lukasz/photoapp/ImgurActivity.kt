package com.example.lukasz.photoapp

import android.app.WallpaperManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import java.io.IOException
import org.json.JSONObject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_imgur.*
import kotlinx.android.synthetic.main.imgur_item.*


class ImgurActivity : AppCompatActivity() {
    private val clientId="53c0db75ef99336"
    private val imgurUrl="https://api.imgur.com/3/gallery/user/rising/0.json"
    var photos = ArrayList<Photo>()
    val httpClient by lazy { OkHttpClient() }
    val httpRequest by lazy{Request.Builder()
        .url(imgurUrl)
        .header("Authorization","Client-ID $clientId")
        .header("User-Agent","PhotoApp")
        .build()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imgur)
        loadImages()
        rv_imgur_photos.adapter=ImgurAdapter(photos,this)




    }
    private fun loadImages(){
        httpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val data = JSONObject(response.body()?.string())
                val items = data.getJSONArray("data")
                //tu to było

                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val imPhoto = Photo()
                    if (item.getBoolean("is_album")) {
                        imPhoto.id = item.getString("cover")
                    } else {
                        imPhoto.id = item.getString("id")
                    }
                    imPhoto.title=item.getString("title")

                    photos.add(imPhoto) // Add photo to list


                }
                runOnUiThread { render(photos)
                    }




            }

        })

    }

    private fun render(photos: ArrayList<Photo>) {
        var rv:RecyclerView=findViewById(R.id.rv_imgur_photos)
        rv.layoutManager = LinearLayoutManager(this)
        //rv.addOnItemTouchListener(mListener: RecycleItem)

//        imgur_photo.setOnClickListener{
//            val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(this)
//            wallpaperManager.setResource(photos)
//        }

    }



}


class Photo{
    lateinit var id:String
    lateinit var title: String
}
//class PhotoVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    internal var photo: ImageView? = null
//
//
