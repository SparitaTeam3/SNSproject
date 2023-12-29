package com.android.hikers

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager
import com.google.android.material.chip.ChipGroup


class MyPage : AppCompatActivity() {
    private val info: TextView by lazy { findViewById(R.id.tv_myinfo) }
    private val more: TextView by lazy { findViewById(R.id.view_more) }
    private val name: TextView by lazy { findViewById(R.id.tv_name) }
    private val id: TextView by lazy { findViewById(R.id.tv_id) }
    private val introduction: TextView by lazy { findViewById(R.id.tv_myinfo) }
    private val noPost: TextView by lazy { findViewById(R.id.tv_no_recent) }
    private val profilePhoto: ImageView by lazy { findViewById(R.id.img_profile) }
    private val back: ImageButton by lazy { findViewById(R.id.btn_back) }
    private val setting: Button by lazy { findViewById(R.id.btn_edit) }
    private val myPageScrollView: HorizontalScrollView by lazy { findViewById(R.id.scrollview) }
    private val myPageScrollView2: HorizontalScrollView by lazy { findViewById(R.id.likescrollview) }
    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()


    //현재 로그인 한 유저의 아이디
    private val userID by lazy {
        "lee_younghee"
        //intent.getStringExtra("userID") ?: ""
    }

    private val postItemList by lazy {
        listOf<ViewGroup>(
            findViewById(R.id.mypage_post1),
            findViewById(R.id.mypage_post2),
            findViewById(R.id.mypage_post3),
            findViewById(R.id.mypage_post4),
            findViewById(R.id.mypage_post5)
        )
    }

    private val postItemIDMap by lazy {
        mutableMapOf<Int, Int>(
            R.id.mypage_post1 to -1,
            R.id.mypage_post2 to -1,
            R.id.mypage_post3 to -1,
            R.id.mypage_post4 to -1,
            R.id.mypage_post5 to -1
        )
    }
    private val userTags by lazy {
        listOf<TextView>(
            findViewById(R.id.tv_tag1),
            findViewById(R.id.tv_tag2),
            findViewById(R.id.tv_tag3)
        )
    }

    private val usertagsMap by lazy {
        mutableMapOf<Int, Int>(
            R.id.tv_tag1 to -1,
            R.id.tv_tag2 to -1,
            R.id.tv_tag3 to -1
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        setViewMore(info, more)

        initProfile()

        initHorizontalScrollView()

        initPostItem()

        goToMain()

        goToUserInfo()

    }

    private fun setViewMore(contentTextView: TextView, viewMoreTextView: TextView) {
        contentTextView.post {
            val lineCount = contentTextView.layout.lineCount
            if (lineCount > 0) {
                if (contentTextView.layout.getEllipsisCount(lineCount - 1) > 0) {
                    // 더보기 표시
                    viewMoreTextView.visibility = View.VISIBLE

                    // 더보기 클릭 이벤트
                    viewMoreTextView.setOnClickListener {
                        contentTextView.maxLines = Int.MAX_VALUE
                        viewMoreTextView.visibility = View.GONE
                        info.setMovementMethod(ScrollingMovementMethod())
                    }
                } else {
                    viewMoreTextView.visibility = View.GONE
                }
            }
        }
    }

    private fun initProfile() {
        val loginUser = userManager.findUserByID(userID)!!
        val userName = loginUser.name
        val userImage = loginUser.profileImage
        val userId = loginUser.ID
        val userintroduction = loginUser.introduction



        name.text = userName
        id.text = userId
        //아직 소개가 없어서 주석 처리 함
        //introduction.text = userintroduction
        profilePhoto.run {
            if (userImage != null) setImageURI(userImage)
            else setImageResource(R.drawable.default_profile)
        }
    }


    private fun initHorizontalScrollView() {
        postItemList.forEach { it.isVisible = false }
        val recentPostList = postManager.getRecentPostList(3)

        if (recentPostList.isEmpty()) {
            noPost.isVisible = true
            myPageScrollView.isVisible = false
            return
        }

        noPost.isVisible = false
        myPageScrollView.isVisible = true
        for ((index, post) in recentPostList.withIndex()) {
            val postItem = postItemList[index]

            postItemIDMap[postItem.id] = post.postID
            postItem.isVisible = true

            val imageView = postItem.findViewById<ImageView>(R.id.pt_image)
            val titleText = postItem.findViewById<TextView>(R.id.tv_post_title)
            val nameText = postItem.findViewById<TextView>(R.id.tv_post_name)

            //아직 사진이 없어서 주석 처리 함
//            imageView.run {
//                if (post.image != null) {
//                    setImageURI(post.image)
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                } else {
//                    setImageResource(R.drawable.hikers_icon_small_grey)
//                    scaleType = ImageView.ScaleType.CENTER
//                }
//            }
            titleText.text = post.title
            nameText.text = userManager.findUserByID(post.writerId)!!.name
        }
    }

    private fun initPostItem() {
        for (postItem in postItemList) {
            //게시글 클릭 이벤트 처리하기
            postItem.setOnClickListener {
                val postID = postItemIDMap[postItem.id]
                Log.d(TAG, "post item clicked) post id: ${postID}")

                if (postID == -1) return@setOnClickListener

                //로그인한 회원 ID와 게시물 ID 전달하며, 디테일 화면으로 이동
                val detailIntent = Intent(this, DetailPageActivity::class.java).apply {
                    putExtra("userID", userID)
                    putExtra("postID", postID)
                }
                startActivity(detailIntent)
            }
        }
    }

    private fun goToMain() {
        back.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish()
        }
    }

    private fun goToUserInfo() {
        setting.setOnClickListener {
            Log.d(TAG, "edit info button clicked")
            val editIntent = Intent(this, UserInfoActivity::class.java).apply {
                putExtra("userID", userID)
            }
            startActivity(editIntent)
        }
    }
}