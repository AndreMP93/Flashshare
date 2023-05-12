package com.example.flashshare.activity

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityPublicationPostBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.viewmodel.PublicationPostViewModel
import java.time.LocalDate
import java.util.Calendar

class PublicationPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPublicationPostBinding
    private lateinit var imageUri: Uri
    private lateinit var viewModel: PublicationPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublicationPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PublicationPostViewModel::class.java]

        binding.filterToolBar.mainToolbar.setTitle(R.string.filterTitle)
        setSupportActionBar(binding.filterToolBar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        setObserve()

        val bundle = intent.extras
        val imageUriString = bundle?.getString(AppConstants.BUNDLE.IMAGE_URI_ID)
        if (imageUriString!=null && imageUriString!=""){
            imageUri = Uri.parse(imageUriString)
            binding.imagePost.setImageURI(imageUri)
        }else{
            Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
            finish()
        }

//        val clarendon: Filter = FilterPack.getClarendon()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.menu_filter, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.ic_post_image -> {
                val post = PostModel()
                post.description = binding.descriptionInputText.text.toString()
                post.datePublication = Calendar.getInstance().timeInMillis.toString()
                post.id = ""
                post.urlPhotoPost = ""
                viewModel.publicationPost(post, imageUri)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setObserve(){
        viewModel.publicationProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    finish()
                }
                is ResultModel.Error -> Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                is ResultModel.Loading -> {}
            }
        }
    }

}