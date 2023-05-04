package com.example.flashshare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityEditProfileBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.viewmodel.EditProfileViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private lateinit var user: UserModel

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


        binding.editProfileToolbar.mainToolbar.setTitle(R.string.edit_profile)
        setSupportActionBar(binding.editProfileToolbar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun observes(){

        viewModel.loadProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    user = it.data
                    binding.nameTextInput.setText(it.data.name)
                    binding.usernameTextInput.setText(it.data.username)
                    binding.bioUserTextInput.setText(it.data.bio)
                }
                is ResultModel.Loading -> Toast.makeText(applicationContext, "Loading!!!", Toast.LENGTH_LONG).show()
                is ResultModel.Error -> {
                    Toast.makeText(applicationContext, "Falha ao carregar os dados!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        viewModel.updateProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    Toast.makeText(applicationContext, "Sucesso!", Toast.LENGTH_LONG).show()
                    finish()
                }
                is ResultModel.Loading -> Toast.makeText(applicationContext, "Loading!!!", Toast.LENGTH_LONG).show()
                is ResultModel.Error -> Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}