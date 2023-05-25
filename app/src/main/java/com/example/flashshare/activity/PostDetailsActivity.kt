package com.example.flashshare.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.flashshare.R
import com.example.flashshare.activity.adapter.CommentAdapter
import com.example.flashshare.databinding.ActivityPostDetailsBinding
import com.example.flashshare.databinding.EditTextAlertDialogBinding
import com.example.flashshare.model.CommentModel
import com.example.flashshare.model.PostModel
import com.example.flashshare.model.ResultModel
import com.example.flashshare.service.AppConstants
import com.example.flashshare.service.listener.CommentListener
import com.example.flashshare.viewmodel.PostDetailsViewModel
import java.util.Calendar

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private lateinit var viewModel: PostDetailsViewModel
    private lateinit var postDetails: PostModel
    private var postId: String = ""
    private var friendId: String? = ""
    private var commentList: List<CommentModel> = mutableListOf()
    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PostDetailsViewModel::class.java]

        setRecyclerView()

        setToolBar()

        setObserve()

        getIntentData()

        setButtons()

        println("TESTE: $friendId  ${friendId is String}" )
        if(friendId == null){
            binding.optionsPost.visibility = View.VISIBLE
            binding.optionsPost.setOnClickListener {
                setOptionMenuPost(binding.optionsPost)
            }
        }

    }

    override fun onStart() {
        if(postId != ""){
            viewModel.getPost(postId)
        }
        super.onStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return false
    }

    private fun setObserve() {
        viewModel.loadPostProcess.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    postDetails = it.data
                    viewModel.getUserData(it.data.userId)
                    binding.descriptionPostText.text = it.data.description
                    Glide.with(this).load(it.data.urlPhotoPost)
                        .into(binding.imagePost)
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.isLiked.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    if (it.data) {
                        binding.likeImage.setImageResource(R.drawable.ic_like)
                    } else {
                        binding.likeImage.setImageResource(R.drawable.ic_not_like)
                    }
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.changeLikeProcess.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    viewModel.checkLikedPost(postId)
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.createCommentProcess.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    getComments()
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.loadCommentsProcess.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    commentList = it.data
                    adapter.updateCommentsList(commentList)
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }

        viewModel.loadUserProcess.observe(this) {
            when (it) {
                is ResultModel.Success -> {
                    Glide.with(this).load(it.data.urlPhotoProfile)
                        .into(binding.userCircleAvatarView)
                    binding.nameTextView.text = it.data.name
                }

                is ResultModel.Error -> {}
                is ResultModel.Loading -> {}
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setRecyclerView() {
        adapter = CommentAdapter(this, commentList, object : CommentListener {
            override fun onClickEdit(comment: CommentModel) {
                showCommentAlertDialog(comment)
            }

            override fun onClickDelete(commentId: String) {
                viewModel.removeComment(postId, commentId)
            }
        })

        binding.commentRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.commentRecyclerView.adapter = adapter

    }

    private fun setButtons() {
        binding.likeImage.setOnClickListener {
            if (friendId != "" && friendId != null) {
                viewModel.changeLike(postId)
            }
        }

        binding.commentImage.setOnClickListener {
            if (friendId != "" && friendId != null) {
                showCommentAlertDialog()
            }
        }
    }

    private fun getIntentData() {
        val bundle = intent.extras

        if (bundle != null) {
            postId = bundle.getString(AppConstants.BUNDLE.POST_ID).toString()
            friendId = bundle.getString(AppConstants.BUNDLE.USER_ID)
            getPostDetails()
            getComments()
        } else {
            showToast(getString(R.string.error_message))
            finish()
        }
    }

    private fun showCommentAlertDialog(commentModel: CommentModel? = null) {
        val view = EditTextAlertDialogBinding.inflate(layoutInflater)
        if(commentModel!=null){
            view.commentEditText.setText(commentModel.description)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.comment))
        alertDialogBuilder.setMessage(getString(R.string.input_comment))
        alertDialogBuilder.setView(view.root)
        alertDialogBuilder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
            if(commentModel == null){
                val comment = CommentModel()
                comment.description = view.commentEditText.text.toString()
                comment.date = Calendar.getInstance().timeInMillis.toString()
                viewModel.createComments(postId, comment)
            }else{
                commentModel.description = view.commentEditText.text.toString()
                viewModel.updateComment(postId, commentModel.id, commentModel)
            }

        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negative_button)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()

        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

            val spannableString = SpannableString(positiveButton.text)
            spannableString.setSpan(
                TextAppearanceSpan(this, R.style.MyAlertDialogButtonStyle),
                0,
                spannableString.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            positiveButton.text = spannableString

            val spannableString2 = SpannableString(negativeButton.text)
            spannableString2.setSpan(
                TextAppearanceSpan(this, R.style.MyAlertDialogButtonStyle),
                0,
                spannableString2.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            negativeButton.text = spannableString2
        }

        alertDialog.show()
    }

    private fun getPostDetails() {
        if (friendId != null && friendId != "") {
            viewModel.getPost(postId)
            viewModel.checkLikedPost(postId)
        } else {
            viewModel.getPost(postId)

        }
    }

    private fun getComments() {
        viewModel.getComments(postId)
    }

    private fun setToolBar() {
        binding.toolbarPostDetails.mainToolbar.setTitle(R.string.post_details)
        setSupportActionBar(binding.toolbarPostDetails.mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    private fun showDeleteAlertDialog(){
        AlertDialog.Builder(this)
        .setTitle(getString(R.string.alert_dialog_warning))
        .setMessage(getString(R.string.alert_dialog_delete_post))
            .setPositiveButton(getString(R.string.positive_button)){_, _ ->
                viewModel.deletePost(postId)
                finish()
            }
            .setNegativeButton(getString(R.string.negative_button)){dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    private fun setOptionMenuPost(view: View){
        val popupMenu = PopupMenu(this , view)
        popupMenu.inflate(R.menu.menu_options_post)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when(item?.itemId){
                    R.id.delete_option -> {
                        showDeleteAlertDialog()
                        return true
                    }
                    R.id.edit_option -> {
                        val intent = Intent(applicationContext, PublicationPostActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString(AppConstants.BUNDLE.POST_ID, postId)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        return true
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }
}