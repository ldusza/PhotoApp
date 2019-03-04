package com.example.lukasz.photoapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.R.attr.password
import android.app.ProgressDialog
import android.widget.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mRegisterBtn : Button
    lateinit var mRegEmail : EditText
    lateinit var mRegPass : EditText
    lateinit var mRegUserName : EditText
    lateinit var mRegLoginBtn : TextView
    lateinit var mProgressbar :ProgressDialog

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
//        mDatabase = FirebaseDatabase.getInstance().getReference("Users")

        mRegEmail = findViewById(R.id.regEmail)
        mRegLoginBtn = findViewById(R.id.regLoginText)
        mRegEmail = findViewById(R.id.regEmail)
        mRegUserName = findViewById(R.id.regUser)
        mRegPass = findViewById(R.id.regPassword)
        mRegisterBtn =findViewById(R.id.regBtn)


        mRegLoginBtn.setOnClickListener {

            val loginIntent = Intent(applicationContext , LoginActivity::class.java)
            startActivity(loginIntent)

        }

        mProgressbar = ProgressDialog(this)

        mRegisterBtn.setOnClickListener {
            val name = mRegUserName.text.toString().trim()
            val email = mRegEmail.text.toString().trim()
            val password = mRegPass.text.toString().trim()

            if(TextUtils.isEmpty(name)){

                mRegUserName.error = "Enter Name"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(email)){

                mRegUserName.error = "Enter Email"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(password)){

                mRegUserName.error = "Enter Password"
                return@setOnClickListener
            }

            createUser(name , email , password)
        }
    }

    private fun createUser(name : String , email : String , password : String) {

        mProgressbar.setMessage("Please wait..")
        mProgressbar.show()

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser!!.uid

                val userMap = HashMap<String, String>()
                userMap["name"] = name

                userMap["status"] = "Hey i am new PhotoApp"
                userMap["image"] = "default"


                mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid)

                mDatabase.setValue(userMap).addOnCompleteListener( OnCompleteListener { task ->

                    if(task.isSuccessful){
                        val Intent = Intent(applicationContext , MainActivity::class.java)
                        startActivity(Intent)
                        finish()
                        mProgressbar.dismiss()
                    }

                })


            } else {

                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                mProgressbar.dismiss()

            }
        }

    }
}
//
//class RegisterActivity : AppCompatActivity() {
//
//    lateinit var mRegisterBtn : Button
//    lateinit var mRegEmial: EditText
//    lateinit var mRegPass:EditText
//    lateinit var mRegUser:EditText
//    lateinit var mRegLoginBtn:TextView
//    lateinit var mRegProgress:ProgressDialog
//    lateinit var mAuth: FirebaseAuth
//    lateinit var mDataBase : DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        mRegisterBtn=findViewById(R.id.regBtn)
//        mRegEmial=findViewById(R.id.regEmail)
//        mRegPass=findViewById(R.id.regPassword)
//        mRegUser=findViewById(R.id.regUser)
//        mRegLoginBtn=findViewById(R.id.regLoginText)
//        mRegProgress=ProgressDialog(this)
//
//        mAuth = FirebaseAuth.getInstance()
//        //mDataBase= FirebaseDatabase.getInstance().getReference("Users")
//
//        mRegLoginBtn.setOnClickListener {
//            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
//            startActivity(loginIntent)
//            finish() }
//
//        mRegisterBtn.setOnClickListener {
//
//            val name = mRegUser.text.toString().trim()
//            val email = mRegEmial.text.toString().trim()
//            val password = mRegPass.text.toString().trim()
//            if(TextUtils.isEmpty(name)){
//                mRegUser.error="Enter Name"
//                return@setOnClickListener
//            }
//            if(TextUtils.isEmpty(email)){
//                mRegEmial.error="Enter Email"
//                return@setOnClickListener
//            }
//            if(TextUtils.isEmpty(password)){
//                mRegPass.error="Enter Password"
//                return@setOnClickListener
//            }
//
//            createUser(name, email, password)
//        }
//
//    }
//
//    private fun createUser(name : String, email : String, password : String) {
//        mRegProgress.setMessage("Please Wait...")
//        mRegProgress.show()
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
//
//                if (task.isSuccessful) {
//                    val currentUser = FirebaseAuth.getInstance().currentUser
//                    val uid = currentUser!!.uid
//                    val userMap= HashMap<String, Any>()
//
//                    userMap["name"] = name
//                    userMap["status"] = "test"
//
//                    mDataBase = FirebaseDatabase.getInstance().getReference("Users").child(uid)
//                    mDataBase.setValue(userMap).addOnCompleteListener(OnCompleteListener { task ->
//                        if (task.isSuccessful){
//                             mRegProgress.dismiss()
//                             val intent = Intent(applicationContext, MainActivity::class.java)
//                             startActivity(intent)
//                             finish()
//
//                        }
//                    })
//
//                } else {
//
//
//                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
//
//
//                }
//            mRegProgress.dismiss()
//            }
//    }
//
//}
