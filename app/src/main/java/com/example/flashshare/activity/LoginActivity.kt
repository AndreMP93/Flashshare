package com.example.flashshare.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.MainActivity
import com.example.flashshare.databinding.ActivityLoginBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        observe()

        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.emailTextField.text.toString(),
                binding.passwordTextField.text.toString()
            )
        }

        binding.signUpButton.setOnClickListener{
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.verifyLoggedUser()
    }

    private fun observe(){
        viewModel.loginProcess.observe(this){
            when(it){
                is ResultModel.Success -> startActivity(Intent(applicationContext, MainActivity::class.java))
                is ResultModel.Error -> Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                is ResultModel.Loading -> Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loggedUser.observe(this){
            if(it){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }
}