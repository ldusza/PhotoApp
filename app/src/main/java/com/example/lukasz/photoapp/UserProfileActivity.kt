package com.example.lukasz.photoapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File


class UserProfileActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var mUserName: EditText
    lateinit var mUserStatus: EditText
    lateinit var mUpdateBtn: Button
    lateinit var mUserImage: ImageView
    lateinit var mDatabase: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var mProgressbar: ProgressDialog

    private val GALLERY_PICK = 1
    lateinit var mImageStorage: StorageReference


    private val LOCATION_AND_CONTACTS = arrayOf<String>(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA
    )
    //private val RC_CAMERA_PERM = 123
    private val RC_LOCATION_CONTACTS_PERM = 124


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        mUserName = findViewById(R.id.userProfileName)
        mUserImage = findViewById(R.id.userImageView)
        mUserStatus = findViewById(R.id.userProfileStatus)
        mUpdateBtn = findViewById(R.id.userProfileBtn)
        mProgressbar = ProgressDialog(this)


        mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        mImageStorage = FirebaseStorage.getInstance().reference
        mDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")             }

            override fun onDataChange(data: DataSnapshot) {


                val name = data.child("name").value!!.toString().trim()
                val status = data.child("status").value!!.toString().trim()
                val image = data.child("image").value!!.toString().trim()

                mUserName.setText(name)
                mUserStatus.setText(status)

                if (!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.drawable.image).into(mUserImage)

                }
            }

        })


        mUpdateBtn.setOnClickListener {

            val name = mUserName.text.toString().trim()
            val status = mUserStatus.text.toString().trim()

            if (TextUtils.isEmpty(name)) {
                mUserName.error = " Enter name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(status)) {
                mUserName.error = " Enter Status"
                return@setOnClickListener
            }

            updateUser(name, status)
        }



        mUserImage.setOnClickListener {
            Toast.makeText(applicationContext, "Touched ", Toast.LENGTH_LONG).show()

            if (hasLocationAndContactsPermissions()) {

                val galleryIntent = Intent()
                galleryIntent.type = "image/*"
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                this.startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK)
            } else {
                // Ask for both permissions
                EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your location and contacts to know where and who you are.",
                    RC_LOCATION_CONTACTS_PERM,
                    *LOCATION_AND_CONTACTS
                )
            }

        }


    }

    private fun updateUser(name: String, status: String) {
        mProgressbar.setMessage("Updating  wait..")
        mProgressbar.show()

        val userMap = HashMap<String, Any>()
        userMap["name"] = name
        userMap["status"] = status

        mDatabase.updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                mProgressbar.dismiss()

                finish()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_PICK && resultCode == Activity.RESULT_OK) {

            val imageUri = data?.data

            CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .setMinCropWindowSize(500, 500)
                .start(this)


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

                mProgressbar = ProgressDialog(this)
                mProgressbar.setTitle("Uploading Image...")
                mProgressbar.setMessage("Please Wait....")
                mProgressbar.setCanceledOnTouchOutside(false)
                mProgressbar.show()


                val resultUri = result.uri
                val thumb_filePath = File(resultUri.path)

                val thumb_bitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filePath)
//
                val baos = ByteArrayOutputStream()
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val thumb_byte = baos.toByteArray()


                mAuth = FirebaseAuth.getInstance()
                val uid = mAuth.currentUser?.uid

                val filepath = mImageStorage.child("profile_images").child(uid + ".jpg")
                val thumb_filepath = mImageStorage.child("profile_images").child("Thumbs").child(uid + ".jpg")
                filepath.putFile(resultUri)
                    .addOnSuccessListener { taskSnapshot ->
                        filepath.downloadUrl.addOnCompleteListener { taskSnapshot ->
                            val download_url = taskSnapshot.result.toString()
                            val uploadTask = thumb_filepath.putBytes(thumb_byte)

                            uploadTask.addOnSuccessListener { thumb_task ->
                                thumb_filepath.downloadUrl.addOnCompleteListener { thumb_task ->
                                    val thumb_downloadUrl = thumb_task.result.toString()
                                    //val thumb_downloadUrl = thumb_task.result.toString()
                                    if (thumb_task.isSuccessful) {

                                        val update_hashMap = HashMap<String, Any>()
                                        update_hashMap["image"] = download_url
                                        update_hashMap["thumb_image"] = thumb_downloadUrl

                                        mDatabase.updateChildren(update_hashMap)
                                            .addOnCompleteListener(OnCompleteListener<Void> { task ->
                                                if (task.isSuccessful) {
                                                    mProgressbar.dismiss()
                                                    Toast.makeText(
                                                        this,
                                                        "Profile Picture is Uploaded Successfully ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                }
                                            })

                                    } else {

                                        mProgressbar.dismiss()
                                        Toast.makeText(this, "Error in Uploading 1", Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }


                        }.addOnFailureListener {
                            mProgressbar.dismiss()
                            Toast.makeText(this, "Error in Uploading ", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()

        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasLocationAndContactsPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *LOCATION_AND_CONTACTS)
    }
}
