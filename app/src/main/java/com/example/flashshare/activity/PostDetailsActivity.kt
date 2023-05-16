package com.example.flashshare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.activity.adapter.CommentAdapter
import com.example.flashshare.databinding.ActivityPostDetailsBinding
import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.listener.CommentListener
import com.example.flashshare.viewmodel.PostDetailsViewModel

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var viewModel: PostDetailsViewModel
    private lateinit var postDetails: PostModel
    private var postId:String = ""
    private var friendId: String = ""
    private var commentList = mutableListOf<CommentModel>()
    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PostDetailsViewModel::class.java]

        val bundle = intent.extras
        postId = bundle?.getString(AppConstants.BUNDLE.POST_ID).toString()
        friendId = bundle?.getString(AppConstants.BUNDLE.USER_ID).toString()

        if (postId != ""){
            if(friendId != ""){
                viewModel.getPost(friendId, postId)
                viewModel.getComments(friendId, postId)
            }else{
                viewModel.getPost(postId)
                viewModel.getComments(postId)
            }
        }else{
            showToast(getString(R.string.error_message))
            finish()
        }

        setObserve()

        setRecyclerView()

        binding.likeImage.setOnClickListener {
            postDetails.isLiked = !postDetails.isLiked
            viewModel.updatePost(postId,postDetails)
        }

    }

    private fun setObserve(){
        viewModel.loadPostProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    postDetails = it.data
                    binding.descriptionPostText.text = it.data.description
                    Glide.with(this).load(it.data.urlPhotoPost)
                        .into(binding.imagePost)
                    if(it.data.isLiked){
                        binding.likeImage.setImageResource(R.drawable.ic_like)
                    }
                }
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.updatePostProcess.observe(this){
            when(it){
                is ResultModel.Success -> {

                }
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.createCommentProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    viewModel.getComments(postId)
                }
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.loadCommentsProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    adapter.updateUsersList(it.data)
                }
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setRecyclerView(){
        adapter = CommentAdapter(applicationContext, commentList, object: CommentListener{
            override fun onClickEdit(commentId: String) {
                println("EDIT")
            }

            override fun onClickDelete(commentId: String) {
                println("DELETE")
            }
        })

        binding.commentRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.commentRecyclerView.adapter = adapter
    }
}