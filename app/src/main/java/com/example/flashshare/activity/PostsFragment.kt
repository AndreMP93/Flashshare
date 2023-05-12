package com.example.flashshare.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.flashshare.databinding.FragmentPostsBinding
import com.example.flashshare.service.AppConstants

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

//    private val viewModel: PostsViewModel by viewModels()

    private lateinit var imageUri: Uri

    private val pickGalleryImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri ->
                showFilterActivity(selectedImageUri)
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                binding.imageViewTeste.setImageURI(imageUri)
                showFilterActivity(imageUri)
            } else {

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        binding.openGalleryButton.setOnClickListener {
            pickGalleryImage.launch("image/*")
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openCameraButton.setOnClickListener {
            if (hasCameraPermission()) {
                if (hasWriteStoragePermission()) {
                    openCamera()
                } else {
                    requestWriteStoragePermission()
                }
            } else {
                requestCameraPermission()
                requestWriteStoragePermission()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hasCameraPermission(): Boolean {
        val cameraPermission = Manifest.permission.CAMERA
        return ContextCompat.checkSelfPermission(
            requireContext(),
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasWriteStoragePermission(): Boolean {
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(
            requireContext(),
            writeStoragePermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA
        requestPermission(cameraPermission)
    }

    private fun requestWriteStoragePermission() {
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        requestPermission(writeStoragePermission)
    }

    private fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 0)
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "My Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "Picture taken by camera")
        }

        val resolver = requireActivity().contentResolver
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        takePictureLauncher.launch(imageUri)
    }

    private fun showFilterActivity(image: Uri) {
        val intent = Intent(context, PublicationPostActivity::class.java)
        val bundle = Bundle().apply {
            putString(AppConstants.BUNDLE.IMAGE_URI_ID, image.toString())
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
