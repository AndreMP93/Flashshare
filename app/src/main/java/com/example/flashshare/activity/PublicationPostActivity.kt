package com.example.flashshare.activity

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityPublicationPostBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.viewmodel.PublicationPostViewModel
import java.util.Calendar

class PublicationPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPublicationPostBinding
    private lateinit var imageUri: Uri
    private lateinit var viewModel: PublicationPostViewModel
    private var isPublicationProcess = false
    private var postModel: PostModel? = null
    private var postId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublicationPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PublicationPostViewModel::class.java]

        binding.filterToolBar.mainToolbar.setTitle(R.string.posts)
        setSupportActionBar(binding.filterToolBar.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        setObserve()

        loadDataFromActivity()
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
                if(postModel == null){
                    val post = PostModel()
                    post.description = binding.descriptionInputText.text.toString()
                    post.datePublication = Calendar.getInstance().timeInMillis.toString()
                    post.id = ""
                    post.urlPhotoPost = ""
                    post.imageUri = imageUri
                    postModel = post
                }else{
                    postModel!!.description = binding.descriptionInputText.text.toString()
                }
                if(!isPublicationProcess){
                    viewModel.publicationPost(postModel!!)
                }
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
                is ResultModel.Error -> {
                    hideProgressBar()
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                }
                is ResultModel.Loading -> {
                    showProgressBar()
                }
            }
        }

        viewModel.loadPostProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    hideProgressBar()
                    postModel = it.data
                    binding.descriptionInputText.setText(it.data.description)
                    Glide.with(this).load(it.data.urlPhotoPost).into(binding.postImageView)
                }
                is ResultModel.Error -> {
                    hideProgressBar()
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                }
                is ResultModel.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun loadDataFromActivity(){
        val bundle = intent.extras
        if(bundle!= null){
            val result = bundle.getString(AppConstants.BUNDLE.POST_ID)
            if(result!= null){
                postId = result
                viewModel.getPostData(postId)
            }else{
                val imageUriString = bundle.getString(AppConstants.BUNDLE.IMAGE_URI_ID)
                if (imageUriString!=null && imageUriString!=""){
                    imageUri = Uri.parse(imageUriString)
                    binding.postImageView.setImageURI(imageUri)
                }else{
                    Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun showProgressBar(){
        isPublicationProcess = true
        binding.progressBarPublication.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        isPublicationProcess = false
        binding.progressBarPublication.visibility = View.GONE
    }
}