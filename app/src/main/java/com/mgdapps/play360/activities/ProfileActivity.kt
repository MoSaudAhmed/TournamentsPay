package com.mgdapps.play360.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mgdapps.play360.BuildConfig
import com.mgdapps.play360.R
import com.mgdapps.play360.helper.*
import com.mgdapps.play360.helper.ImageUtils.filePath
import com.mgdapps.play360.models.UpdateProfileModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    var lay_Profile_picture: FrameLayout? = null
    var img_Profile_picture: ImageView? = null
    var et_Profile_displayName: EditText? = null
    var et_Profile_email: EditText? = null
    var btn_Profile_update: Button? = null
    var preferences: Preferences? = null
    var toolbar: Toolbar? = null
    var permissions: Permissions? = null

    private var imageFileName: String? = null
    private val CAMERA_IMAGE_CAPTURE = 1
    private val GALLERY_IMAGE_CAPTURE = 2
    private var lengthbmp: Long = 0
    private var imageBitmap: Bitmap? = null
    internal lateinit var byteImage: ByteArray

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var firestore: FirebaseFirestore
    private var mStorageRef: StorageReference? = null

    lateinit var updateProfileModel: UpdateProfileModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        img_Profile_picture = findViewById(R.id.img_Profile_picture)
        lay_Profile_picture = findViewById(R.id.lay_Profile_picture)
        et_Profile_displayName = findViewById(R.id.et_Profile_displayName)
        btn_Profile_update = findViewById(R.id.btn_Profile_update)
        et_Profile_email = findViewById(R.id.et_Profile_email)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "Update Profile"

        permissions = Permissions(this)
        preferences = Preferences()
        preferences!!.loadPreferences(this)
        et_Profile_displayName!!.setText(preferences!!.displayName)

        if (preferences!!.email != null && !TextUtils.isEmpty(preferences!!.email)) {
            et_Profile_email!!.setText(preferences!!.email)
            et_Profile_email!!.isFocusable = false
            et_Profile_email!!.setOnClickListener(this)
        }
        if (!TextUtils.isEmpty(preferences!!.profilePic)) {
            Picasso.get().load(preferences!!.profilePic).error(R.drawable.ic_person).into(img_Profile_picture)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        firestore = FirebaseFirestore.getInstance()
        mStorageRef = FirebaseStorage.getInstance().reference

        btn_Profile_update!!.setOnClickListener(this)
        lay_Profile_picture!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.lay_Profile_picture -> {
                if (permissions!!.checkAndRequestPermissions()) {
                    imagePickerDialog()
                } else {
                    Toast.makeText(this@ProfileActivity, "Please accept storage permissions, to access images", Toast.LENGTH_LONG).show()
                }
                return
            }
            R.id.btn_Profile_update -> {
                if (!TextUtils.isEmpty(et_Profile_displayName!!.text.toString().trim())) {
                    checkDataAndContinue()
                } else {
                    Toast.makeText(this@ProfileActivity, "Display name cannot be empty", Toast.LENGTH_LONG).show()
                }
                return
            }

            R.id.et_Profile_email -> {
                Toast.makeText(this@ProfileActivity, "Only \'null\' emails are editable", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun imagePickerDialog() {
        val settingsDialogue = AlertDialog.Builder(this@ProfileActivity)
        val layoutInflater = this@ProfileActivity.layoutInflater
        val view1 = layoutInflater.inflate(R.layout.exit_dialogue, null)
        val dialog = settingsDialogue.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setView(view1)
        val tv_exitDialog_title = view1.findViewById<TextView>(R.id.tv_exitDialog_title)
        val btn_exit = view1.findViewById<Button>(R.id.btn_exit)
        val btn_cancel = view1.findViewById<Button>(R.id.btn_cancel)
        tv_exitDialog_title.text = "Choose an action"
        btn_exit.text = "Camera"
        btn_cancel.text = "Gallery"
        btn_exit.background = resources.getDrawable(R.drawable.green_button_background)
        btn_exit.setOnClickListener {
            dialog.cancel()
            takeCameraPicture()
        }
        btn_cancel.setOnClickListener {
            dialog.cancel()
            takeGalleryPicture()
        }
        dialog.show()
    }


    private fun takeCameraPicture() {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        imageFileName = "$timeStamp.png"
        val outPutFile = File(filePath(this@ProfileActivity, imageFileName!!))
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val mImageCaptureUri = FileProvider.getUriForFile(
                this@ProfileActivity, BuildConfig.APPLICATION_ID + ".provider", outPutFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        startActivityForResult(cameraIntent, CAMERA_IMAGE_CAPTURE)
    }

    private fun takeGalleryPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_IMAGE_CAPTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (imageFileName != null) {
                val f: File
                val sourceUri = Uri.fromFile(File(ImageUtils.filePath(this@ProfileActivity, imageFileName!!)))
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val editedimageFileName = timeStamp + "_.png"
                val destinationUri = Uri.fromFile(
                        File(
                                ImageUtils.filePath(
                                        this@ProfileActivity,
                                        editedimageFileName
                                )
                        )
                )
                val scheme = sourceUri.scheme
                if (scheme != null) {
                    if (scheme == ContentResolver.SCHEME_CONTENT) {
                        try {
                            val fileInputStream =
                                    applicationContext.contentResolver
                                            .openInputStream(sourceUri)
                            if (fileInputStream != null) {
                                lengthbmp = fileInputStream.available() / 1024.toLong()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (scheme == ContentResolver.SCHEME_FILE) {
                        val path = sourceUri.path
                        try {
                            if (path != null) {
                                f = File(path)
                                lengthbmp = f.length() / 1024
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                val file: File = FileUtil.from(this@ProfileActivity, sourceUri!!)
                val compressedImageFile =
                        Compressor(this@ProfileActivity).compressToFile(file)
                launchPhotoEditor(sourceUri, Uri.fromFile(compressedImageFile))
            }
        } else if (requestCode == GALLERY_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val f: File
                val imageUri = data.data
                val timeStamp =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                                .format(Date())
                var scheme: String? = null
                if (imageUri != null) {
                    scheme = imageUri.scheme
                }
                if (scheme != null) {
                    if (scheme == ContentResolver.SCHEME_CONTENT) {
                        try {
                            val fileInputStream =
                                    applicationContext.contentResolver
                                            .openInputStream(imageUri!!)
                            if (fileInputStream != null) {
                                lengthbmp = fileInputStream.available() / 1024.toLong()
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else if (scheme == ContentResolver.SCHEME_FILE) {
                        val path = imageUri!!.path
                        try {
                            if (path != null) {
                                f = File(path)
                                lengthbmp = f.length() / 2014
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                imageFileName = timeStamp + "_.png"
                val file: File = FileUtil.from(this@ProfileActivity, imageUri!!)
                val compressedImageFile =
                        Compressor(this@ProfileActivity).compressToFile(file)
                launchPhotoEditor(imageUri, Uri.fromFile(compressedImageFile))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {

            val activityResult = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {

                Picasso.get().load(activityResult.uri)
                        .into(img_Profile_picture)
                try {
                    this.imageBitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            activityResult.uri
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = activityResult.error
                error.printStackTrace()
            }
        }

    }

    private fun launchPhotoEditor(sourceUri: Uri, destinationUri: Uri) {
        CropImage.activity(sourceUri)
                .setActivityTitle("")
                .setCropMenuCropButtonTitle("Done")
                .setOutputCompressQuality(100)
                .setAutoZoomEnabled(true).start(this@ProfileActivity)
    }


    private fun checkDataAndContinue() {

        updateProfileModel = UpdateProfileModel()

        if (imageBitmap != null) {

            var circularProgressBar = CircularProgressBar(this@ProfileActivity)
            circularProgressBar.showProgressDialog()

            byteImage = ImageUtils.encodeToBase64(imageBitmap!!)
            var localStorageRef =
                    mStorageRef?.child(Constants.UserDB)?.child(firebaseUser.uid)
                            ?.child(Constants.ProfilePic)
            localStorageRef?.putBytes(byteImage)?.addOnSuccessListener {
                localStorageRef.downloadUrl.addOnSuccessListener {

                    preferences!!.profilePic = it.toString()

                    saveDataAndExit()
                    circularProgressBar.dismissProgressDialog()
                }
            }?.addOnFailureListener {
                Toast.makeText(this@ProfileActivity, "Failed Uploading Picture: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                circularProgressBar.dismissProgressDialog()
            }?.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                //TODO: upload percent to dialog
            }
        } else {
            saveDataAndExit()
        }
    }

    private fun saveDataAndExit() {
        var circularProgressBar = CircularProgressBar(this)
        circularProgressBar.showProgressDialog()
        updateProfileModel.photoURL = preferences!!.profilePic
        updateProfileModel.displayName = et_Profile_displayName!!.text.toString()
        preferences!!.displayName = et_Profile_displayName!!.text.toString()
        preferences!!.email = et_Profile_email!!.text.toString()

        firestore.collection(Constants.UserDB).document(firebaseUser.uid).set(
                updateProfileModel,
                SetOptions.merge()
        ).addOnSuccessListener {
            circularProgressBar.dismissProgressDialog()
            preferences!!.savePreferences(this@ProfileActivity)
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
        }.addOnFailureListener {
            circularProgressBar.dismissProgressDialog()
            Toast.makeText(this@ProfileActivity, "${it.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}