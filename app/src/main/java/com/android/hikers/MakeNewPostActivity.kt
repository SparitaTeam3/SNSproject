package com.android.hikers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
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

    private val postManager=PostManager.newInstance()
    private val userManager=UserManager.newInstance()

    private var imgUri: Uri? = null

    private val getImage=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            imgUri = it.data?.data
            post_img.setPadding(0)
            post_img.setImageURI(imgUri)
        }
    }
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makenewpost)

        val userId=intent.getStringExtra("userID") ?: ""

        closeBtnCheck()
        uploadBtnCheck(userId)
        imgAddBtnCheck()

    }

    private fun closeBtnCheck(){
        close_btn.setOnClickListener {
            Log.d(TAG, "close button clicked")
            finish()
        }
    }

    private fun uploadBtnCheck(userId: String){
        upload_btn.setOnClickListener{
            Log.d(TAG, "upload button clicked")
            val titleStr=post_title.editText?.text.toString()
            val locStr=post_loc.editText?.text.toString()
            val bodyStr=post_body.text.toString()

            Log.d(TAG, "img uri: ${imgUri.toString()}")
            postManager.addNewPost(titleStr, bodyStr, userId, imgUri, locStr)
            val postID=postManager.getRecentPostList(1)[0].postID
            userManager.findUserByID(userId)?.writtenPostIDList?.add(postID)
            finish()
        }
    }

    private fun imgAddBtnCheck(){
        img_add_btn.setOnClickListener {
            Log.d(TAG, "image add button clicked")

            val imgIntent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getImage.launch(imgIntent)
        }
    }
}
