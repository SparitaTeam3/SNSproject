package com.android.hikers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.view.isVisible
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val noPostTextView: TextView by lazy { findViewById(R.id.tv_no_post) }
    private val postScrollView: ScrollView by lazy { findViewById(R.id.scroll_view_post) }

    private val postItemList by lazy {

        listOf<ViewGroup>(
            findViewById(R.id.post_item1),
            findViewById(R.id.post_item2),
            findViewById(R.id.post_item3),
            findViewById(R.id.post_item4),
            findViewById(R.id.post_item5)
        )
    }

    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //최신 게시글 보이기
        initScrollView()
    }

    private fun initScrollView() {
        postItemList.forEach { it.isVisible = false }
        val recentPostList = postManager.getRecentPostList(3)

        //최신 글이 없는 경우
        if (recentPostList.isEmpty()) {
            noPostTextView.isVisible = true
            postScrollView.isVisible = false
            return
        }
        //최신 글이 있는 경우
        noPostTextView.isVisible = false
        postScrollView.isVisible = true
        for ((index, post) in recentPostList.withIndex()) {
            val postItem = postItemList[index]
            val imageImageView = postItem.findViewById<ImageView>(R.id.iv_img)
            val titleTextView = postItem.findViewById<TextView>(R.id.tv_title)
            val locationTextView = postItem.findViewById<TextView>(R.id.tv_location)
            val writerNameTextView = postItem.findViewById<TextView>(R.id.tv_writer_name)
            val timeTextView = postItem.findViewById<TextView>(R.id.tv_time)

            imageImageView.run {
                if (post.image != null) {
                    setImageURI(post.image)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    setImageResource(R.drawable.hikers_icon_small_grey)
                    scaleType = ImageView.ScaleType.CENTER
                }
            }
            titleTextView.text = post.title
            locationTextView.text = post.location
            writerNameTextView.text = userManager.findUserByID(post.writerId)!!.name
            timeTextView.text = post.writtenTime

            postItem.isVisible = true
        }
    }
}