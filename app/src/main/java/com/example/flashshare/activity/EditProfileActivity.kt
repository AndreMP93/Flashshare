package com.example.flashshare.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityEditProfileBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.viewmodel.EditProfileViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private lateinit var user: UserModel
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            viewModel.savePhotoProfile(user, selectedImageUri)
            binding.imageAvatarProfile.setImageURI(selectedImageUri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]

        observes()

        viewModel.getUserData()

        binding.updateButton.setOnClickListener {
            user.name = binding.nameTextInput.text.toString()
            user.username = binding.usernameTextInput.text.toString()
            user.bio = binding.bioUserTextInput.text.toString()
            viewModel.update(user)
        }

        binding.textChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }


        binding.editProfileToolbar.mainToolbar.setTitle(R.string.edit_profile)
        setSupportActionBar(binding.editProfileToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun observes(){

        viewModel.loadProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    user = it.data
                    binding.nameTextInput.setText(it.data.name)
                    binding.usernameTextInput.setText(it.data.username)
                    binding.bioUserTextInput.setText(it.data.bio)
                    if (it.data.urlPhotoProfile != null && it.data.urlPhotoProfile != ""){
                        Glide.with(this).load(it.data.urlPhotoProfile).into(binding.imageAvatarProfile)
                    }
                }
                is ResultModel.Loading -> {}
                is ResultModel.Error -> {
                    Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        viewModel.updateProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    Toast.makeText(applicationContext, getString(R.string.success_save_user_data), Toast.LENGTH_LONG).show()
                    finish()
                }
                is ResultModel.Loading -> {}
                is ResultModel.Error -> Toast.makeText(applicationContext, getString(R.string.error_save_user_data), Toast.LENGTH_LONG).show()
            }
        }
    }
}