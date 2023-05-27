import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import com.example.aiphoto21.R

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var downloadButton: Button
    private var imageBitmap: Bitmap? = null
    private var outputFile: File? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_WRITE_PERMISSION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        imageView = findViewById(R.id.image_view)
        captureButton = findViewById(R.id.capture_button)
        downloadButton = findViewById(R.id.download_button)

        captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        downloadButton.setOnClickListener {
            if (imageBitmap != null) {
                saveImageToGallery()
            } else {
                Toast.makeText(this, "No image to download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data?.extras
            if (extras != null) {
                imageBitmap = extras.get("data") as Bitmap?
                imageView.setImageBitmap(imageBitmap)
                captureButton.isEnabled = false
                downloadButton.isEnabled = true
            }
        }
    }

    private fun saveImageToGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION
            )
        } else {
            saveImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImage() {
        val fileName = "enhanced_image.jpg"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        outputFile = File(storageDir, fileName)

        try {
            val outputStream: OutputStream = FileOutputStream(outputFile)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("MainActivity", "Error saving image", e)
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}
