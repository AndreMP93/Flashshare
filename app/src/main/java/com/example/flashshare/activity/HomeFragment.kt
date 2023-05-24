package com.example.flashshare.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashshare.R
import com.example.flashshare.activity.adapter.FeedAdapter
import com.example.flashshare.databinding.FragmentHomeBinding
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.listener.FeedListener
import com.example.flashshare.viewmodel.FeedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _adapter: FeedAdapter? = null
    private val adapter get() = _adapter!!
    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container,false)

        viewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        setObserve()
        viewModel.getFeed()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setObserve(){
        viewModel.loadFeedProcess.observe(viewLifecycleOwner){
            when(it){
                is ResultModel.Success ->{
                    binding.progressBarFeed.visibility = View.GONE
                    if(_adapter == null){
                        setRecyclerView(it.data)
                    }else{
                        adapter.updateListPost(it.data)
                    }
                }
                is ResultModel.Loading ->{binding.progressBarFeed.visibility = View.VISIBLE}
                is ResultModel.Error ->{println("ERROR -> ${it.message}")}
            }
        }

        viewModel.loadUserDataProcess.observe(viewLifecycleOwner){
            when(it){
                is ResultModel.Success -> {}
                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }
    }

    private fun setRecyclerView(listPosts: List<PostModel>){
        _adapter = FeedAdapter(requireContext(), listPosts, object:FeedListener{
            override fun onClick(post: PostModel) {
                val intent = Intent(context, PostDetailsActivity::class.java)
                val bundle = Bundle().apply {
                    putString(AppConstants.BUNDLE.POST_ID, post.id)
                    putString(AppConstants.BUNDLE.USER_ID, post.userId)
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun likeClick(postId: String) {
                viewModel.changeLike(postId)
            }

            override fun userProfileClick(userId: String) {
                val intent = Intent(context, FriendProfileActivity::class.java)
                val bundle = Bundle().apply {
                    putString(AppConstants.BUNDLE.USER_ID, userId)
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }

        })

        binding.feedRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.feedRecyclerView.adapter = adapter
    }
}