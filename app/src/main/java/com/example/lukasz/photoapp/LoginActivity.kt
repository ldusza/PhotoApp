package com.example.lukasz.photoapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    lateinit var mLoginButton: Button
    lateinit var mCreateUser: TextView
    lateinit var mForgetPass: TextView
    lateinit var mLoginEmail: EditText
    lateinit var mLoginPass: EditText
    lateinit var mRegProgress: ProgressDialog

    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mLoginButton = findViewById(R.id.loginButton)
        mCreateUser = findViewById(R.id.loginCreateBtn)
        mForgetPass = findViewById(R.id.loginForgetPassword)
        mLoginEmail = findViewById(R.id.loginEmail)
        mLoginPass = findViewById(R.id.loginPassword)
        mRegProgress = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        mCreateUser.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        mLoginButton.setOnClickListener {
            val email = mLoginEmail.text.toString().trim()
            val password = mLoginPass.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                mLoginEmail.error = "Enter email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                mLoginPass.error = "Enter password"
                return@setOnClickListener
            }
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        mRegProgress.setMessage("Please Wait...")
        mRegProgress.show()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mRegProgress.dismiss()
                    val startIntent = Intent(applicationContext, ImgurActivity::class.java)
                    startActivity(startIntent)
                    finish()

                } else {

                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()

                }
                mRegProgress.dismiss()
            }


    }
}
