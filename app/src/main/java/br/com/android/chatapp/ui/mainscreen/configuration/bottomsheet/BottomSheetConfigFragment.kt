package br.com.android.chatapp.ui.mainscreen.configuration.bottomsheet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import br.com.android.chatapp.databinding.FragmentBottomsheetBinding
import br.com.android.chatapp.ui.util.hasPermission

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class BottomSheetConfigFragment : BottomSheetDialogFragment(){

    companion object {
        private const val PERMISSION_GALLERY = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
    }

    private var _binding: FragmentBottomsheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var image               : ByteArray
    private lateinit var storageReference    : StorageReference
    private lateinit var dialog              : AlertDialog
    private lateinit var auth                : FirebaseAuth
    private lateinit var fstore              : FirebaseFirestore
    private lateinit var userId              : String
    private lateinit var db                  : DocumentReference

    private val requestGallery =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requestGalleryPermission(it)
        }

    private val resultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultGalleryForResult(it)
        }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requestCameraPermission(it)
        }

    private val resultCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultCameraForResult(it)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentBottomsheetBinding.inflate(inflater, container, false)


        auth                = FirebaseAuth.getInstance()
        fstore              = FirebaseFirestore.getInstance()
        userId              = auth.currentUser!!.uid
        storageReference    = FirebaseStorage.getInstance().reference
            .child("$userId/profilePhoto")
        db = fstore.collection("users").document(userId)

        binding.camButton.setOnClickListener {
            verifyCameraPermission()
        }

        binding.galleryButton.setOnClickListener {
            verifyGalleryPermission()
        }

        return binding.root
    }

    private fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        image = baos.toByteArray()


        storageReference.putBytes(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String, String>()
                obj["userProfilePhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSucess", "Profile Picture Uploaded")
                }
            }
        }
    }



    private fun resultCameraForResult(it: ActivityResult) {
        if(it.resultCode == Activity.RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data") as Bitmap
            uploadImage(imageBitmap)
        }
    }

    private fun requestCameraPermission(it: Boolean) {
        if (it) {
            resultCamera.launch(
                Intent(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                )
            )
        } else {
            showDialg()
        }
    }


    private fun verifyCameraPermission() {
        val permissionCameraGranted = hasPermission(PERMISSION_CAMERA)
        when {
            permissionCameraGranted -> {
                resultCamera.launch(
                    Intent(
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    )
                )
            }
            shouldShowRequestPermissionRationale(PERMISSION_CAMERA) -> showDialg()
            else -> requestCamera.launch(PERMISSION_CAMERA)
        }
    }

    private fun showDialg() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Atenção")
            .setMessage("Precisamos do acesso a sua galeria, deseja permitir agora?")
            .setNegativeButton("Não") {_, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") {_, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", requireActivity().packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = builder.create()
        dialog.show()

    }

    private fun verifyGalleryPermission() {
        val permissionGalleryGranted = hasPermission(PERMISSION_GALLERY)
        when {
            permissionGalleryGranted -> {
                resultGallery.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }
            shouldShowRequestPermissionRationale(PERMISSION_GALLERY) -> showDialg()
            else -> requestGallery.launch(PERMISSION_GALLERY)
        }
    }

    private fun resultGalleryForResult(result: ActivityResult) {
        if (result.data?.data != null) {
            val bitmap : Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver,
                    result.data?.data
                )
            } else {
                val source = ImageDecoder.createSource(
                    requireActivity().contentResolver,
                    result.data?.data!!
                )
                ImageDecoder.decodeBitmap(source)
            }
            uploadImage(bitmap)
        }
    }

    private fun requestGalleryPermission(it: Boolean) {
        if( it ) {
            resultGallery.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        } else {
            showDialg()
        }
    }
}
