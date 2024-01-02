package com.android.hikers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.android.hikers.data.PostManager
import com.android.hikers.data.UserManager

class DetailPageActivity : AppCompatActivity() {

    private val TAG = "DetailPageActivity"

    private val backButton: ImageButton by lazy { findViewById(R.id.detail_btn_back) }

    private val imageImageView: ImageView by lazy { findViewById(R.id.detail_img) }
    private val titleTextView: TextView by lazy { findViewById(R.id.detail_title) }
    private val profileImageView: ImageView by lazy { findViewById(R.id.detail_profileImg) }
    private val writerNameTextView: TextView by lazy { findViewById(R.id.detail_name) }
    private val writtenTimeTextView: TextView by lazy { findViewById(R.id.detail_howLongAgo) }

    private val locationTextView: TextView by lazy { findViewById(R.id.detail_where) }
    private val bodyTextView: TextView by lazy { findViewById(R.id.detail_postTxt) }
    private val heartButton: ImageButton by lazy { findViewById(R.id.detail_btn_heart) }
    private val heartNumTextView: TextView by lazy { findViewById(R.id.detail_heartNum) }

    private val preButton: ImageButton by lazy { findViewById(R.id.detail_btn_pre) }
    private val preTextView:TextView by lazy{findViewById(R.id.detail_txt_pre)}
    private val nextButton: ImageButton by lazy { findViewById(R.id.detail_btn_next) }
    private val nextTextView:TextView by lazy{findViewById(R.id.detail_txt_next)}

    private val userID by lazy {
        intent.getStringExtra("userID") ?: ""
    }
    private var postID = 0

    private val userManager = UserManager.newInstance()
    private val postManager = PostManager.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailpage)
        //본문 TextView 스크롤 추가
        bodyTextView.movementMethod = ScrollingMovementMethod()

        //현재 화면에 표시할 게시물ID 
        postID = intent?.getIntExtra("postID", 0) ?: 0

        //디테일 화면을 시작시킨 액티비티 이름
        val activityName = intent?.getStringExtra("activityName") ?: ""
        if(activityName == "MyPage"){
            preButton.isVisible = false
            preTextView.isVisible = false
            nextButton.isVisible = false
            nextTextView.isVisible = false
        }

        //뒤로가기 버튼 클릭 이벤트 처리
        initBackButton()

        //게시물 화면에 표시
        initDetailPage()

        //하트 클릭 이벤트 처리
        initHeartButton()

        //이전 게시물 버튼 클릭 이벤트 처리
        initPreButton()
        //다음 게시물 버튼 클릭 이벤트 처리
        initNextButton()
    }

    private fun initBackButton(){
        backButton.setOnClickListener {
            //디테일 화면 종료 -> 메인 화면으로 돌아가기
            finish()
        }
    }

    private fun initDetailPage() {
        val currentPost = postManager.findPostByID(postID)!!

        currentPost.run {
            //게시물 이미지 설정
            if (image != null) {
                try {
                    imageImageView.setImageURI(image)
                    imageImageView.scaleType = ImageView.ScaleType.CENTER_CROP
                }
                catch (e:Exception){
                    Log.d(TAG, "게시물 이미지 uri 접근 문제 발생!")
                    imageImageView.setImageResource(R.drawable.hikers_icon_small_grey)
                    imageImageView.scaleType = ImageView.ScaleType.CENTER
                }
            } else {
                imageImageView.setImageResource(R.drawable.hikers_icon_small_grey)
                imageImageView.scaleType = ImageView.ScaleType.CENTER
            }

            //게시물 제목 설정
            titleTextView.text = title

            //작성자 User 정보 가져오기
            userManager.findUserByID(writerId)!!.run {
                //프로필 이미지 설정
                profileImageView.run {
                    if (profileImage != null) setImageURI(profileImage)
                    else setImageResource(R.drawable.default_profile)
                }
                //작성자 이름 설정
                writerNameTextView.text = name
            }

            //게시물 작성 시간 설정
            writtenTimeTextView.text = writtenTime

            //위치 정보 설정
            locationTextView.text = location

            //본문 내용 설정
            bodyTextView.text = body

            //하트 관련 UI 설정
            setHeartUI()

            //버튼 관련 UI 설정
            setPreNextButtonUI()
        }
    }

    private fun setHeartUI() {
        //로그인한 User 정보 가져오기
        userManager.findUserByID(userID)!!.run {
            //하트 이미지 설정
            heartButton.run {
                if (isInLikedPostIDList(postID)) setImageResource(R.drawable.full_heart_icon)
                else setImageResource(R.drawable.empty_heart_icon)
            }
            //좋아요 개수 설정
            heartNumTextView.text = postManager.findPostByID(postID)!!.heartCount.toString()
        }
    }

    private fun setPreNextButtonUI(){
        //이전 게시물이 없는 경우, 이전 버튼 disable
        val mostRecentPostID = postManager.getMostRecentPostID()
        Log.d(TAG, "set pre button, mostRecentPostID: ${mostRecentPostID?: "null"}")
        preButton.run{
            if((mostRecentPostID == null)||(postID == mostRecentPostID)){
                isEnabled = false
                setImageResource(R.drawable.btn_nextpage_disabled)
            }
            else{
                isEnabled = true
                setImageResource(R.drawable.btn_nextpage)
            }
        }

        //다음 게시물이 없는 경우, 다음 버튼 disable
        val leastRecentPostID = postManager.getLeastRecentPostID(10)
        Log.d(TAG, "set next button, leastRecentPostID: ${leastRecentPostID?: "null"}")
        nextButton.run{
            if((leastRecentPostID == null)||(postID == leastRecentPostID)){
                isEnabled = false
                setImageResource(R.drawable.btn_nextpage_disabled)
            }
            else{
                isEnabled = true
                setImageResource(R.drawable.btn_nextpage)
            }
        }
    }
    
    private fun initHeartButton(){
        heartButton.setOnClickListener {
            //로그인한 User 정보 가져오기
            userManager.findUserByID(userID)!!.run {
                //이미 좋아요한 경우, 좋아요 삭제하기
                if (isInLikedPostIDList(postID)) {
                    deleteLikedPostID(postID)
                    postManager.findPostByID(postID)!!.minusHeartCount()
                }
                //이미 좋아요하지 않은 경우, 좋아요 추가하기
                else {
                    addLikedPostID(postID)
                    postManager.findPostByID(postID)!!.plusHeartCount()
                }
            }
            //하트 관련 UI 다시 설정
            setHeartUI()
        }
    }

    private fun initPreButton(){
        preButton.setOnClickListener {
            if(!it.isEnabled) return@setOnClickListener
            postID++
            Log.d(TAG, "pre button clicked, postID: ${postID}")
            initDetailPage()
        }
    }
    private fun initNextButton(){
        nextButton.setOnClickListener {
            if(!it.isEnabled) return@setOnClickListener
            postID--
            Log.d(TAG, "next button clicked, postID: ${postID}")
            initDetailPage()
        }
    }
}