package com.example.flashshare.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.activity.adapter.GridAdapter
import com.example.flashshare.databinding.ActivityFriendProfileBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.listener.GridListener
import com.example.flashshare.viewmodel.FriendProfileViewModel

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var viewModel: FriendProfileViewModel
    private var friendId: String = ""
    private var adapter: GridAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FriendProfileViewModel::class.java]

        val bundle = intent.extras
        friendId = bundle?.getString(AppConstants.BUNDLE.USER_ID).toString()
        if (friendId!=""){
            viewModel.getUserData(friendId)
        }else{
            Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
            finish()
        }

        observe()

        getUserData(friendId)

        binding.fiendProfileLayout.actionProfileButton.setText(R.string.save_button)

        binding.toolbarFiendProfile.mainToolbar.setTitle(R.string.edit_profile)
        setSupportActionBar(binding.toolbarFiendProfile.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)



    }

    override fun onStart() {
        super.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun observe(){
        viewModel.loadUserDataProcess.observe(this){
            when(it){
                is ResultModel.Success -> {
                    binding.toolbarFiendProfile.mainToolbar.title = it.data.name
                    binding.fiendProfileLayout.nameText.text = it.data.name
                    binding.fiendProfileLayout.bioText.text = it.data.bio
                    if (it.data.urlPhotoProfile != null && it.data.urlPhotoProfile != ""){
                        Glide.with(this).load(it.data.urlPhotoProfile).into(binding.fiendProfileLayout.imageAvatarProfile)
                    }
                }
                is ResultModel.Loading -> {}
                is ResultModel.Error -> Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.isFollowingUser.observe(this){
            if(it is ResultModel.Success){
                if(it.data){
                    binding.fiendProfileLayout.actionProfileButton.text = getString(R.string.unfllow_button)
                    binding.fiendProfileLayout.actionProfileButton.setOnClickListener {
                        viewModel.unfollowUser(friendId)
                    }
                }else{
                    binding.fiendProfileLayout.actionProfileButton.text = getString(R.string.follow_button)
                    binding.fiendProfileLayout.actionProfileButton.setOnClickListener {
                        viewModel.followUser(friendId)
                    }
                }
            }else{
                showToast(it.toString())
            }
        }

        viewModel.followerQuantity.observe(this){
            if(it is ResultModel.Success){
                binding.fiendProfileLayout.textQuantityFollowers.text = it.data.toString()
            }else if(it is ResultModel.Error){
                showToast(it.message!!)
            }
        }

        viewModel.followingQuantity.observe(this){
            if(it is ResultModel.Success){
                binding.fiendProfileLayout.textQuantityFollowing.text = it.data.toString()
            }else if(it is ResultModel.Error){
                showToast(it.message!!)
            }
        }

        viewModel.postsQuantity.observe(this){
            if(it is ResultModel.Success){
                binding.fiendProfileLayout.textQuantityPublications.text = it.data.toString()
            }else if(it is ResultModel.Error){
                showToast(it.message!!)
            }
        }

        viewModel.loadPosts.observe(this){
            when(it){
                is ResultModel.Success -> {
                    if (adapter != null) {
                        adapter!!.updateItems(it.data)
                    } else {
                        binding.fiendProfileLayout.profileGridView.layoutManager = GridLayoutManager(this, 3)
                        adapter = GridAdapter(applicationContext, it.data, object : GridListener {
                            override fun onClick(postId: String) {
                                val intent = Intent(applicationContext, PostDetailsActivity::class.java)
                                val bundle = Bundle().apply {
                                    putString(AppConstants.BUNDLE.POST_ID, postId)
                                    putString(AppConstants.BUNDLE.USER_ID, friendId)
                                }
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                        })
                        binding.fiendProfileLayout.profileGridView.adapter = adapter
                    }
                }
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }
    }

    private fun showToast(text: String){
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    private fun getUserData(userId: String){
        viewModel.checkFollowingUser(userId)
        viewModel.getQuantityFollower(userId)
        viewModel.getQuantityFollowing(userId)
        viewModel.getPostsQuantity(userId)
        viewModel.getPosts(userId)
    }
}