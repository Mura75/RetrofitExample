package team.codebuster.retrofitexample.view

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import team.codebuster.retrofitexample.R
import team.codebuster.retrofitexample.utils.RequestConstants
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var buttonCamera: Button
    private lateinit var buttonGallery: Button

    private var selectedPhotoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ivAvatar = findViewById(R.id.ivAvatar)
        buttonCamera = findViewById(R.id.buttonCamera)
        buttonGallery = findViewById(R.id.buttonGallery)

        ivAvatar.setOnClickListener {
           getPermissions()
        }

        buttonCamera.setOnClickListener {
            getPermissionsForCamera()
        }

        buttonGallery.setOnClickListener {
            getPermissionForGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestConstants.CAMERA) {
                selectedPhotoFile?.let { file ->
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            Uri.parse("file:${file.absolutePath}")
                        )
                    ivAvatar.setImageBitmap(bitmap)
                }
            } else if (requestCode == RequestConstants.GALLERY) {
                val image = data?.data
                ivAvatar.setImageURI(image)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestConstants.AVATAR_CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
            return
        } else if (requestCode == RequestConstants.AVATAR_GALLERY_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
            return
        } else if (requestCode == RequestConstants.AVATAR_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imageChooserDialog()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File.createTempFile("avatar", ".jpg", cacheDir)
        val avatarUri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            imageFile
        )
        selectedPhotoFile = imageFile
        intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri)
        startActivityForResult(intent, RequestConstants.CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        startActivityForResult(intent, RequestConstants.GALLERY)
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            val galleryGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            if (cameraGranted && galleryGranted) {
                imageChooserDialog()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                    RequestConstants.AVATAR_PERMISSION_REQUEST
                )
            }
        } else {
            imageChooserDialog()
        }
    }

    private fun getPermissionsForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (cameraGranted) {
                openCamera()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    RequestConstants.AVATAR_CAMERA_PERMISSION_REQUEST
                )
            }
        } else {
            openCamera()
        }
    }

    private fun getPermissionForGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val galleryGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            if (galleryGranted) {
                openGallery()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RequestConstants.AVATAR_GALLERY_PERMISSION_REQUEST
                )
            }
        } else {
            openGallery()
        }
    }

    private fun imageChooserDialog() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        adapter.add("Camera")
        adapter.add("Gallery")
        AlertDialog.Builder(this)
            .setTitle("Change avatar")
            .setAdapter(adapter) { dialog, which ->
                if (which == 0) {
                    openCamera()
                } else {
                    openGallery()
                }
            }
            .create()
            .show()
    }
}
