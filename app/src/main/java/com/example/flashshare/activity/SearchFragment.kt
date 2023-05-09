package com.example.flashshare.activity

import android.content.Intent
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
import com.example.flashshare.service.AppConstants
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
                val bundle = Bundle().apply {
                    putString(AppConstants.BUNDLE.USER_ID, userId)
                }

                val intent = Intent(context, FriendProfileActivity::class.java).apply {
                    putExtras(bundle)
                }

                startActivity(intent)
            }
        })

        binding!!.searchRecycleView.layoutManager = LinearLayoutManager(context)
        binding!!.searchRecycleView.adapter = adapter


        observer()


        return binding!!.root
    }

    private fun observer(){
        viewModel.searchProcess.observe(viewLifecycleOwner){
            when(it){
                is ResultModel.Success -> {
                    adapter.updateUsersList(it.data)
                }
                is ResultModel.Loading -> {}
                is ResultModel.Error -> Toast.makeText(context, getString(R.string.error_message), Toast.LENGTH_LONG).show()
            }
        }
    }

}