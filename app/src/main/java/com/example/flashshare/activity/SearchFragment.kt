package com.example.flashshare.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashshare.R
import com.example.flashshare.activity.adapter.SearchAdapter
import com.example.flashshare.databinding.FragmentSearchBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.model.UserModel
import com.example.flashshare.service.SearchListener
import com.example.flashshare.viewmodel.SearchViewModel

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private lateinit var viewModel: SearchViewModel
    private lateinit var usersList: MutableList<UserModel>
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        binding!!.searchUser.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null && query!=""){
                    viewModel.searchUser(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null && newText!=""){
                    viewModel.searchUser(newText)
                }
                return true
            }
        })


        usersList = mutableListOf()
        adapter = SearchAdapter(requireContext() ,usersList, object : SearchListener{
            override fun onClick(userId: String) {
                println(userId)
            }
        })

        binding!!.searchRecycleView.layoutManager = LinearLayoutManager(context)
        binding!!.searchRecycleView.adapter = adapter


        viewModel.searchProcess.observe(viewLifecycleOwner){
            when(it){
                is ResultModel.Success -> adapter.updateUsersList(it.data)
                is ResultModel.Loading -> Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show()
                is ResultModel.Error -> println("ERROR: ${it.message}")
            }
        }

        return binding!!.root
    }


}