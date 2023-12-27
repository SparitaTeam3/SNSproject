package com.android.hikers

import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val userGreetingTextView: TextView by lazy{findViewById(R.id.tv_user_greeting)}
    private val userProfileImageView : ImageView by lazy{findViewById(R.id.iv_user_profile)}

    private val noPostTextView: TextView by lazy { findViewById(R.id.tv_no_post) }
    private val postScrollView: ScrollView by lazy { findViewById(R.id.scroll_view_post) }

    private val writeFloatingButton: FloatingActionButton by lazy{ findViewById(R.id.floating_btn_write)}

    private val postItemList by lazy {
        listOf<ViewGroup>(
            findViewById(R.id.post_item1),
            findViewById(R.id.post_item2),
            findViewById(R.id.post_item3),
            findViewById(R.id.post_item4),
            findViewById(R.id.post_item5)
        )
    }
    //postItemIDMap[postItem]: postItem이 표시하는 게시물 ID
    private val postItemIDMap by lazy{
        mutableMapOf<Int,Int>(
            R.id.post_item1 to -1,
            R.id.post_item2 to -1,
            R.id.post_item3 to -1,
            R.id.post_item4 to -1,
            R.id.post_item5 to -1)
    }

    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()

    //현재 로그인 한 유저의 아이디
    private val userID by lazy{
        intent.getStringExtra("userID") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //화면 상단 프로필 영역 설정하기
        //initProfile()

        //최신 게시글 보이기
        initScrollView()

        //게시글 클릭 이벤트 처리하기
        initPostItem()

        //글쓰기 버튼 클릭 이벤트 처리하기
        initWriteFloatingButton()
    }

    private fun initProfile(){
        val loginUser = userManager.findUserByID(userID)!!
        val userName = loginUser.name
        val userImage = loginUser.profileImage

        userGreetingTextView.text = getString(R.string.user_greeting, userName)
        userProfileImageView.run{
            if(userImage != null) setImageURI(userImage)
            else setImageResource(R.drawable.default_profile)
        }
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

            postItemIDMap[postItem.id] = post.postID
            postItem.isVisible = true
        }
    }

    private fun initPostItem(){
        for(postItem in postItemList){
            postItem.setOnClickListener {
                val postID = postItemIDMap[postItem.id]
                Log.d(TAG, "post item clicked) post id: ${postID}")

                if(postID == -1) return@setOnClickListener
                //TODO 게시물 ID 전달하며, 디테일 화면으로 이동
            }
        }
    }

    private fun initWriteFloatingButton(){
        writeFloatingButton.setOnClickListener {
            Log.d(TAG, "write button clicked")
            //TODO 로그인한 회원 ID 전달하며, 글쓰기 화면으로 이동
        }
    }
}