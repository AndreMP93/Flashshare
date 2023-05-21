package com.example.flashshare.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
                    binding.bioUserTextInput.setText(it.data.bio)
                    if (it.data.urlPhotoProfile != null && it.data.urlPhotoProfile != ""){
                        Glide.with(this).load(it.data.urlPhotoProfile).into(binding.imageAvatarProfile)
                    }
                    binding.updateButton.isClickable = true
                }
                is ResultModel.Loading -> {binding.updateButton.isClickable = false}
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
                    binding.updateButton.isClickable = true
                    binding.updateButton.text = getString(R.string.save_button)
                    finish()
                }
                is ResultModel.Loading -> {
                    binding.updateButton.isClickable = false
                    binding.updateButton.text = ""
                    binding.progressBarSaveData.visibility = View.VISIBLE
                }
                is ResultModel.Error -> {
                    Toast.makeText(applicationContext, getString(R.string.error_save_user_data), Toast.LENGTH_LONG).show()
                    binding.updateButton.isClickable = true
                    binding.updateButton.text = getString(R.string.save_button)
                    binding.progressBarSaveData.visibility = View.GONE
                }
            }
        }

        viewModel.savePhotoProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    binding.textChangePhoto.isClickable = true
                    binding.progressBarImageProfile.visibility = View.GONE
                }
                is ResultModel.Loading -> {
                    binding.textChangePhoto.isClickable = false
                    binding.progressBarImageProfile.visibility = View.VISIBLE
                }
                is ResultModel.Error -> {
                    Toast.makeText(applicationContext, getString(R.string.error_message), Toast.LENGTH_LONG).show()
                    binding.textChangePhoto.isClickable = true
                    binding.progressBarImageProfile.visibility = View.GONE
                }
            }
        }
    }
}