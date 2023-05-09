package com.example.flashshare.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.databinding.ActivityFriendProfileBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.viewmodel.FriendProfileViewModel

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendProfileBinding
    private lateinit var viewModel: FriendProfileViewModel
    private var friendId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FriendProfileViewModel::class.java]

        val bundle = intent.extras
        friendId = bundle?.getString(AppConstants.BUNDLE.USER_ID).toString()
        if (friendId!=null && friendId!=""){
            viewModel.getUserData(friendId)
        }else{
            Toast.makeText(applicationContext, getString(R.string.error_get_user_data), Toast.LENGTH_LONG).show()
            finish()
        }

        observe()

        viewModel.checkFollowingUser(friendId)
        viewModel.getQuantityFollower(friendId)
        viewModel.getQuantityFollowing(friendId)

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
                    binding.fiendProfileLayout.usernameText.text = it.data.username
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
                print("TESTE: ERROR = ${it}")
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
    }

    private fun showToast(text: String){
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }
}