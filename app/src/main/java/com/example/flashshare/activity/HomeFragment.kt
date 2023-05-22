package com.example.flashshare.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.R
import com.example.flashshare.databinding.FragmentHomeBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.viewmodel.FeedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
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

                }
                is ResultModel.Loading ->{}
                is ResultModel.Error ->{println("ERROR -> ${it.message}")}
            }
        }
    }

}