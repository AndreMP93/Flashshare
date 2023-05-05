package com.example.flashshare.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.databinding.FragmentProfileBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.viewmodel.EditProfileViewModel

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        observes()

        binding!!.editProfileButton.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }

        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.getUserData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun observes(){
        viewModel.loadProcess.observe(viewLifecycleOwner){
            when(it){
                is ResultModel.Success -> {
                    if(binding!=null){
                        binding!!.progressBarProfile.visibility = View.GONE
                        binding!!.nameText.text = it.data.name
                        binding!!.usernameText.text = it.data.username
                        binding!!.bioText.text = it.data.bio
                        if(it.data.urlPhotoProfile !=null){
                            Glide.with(this).load(it.data.urlPhotoProfile).into(binding!!.imageAvatarProfile)
                        }
                    }


                }
                is ResultModel.Loading -> {
                    binding!!.progressBarProfile.visibility = View.VISIBLE
                }
                is ResultModel.Error -> Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}