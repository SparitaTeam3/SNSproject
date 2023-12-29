package com.android.hikers

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isVisible
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val userGreetingTextView: TextView by lazy { findViewById(R.id.tv_user_greeting) }
    private val userProfileImageView: ImageView by lazy { findViewById(R.id.iv_user_profile) }

    private val noPostTextView: TextView by lazy { findViewById(R.id.tv_no_post) }
    private val postScrollView: ScrollView by lazy { findViewById(R.id.scroll_view_post) }

    private val writeFloatingButton: FloatingActionButton by lazy { findViewById(R.id.floating_btn_write) }

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
    private val postItemIDMap by lazy {
        mutableMapOf<Int, Int>(
            R.id.post_item1 to -1,
            R.id.post_item2 to -1,
            R.id.post_item3 to -1,
            R.id.post_item4 to -1,
            R.id.post_item5 to -1
        )
    }

    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()

    //현재 로그인 한 유저의 아이디
    private val userID by lazy {
        intent.getStringExtra(EXTRA_ID) ?: "lee_younghee"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //화면 상단 프로필 영역
        initProfile()
        //프로필 이미지 클릭 이벤트 처리하기
        initProfileImageView()

        //최신 게시글 보이기
        initScrollView()
        //게시글 클릭 이벤트 처리하기
        initPostItem()

        //글쓰기 버튼 클릭 이벤트 처리하기
        initWriteFloatingButton()
    }

    override fun onResume() {
        super.onResume()

        //최신 게시글 다시 보이기
        initScrollView()
    }

    private fun initProfile() {
        val loginUser = userManager.findUserByID(userID)!!
        val userName = loginUser.name
        val userImage = loginUser.profileImage

        userGreetingTextView.text = getString(R.string.user_greeting, userName)
        userProfileImageView.run {
            if (userImage != null){
                try{
                    Log.d(TAG, "user profile image uri is not null")
                    setImageURI(userImage)
                }
                catch(e:Exception){
                    Log.d(TAG, "프로필 이미지 uri 접근 문제 발생!")
                    setImageResource(R.drawable.default_profile)
                }
            }
            else {
                Log.d(TAG, "user profile image uri is null")
                setImageResource(R.drawable.default_profile)
            }
        }
    }


    private fun initProfileImageView() {
        userProfileImageView.setOnClickListener {
            Log.d(TAG, "profile image clicked")
            //로그인한 회원 ID 전달하며, 마이페이지 화면으로 이동
            val profileIntent = Intent(this, MyPage::class.java).apply {
                putExtra(EXTRA_ID, userID)
            }

            //공유 요소가 있는 화면 애니메이션 만들기
            val options = ActivityOptions
                .makeSceneTransitionAnimation(this, userProfileImageView, resources.getString(R.string.trans_profile_image))

            startActivity(profileIntent, options.toBundle())
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

            postItemIDMap[postItem.id] = post.postID
            postItem.isVisible = true

            //게시글 정보 표시하기
            val imageImageView = postItem.findViewById<ImageView>(R.id.iv_img)
            val titleTextView = postItem.findViewById<TextView>(R.id.tv_title)
            val locationTextView = postItem.findViewById<TextView>(R.id.tv_location)
            val writerNameTextView = postItem.findViewById<TextView>(R.id.tv_writer_name)
            val timeTextView = postItem.findViewById<TextView>(R.id.tv_time)

            imageImageView.run {
                if (post.image != null) {
                    try {
                        Log.d(TAG, "postID: ${post.postID}, image uri is not null")
                        setImageURI(post.image)
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    catch (e:Exception){
                        Log.d(TAG, "게시물 이미지 uri 접근 문제 발생!")
                        setImageResource(R.drawable.hikers_icon_small_grey)
                        scaleType = ImageView.ScaleType.CENTER
                    }
                } else {
                    Log.d(TAG, "postID: ${post.postID}, image uri is null")
                    setImageResource(R.drawable.hikers_icon_small_grey)
                    scaleType = ImageView.ScaleType.CENTER
                }
            }
            titleTextView.text = post.title
            locationTextView.text = post.location
            writerNameTextView.text = userManager.findUserByID(post.writerId)!!.name
            timeTextView.text = post.writtenTime

            //게시글 좋아요 여부 표시하기
            val heartImageView = postItem.findViewById<ImageView>(R.id.iv_heart)

            val loginUser = userManager.findUserByID(userID)!!
            heartImageView.setImageResource(
                if (loginUser.isInLikedPostIDList(post.postID)) R.drawable.full_heart_icon
                else R.drawable.empty_heart_icon
            )
        }
    }

    private fun initPostItem() {
        for (postItem in postItemList) {
            //좋아요 클릭 이벤트 처리하기
            val heartImageView = postItem.findViewById<ImageView>(R.id.iv_heart)
            heartImageView.setOnClickListener {
                val postID = postItemIDMap[postItem.id]!!
                Log.d(TAG, "post item heart clicked) post id: ${postID}")

                //좋아요 여부 확인하기
                val loginUser = userManager.findUserByID(userID)!!

                //이미 좋아요한 경우, 좋아요 삭제하기
                if (loginUser.isInLikedPostIDList(postID)) {
                    heartImageView.setImageResource(R.drawable.empty_heart_icon)
                    loginUser.deleteLikedPostID(postID)

                    postManager.findPostByID(postID)!!.minusHeartCount()
                }
                //이미 좋아요하지 않은 경우, 좋아요 추가하기
                else {
                    heartImageView.setImageResource(R.drawable.full_heart_icon)
                    loginUser.addLikedPostID(postID)

                    postManager.findPostByID(postID)!!.plusHeartCount()
                }
            }

            //게시글 클릭 이벤트 처리하기
            postItem.setOnClickListener {
                val postID = postItemIDMap[postItem.id]
                Log.d(TAG, "post item clicked) post id: ${postID}")

                if(postID == -1) return@setOnClickListener

                //로그인한 회원 ID와 게시물 ID 전달하며, 디테일 화면으로 이동
                val detailIntent = Intent(this, DetailPageActivity::class.java).apply{
                    putExtra("userID", userID)
                    putExtra("postID", postID)
                }

                //메인 화면과 디테일 화면의 공유 요소
                val postImageView = postItem.findViewById<ImageView>(R.id.iv_img)
                //공유 요소가 있는 화면 애니메이션 만들기
                val options = ActivityOptions
                    .makeSceneTransitionAnimation(this, postImageView,getString(R.string.trans_post_image))

                startActivity(detailIntent, options.toBundle())
            }
        }
    }

    private fun initWriteFloatingButton() {
        writeFloatingButton.setOnClickListener {
            Log.d(TAG, "write button clicked")

            //TODO 로그인한 회원 ID 전달하며, 글쓰기 화면으로 이동
        }
    }
}