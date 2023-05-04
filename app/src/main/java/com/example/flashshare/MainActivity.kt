package com.example.flashshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flashshare.activity.HomeFragment
import com.example.flashshare.activity.LoginActivity
import com.example.flashshare.activity.PostsFragment
import com.example.flashshare.activity.ProfileFragment
import com.example.flashshare.activity.SearchFragment
import com.example.flashshare.databinding.ActivityMainBinding
import com.example.flashshare.model.ResultModel
import com.example.flashshare.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var currentFragment: Fragment = HomeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.mainToolbar.setTitle(R.string.app_name)
        setSupportActionBar(binding.toolbar.mainToolbar)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        observe()

        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    currentFragment = HomeFragment()
                }
                R.id.nav_search -> {
                    currentFragment = SearchFragment()
                }
                R.id.nav_posts -> {
                    currentFragment = PostsFragment()
                }
                R.id.nav_profile -> {
                    currentFragment = ProfileFragment()
                }
            }

            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, currentFragment)
                commit()
            }
            true
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, currentFragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logoutItem -> viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observe(){
        viewModel.logoutProcess.observe(this){
            when(it){
                is ResultModel.Success -> closeActivity()
                is ResultModel.Error -> Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                is ResultModel.Loading -> Unit
            }
        }
    }
    private fun closeActivity(){
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    private fun setTitleToolbar(text: String){
        binding.toolbar.mainToolbar.setTitle(R.string.app_name)
    }
}