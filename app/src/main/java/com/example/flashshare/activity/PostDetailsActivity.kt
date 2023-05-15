package com.example.flashshare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityPostDetailsBinding

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}