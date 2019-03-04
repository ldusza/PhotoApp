package com.example.lukasz.photoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast


import com.google.firebase.auth.FirebaseAuth
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mToolbar: Toolbar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mAuth=FirebaseAuth.getInstance()
        mToolbar=findViewById(R.id.mainToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Photo App"

            }




    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser==null){
            val intent=Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==R.id.menu_Logout){
            FirebaseAuth.getInstance().signOut()
            val intent=Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if(item?.itemId==R.id.menu_Profile){
                val userProfile=Intent(applicationContext, UserProfileActivity::class.java)
                startActivity(userProfile)
            //finish()


        }
        if(item?.itemId==R.id.menu_Imgur){
            val userProfile=Intent(applicationContext, ImgurActivity::class.java)
            startActivity(userProfile)}
        return true
    }
}

