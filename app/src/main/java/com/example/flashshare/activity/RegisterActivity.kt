package com.example.flashshare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityRegisterBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val user = UserModel(
                binding.userNameTextEdit.text.toString(),
                binding.emailEditText.text.toString()
            )
            user.password = binding.passwordEditText.text.toString()
            viewModel.registerUser(user)
        }

        observes()
    }

    private fun observes(){
        viewModel.registerProcess.observe(this){
            when(it){
                is ResultModel.Success -> {}
                is ResultModel.Error -> it.message?.let { it1 -> showToast(it1) }
                is ResultModel.Loading -> {}
            }
        }

        viewModel.saveDateProcess.observe(this){
            if (it is ResultModel.Success){
                finish()
            }
            if(it is ResultModel.Error){
                showToast(getString(R.string.error_register))
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}