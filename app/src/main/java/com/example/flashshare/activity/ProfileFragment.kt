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
import com.example.flashshare.activity.adapter.GridAdapter
import com.example.flashshare.databinding.FragmentProfileBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.listener.GridListener
import com.example.flashshare.viewmodel.EditProfileViewModel

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private lateinit var viewModel: EditProfileViewModel
    private var adapter: GridAdapter? = null
    private val listPost = mutableListOf<PostModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)


        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        observes()

        binding!!.actionProfileButton.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }





        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        getUserData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun observes() {
        viewModel.loadProcess.observe(viewLifecycleOwner) {
            when (it) {
                is ResultModel.Success -> {
                    if (binding != null) {
                        binding!!.progressBarProfile.visibility = View.GONE
                        binding!!.nameText.text = it.data.name
                        binding!!.usernameText.text = it.data.username
                        binding!!.bioText.text = it.data.bio
                        if (it.data.urlPhotoProfile != null && it.data.urlPhotoProfile != "") {
                            Glide.with(this).load(it.data.urlPhotoProfile)
                                .into(binding!!.imageAvatarProfile)
                        }
                    }


                }

                is ResultModel.Loading -> {
                    binding!!.progressBarProfile.visibility = View.VISIBLE
                }

                is ResultModel.Error -> showToast(getString(R.string.error_get_user_data))
            }
        }

        viewModel.loadPosts.observe(viewLifecycleOwner) {
            if (it is ResultModel.Success) {
                if (adapter != null) {
                    adapter!!.updateItems(it.data)
                } else {
                    adapter = GridAdapter(requireContext(), it.data, object : GridListener{
                        override fun onClick(postId: String) {
                            val intent = Intent(context, PostDetailsActivity::class.java)
                            val bundle = Bundle().apply {
                                putString(AppConstants.BUNDLE.POST_ID, postId)
                            }
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    })
                    binding!!.profileGridView.adapter = adapter
                }

            }
        }

        viewModel.postsQuantity.observe(viewLifecycleOwner) {
            if (it is ResultModel.Success) {
                binding!!.textQuantityPublications.text = it.data.toString()
            } else {
                showToast(getString(R.string.error_get_user_data))
            }
        }
        viewModel.followerQuantity.observe(viewLifecycleOwner) {
            if (it is ResultModel.Success) {
                binding!!.textQuantityFollowers.text = it.data.toString()
            } else {
                showToast(getString(R.string.error_get_user_data))
            }
        }
        viewModel.followingQuantity.observe(viewLifecycleOwner) {
            if (it is ResultModel.Success) {
                binding!!.textQuantityFollowing.text = it.data.toString()
            } else {
                showToast(getString(R.string.error_get_user_data))
            }
        }
    }

    private fun getUserData() {
        viewModel.getUserData()
        viewModel.getPosts()
        viewModel.getPostsQuantity()
        viewModel.getFollowerQuantity()
        viewModel.getFollowingQuantity()
    }

    private fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}