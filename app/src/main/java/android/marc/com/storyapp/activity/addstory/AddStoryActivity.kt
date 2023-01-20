package android.marc.com.storyapp.activity.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.ViewModelFactory
import android.marc.com.storyapp.databinding.ActivityAddStoryBinding
import android.marc.com.storyapp.helper.createCustomTempFile
import android.marc.com.storyapp.helper.reduceFileImage
import android.marc.com.storyapp.helper.rotateBitmap
import android.marc.com.storyapp.helper.uriToFile
import android.marc.com.storyapp.model.SessionPreference
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String
    private lateinit var auth: String
    private var uploadFile: File? = null

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            uploadFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), true)
            binding.previewImage.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            uploadFile = myFile
            binding.previewImage.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        getToken()
        hideActionBar()
        setupViewModel()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnCamera.setOnClickListener {
            takePhoto()
        }
        binding.btnGallery.setOnClickListener {
            getPhotoFromGallery()
        }
        binding.btnUpload.setOnClickListener {
            if (uploadFile != null) {
                addStoryViewModel.startLoading()
                val file = reduceFileImage(uploadFile as File)

                val description = binding.edtDescriptionBox.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                addStoryViewModel.uploadImage(imageMultipart, description, null, null, auth)
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.error_dialog_title))
                    setMessage(getString(R.string.error_dialog_upload_message1))
                    setPositiveButton(getString(R.string.error_dialog_button)) { _,_ -> }
                    create()
                    show()
                }
            }
        }
    }

    private fun setupViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        addStoryViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        addStoryViewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                addStoryViewModel.doneDialogIsSuccess()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success_dialog_title))
                    setMessage(getString(R.string.success_dialog_upload_message))
                    setPositiveButton(getString(R.string.success_dialog_button)) { _,_ ->
                        finish()
                    }
                    create()
                    show()
                }
            }
        }

        addStoryViewModel.isError.observe(this) { isError ->
            if (isError) {
                addStoryViewModel.doneDialogIsError()
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.error_dialog_title))
                    setMessage(getString(R.string.error_dialog_upload_message2))
                    setPositiveButton(getString(R.string.error_dialog_button)) {_,_ -> }
                    create()
                    show()
                }
            }
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "android.marc.com.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun getToken() {
        var token: String?
        runBlocking { token = SessionPreference.getInstance(dataStore).getSessionToken().first() }
        this.auth = "Bearer $token"
    }

    private fun getPhotoFromGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.intent_gallery_title))
        launcherIntentGallery.launch(chooser)
    }

    private fun hideActionBar() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_is_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}