package com.android.hikers

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.hikers.data.UserManager


class MyPage : AppCompatActivity() {
    private val info: TextView by lazy { findViewById(R.id.tv_myinfo) }
    private val more: TextView by lazy { findViewById(R.id.view_more) }
    private val name: TextView by lazy { findViewById(R.id.tv_name) }
    private val id: TextView by lazy { findViewById(R.id.tv_id) }
    private val introduction: TextView by lazy { findViewById(R.id.tv_myinfo) }
    private val profilePhoto: ImageView by lazy { findViewById(R.id.img_profile) }
    private val back: ImageButton by lazy { findViewById(R.id.btn_back) }
    private val setting: Button by lazy { findViewById(R.id.btn_edit) }
    private val userManager = UserManager.newInstance()
    private val myPageScrollView: HorizontalScrollView by lazy { findViewById(R.id.scrollview) }
    private val myPageScrollView2: HorizontalScrollView by lazy { findViewById(R.id.likescrollview) }

    //현재 로그인 한 유저의 아이디
    private val userID by lazy {
        "hong_gildong"
    }

    private val postItemList by lazy {
        listOf<ViewGroup>(
            findViewById(R.id.post_item1),
            findViewById(R.id.post_item2),
            findViewById(R.id.post_item3),
            findViewById(R.id.post_item4),
            findViewById(R.id.post_item5)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        setViewMore(info, more)

        goToMain()

        goToUserInfo()

        initProfile2()

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
                }
            }
        }
    }

    private fun initProfile2() {
        val loginUser = userManager.findUserByID(userID)!!
        val userName = loginUser.name
        val userImage = loginUser.profileImage
        val userId = loginUser.ID
        val userintroduction = loginUser.introduction

        name.text = userName
        id.text = userId
        //introduction.text = userintroduction
        profilePhoto.run {
            if (userImage != null) setImageURI(userImage)
            else setImageResource(R.drawable.default_profile)
        }
    }

    private fun goToMain() {
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goToUserInfo() {
        setting.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
    }
}