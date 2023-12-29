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
import androidx.core.view.setPadding
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager
import com.google.android.material.chip.ChipGroup
import kotlin.math.log
import kotlin.math.min


class MyPage : AppCompatActivity() {

    private val back: ImageButton by lazy { findViewById(R.id.btn_back) }
    private val setting: Button by lazy { findViewById(R.id.btn_edit) }

    private val profilePhoto: ImageView by lazy { findViewById(R.id.img_profile) }
    private val name: TextView by lazy { findViewById(R.id.tv_name) }
    private val id: TextView by lazy { findViewById(R.id.tv_id) }
    private val info: TextView by lazy { findViewById(R.id.tv_myinfo) }
    private val more: TextView by lazy { findViewById(R.id.view_more) }

    private val myPostScrollView: HorizontalScrollView by lazy { findViewById(R.id.scrollview_my_post) }
    private val noWrittenPostTextView: TextView by lazy { findViewById(R.id.tv_no_my_post) }
    private val likedPostScrollView: HorizontalScrollView by lazy { findViewById(R.id.scrollview_my_like) }
    private val noLikedPostTextView: TextView by lazy { findViewById(R.id.tv_no_liked_post) }
    private val character1: TextView by lazy { findViewById(R.id.tv_character1) }
    private val character2: TextView by lazy { findViewById(R.id.tv_character2) }
    private val character3: TextView by lazy { findViewById(R.id.tv_character3) }


    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()

    //현재 로그인 한 유저의 아이디
    private val userID by lazy {
        intent.getStringExtra(EXTRA_ID) ?: "hong_gildong"
    }

    private val myPostItemList by lazy {
        listOf<ViewGroup>(
            findViewById(R.id.mypage_mypost1),
            findViewById(R.id.mypage_mypost2),
            findViewById(R.id.mypage_mypost3),
            findViewById(R.id.mypage_mypost4),
            findViewById(R.id.mypage_mypost5)
        )
    }
    private val likedPostItemList by lazy {
        listOf<ViewGroup>(
            findViewById(R.id.mypage_likepost1),
            findViewById(R.id.mypage_likepost2),
            findViewById(R.id.mypage_likepost3),
            findViewById(R.id.mypage_likepost4),
            findViewById(R.id.mypage_likepost5)
        )
    }

    private val myPostItemIDMap by lazy {
        mutableMapOf<Int, Int>(
            R.id.mypage_mypost1 to -1,
            R.id.mypage_mypost2 to -1,
            R.id.mypage_mypost3 to -1,
            R.id.mypage_mypost4 to -1,
            R.id.mypage_mypost5 to -1
        )
    }
    private val likedPostItemIDMap by lazy {
        mutableMapOf<Int, Int>(
            R.id.mypage_likepost1 to -1,
            R.id.mypage_likepost2 to -1,
            R.id.mypage_likepost3 to -1,
            R.id.mypage_likepost4 to -1,
            R.id.mypage_likepost5 to -1
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        setViewMore(info, more)

        //프로필 정보 표시하기
        initProfile()

        //내가 작성한 게시물 목록 표시하기
        initMyPostScrollView()
        //내가 좋아한 게시물 목록 표시하기
        initLikedPostScrollView()

        //게시물 클릭 이벤트 처리하기
        initPostItem()

        goToMain()
        goToUserInfo()
    }

    override fun onResume() {
        super.onResume()

        //디테일 페이지에서 좋아요를 누르거나 취소한 뒤, 마이 페이지로 돌아왔을 때,
        //좋아요 여부를 반영하여 다시 UI 표시하기
        initProfile()
        initLikedPostScrollView()
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
        val userId = "@ " + loginUser.ID
        val userIntroduction = loginUser.introduction

        character1.isVisible = false
        character2.isVisible = false
        character3.isVisible = false

        if (loginUser.character.size != 0) {
            if (loginUser.character.size == 1) {
                character1.isVisible = true
                val userCharacter1 = loginUser.character[0]

                character1.text = userCharacter1
            } else if (loginUser.character.size == 2) {
                character1.isVisible = true
                character2.isVisible = true
                val userCharacter1 = loginUser.character[0]
                val userCharacter2 = loginUser.character[1]

                character1.text = userCharacter1
                character2.text = userCharacter2
            } else if (loginUser.character.size == 3) {
                character1.isVisible = true
                character2.isVisible = true
                character3.isVisible = true
                val userCharacter1 = loginUser.character[0]
                val userCharacter2 = loginUser.character[1]
                val userCharacter3 = loginUser.character[2]

                character1.text = userCharacter1
                character2.text = userCharacter2
                character3.text = userCharacter3
            }


        }


        name.text = userName
        id.text = userId
        //아직 소개가 없어서 주석 처리 함
        info.text = userIntroduction
        profilePhoto.run {
            if (userImage != null) setImageURI(userImage)
            else setImageResource(R.drawable.default_profile)
        }
        info.run {
            if (userIntroduction != null) text = userIntroduction
            else setText(R.string.introduction)
        }
    }


    private fun initMyPostScrollView() {
        val loginUser = userManager.findUserByID(userID)!!
        val myPostIDList = loginUser.writtenPostIDList

        if (myPostIDList.isEmpty()) {
            noWrittenPostTextView.isVisible = true
            myPostScrollView.isVisible = false
            return
        }

        noWrittenPostTextView.isVisible = false
        myPostScrollView.isVisible = true
        myPostItemList.forEach { it.isVisible = false }

        //내가 작성한 게시물 중 최근에 작성한 5개의 게시물만 표시하기
        for (index in 0 until min(5, myPostIDList.size)) {
            //표시할 게시물 ID
            val postID = myPostIDList[myPostIDList.size - 1 - index]
            //게시물이 표시될 postItem 레이아웃
            val postItem = myPostItemList[index]

            myPostItemIDMap[postItem.id] = postID
            setPostItemUI(postItem, postID)
        }
    }

    private fun initLikedPostScrollView() {
        val loginUser = userManager.findUserByID(userID)!!
        val likedPostIDList = loginUser.likedPostIDList

        Log.d(TAG, "userID: ${userID}, likedPostList: ")
        for (postID in likedPostIDList) {
            Log.d(TAG, "$postID")
        }

        if (likedPostIDList.isEmpty()) {
            noLikedPostTextView.isVisible = true
            likedPostScrollView.isVisible = false
            return
        }

        noLikedPostTextView.isVisible = false
        likedPostScrollView.isVisible = true
        likedPostItemList.forEach { it.isVisible = false }

        //내가 좋아한 게시물 중 최근에 작성한 5개의 게시물만 표시하기
        for (index in 0 until min(5, likedPostIDList.size)) {
            //표시할 게시물 ID
            val postID = likedPostIDList[likedPostIDList.size - 1 - index]
            //게시물이 표시될 postItem 레이아웃
            val postItem = likedPostItemList[index]

            likedPostItemIDMap[postItem.id] = postID
            setPostItemUI(postItem, postID)
        }
    }

    private fun setPostItemUI(postItem: ViewGroup, postID: Int) {
        postItem.isVisible = true

        val imageView = postItem.findViewById<ImageView>(R.id.pt_image)
        val titleText = postItem.findViewById<TextView>(R.id.tv_post_title)
        val nameText = postItem.findViewById<TextView>(R.id.tv_post_name)

        val post = postManager.findPostByID(postID)!!
        imageView.run {
            if (post.image != null) {
                try {
                    Log.d(TAG, "postID: ${post.postID}, post image is not null")
                    setImageURI(post.image)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setPadding(0)
                } catch (e: Exception) {
                    Log.d(TAG, "게시물 이미지 접근 오류 발생!")
                    setImageResource(R.drawable.hikers_icon_small_grey)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setPadding(20)
                }
            } else {
                Log.d(TAG, "postID: ${post.postID}, post image is null")
                setImageResource(R.drawable.hikers_icon_small_grey)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setPadding(20)
            }
        }
        titleText.text = post.title
        nameText.text = userManager.findUserByID(post.writerId)!!.name
    }

    private fun initPostItem() {
        //내가 작성한 게시글
        for (postItem in myPostItemList) {
            //게시글 클릭 이벤트 처리하기
            postItem.setOnClickListener {
                val postID = myPostItemIDMap[postItem.id]!!
                Log.d(TAG, "post item clicked) post id: ${postID}")

                if (postID == -1) return@setOnClickListener

                goToDetail(postID)
            }
        }
        //내가 좋아요한 게시글
        for (postItem in likedPostItemList) {
            //게시글 클릭 이벤트 처리하기
            postItem.setOnClickListener {
                val postID = likedPostItemIDMap[postItem.id]!!
                Log.d(TAG, "post item clicked) post id: ${postID}")

                if (postID == -1) return@setOnClickListener

                goToDetail(postID)
            }
        }
    }

    private fun goToDetail(postID: Int) {
        //로그인한 회원 ID와 게시물 ID 전달하며, 디테일 화면으로 이동
        val detailIntent = Intent(this, DetailPageActivity::class.java).apply {
            putExtra("userID", userID)
            putExtra("postID", postID)
            //마이페이지에서 디테일 화면을 시작했음을 전달
            putExtra("activityName", "MyPage")
        }
        startActivity(detailIntent)
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
                putExtra(EXTRA_ID, userID)
            }
            startActivity(editIntent)
        }
    }
}