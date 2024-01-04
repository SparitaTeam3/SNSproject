package com.android.hikers

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import com.android.hikers.data.Post
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager
import com.google.android.material.textfield.TextInputLayout

class MakeNewPostActivity : AppCompatActivity() {
    private val TAG = "MakeNewPostActivity"
    private val close_btn: ImageButton by lazy { findViewById(R.id.btn_close) }
    private val upload_btn: Button by lazy { findViewById(R.id.btn_upload) }
    private val post_title: TextInputLayout by lazy { findViewById(R.id.edit_title) }
    private val post_loc: TextInputLayout by lazy { findViewById(R.id.edit_location) }
    private val post_body: EditText by lazy { findViewById(R.id.edit_postBody) }
    private val img_add_btn: ImageButton by lazy { findViewById(R.id.btn_imgAdd) }
    private val post_img: ImageView by lazy { findViewById(R.id.img_post) }

    private val postManager = PostManager.newInstance()
    private val userManager = UserManager.newInstance()

    private var imgUri: Uri? = null

    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imgUri = it.data?.data
                grantUriPermission(
                    "com.android.hikers",
                    imgUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                post_img.setPadding(0)
                post_img.setImageURI(imgUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makenewpost)

        val userId = intent.getStringExtra("userID") ?: ""
        upload_btn.isEnabled = false
        upload_btn.alpha = 0.5F

        closeBtnCheck()
        textFocusCheck()
        textErrorCheck()
        uploadBtnCheck(userId)
        imgAddBtnCheck()

    }

    private fun closeBtnCheck() {
        close_btn.setOnClickListener {
            Log.d(TAG, "close button clicked")
            finish()
        }
    }

    private fun textFocusCheck() {
        post_title.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (post_title.editText!!.text.toString().isEmpty()) {
                when (hasFocus) {
                    true -> post_title.hint = ""
                    false -> post_title.hint = getString(R.string.enter_title)
                }
            }
        }
        post_body.setOnFocusChangeListener { v, hasFocus ->
            if (post_body.text.toString().isEmpty()) {
                when (hasFocus) {
                    true -> post_body.hint = ""
                    false -> post_body.hint = getString(R.string.enter_body)
                }
            }
        }
        post_loc.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (post_loc.editText!!.text.toString().isEmpty()) {
                when (hasFocus) {
                    true -> post_loc.hint = ""
                    false -> post_loc.hint = getString(R.string.post_location)
                }
            }
        }
    }

    private fun textErrorCheck() {
        post_title.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && post_title.editText!!.text.toString().isEmpty()) {
                post_title.error = getString(R.string.title_error)
            }
        }
        post_title.editText?.addTextChangedListener { text ->
            if (text.toString().isNotEmpty()) {
                upload_btn.isEnabled = true
                upload_btn.alpha = 1f
                post_title.error = null
            }
        }
    }

    private fun uploadBtnCheck(userId: String) {
        upload_btn.setOnClickListener {
            Log.d(TAG, "upload button clicked")
            val titleStr = post_title.editText?.text.toString()
            val locStr = post_loc.editText?.text.toString()
            val bodyStr = post_body.text.toString()

            Log.d(TAG, "img uri: ${imgUri.toString()}")
            val newPostID = postManager.addNewPost(titleStr, bodyStr, userId, imgUri, locStr)
            userManager.findUserByID(userId)!!.addWrittenPostID(newPostID)

            finish()
        }
    }

    private fun imgAddBtnCheck() {
        img_add_btn.setOnClickListener {
            Log.d(TAG, "image add button clicked")

            val imgIntent =
                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getImage.launch(imgIntent)
        }
    }
}