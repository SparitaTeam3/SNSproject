package com.android.hikers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged

const val EXTRA_TO_SIGN_UP_ID = "toSignUpId"

class UserInfoActivity : AppCompatActivity() {
    private val ivUserInfoProfile by lazy { findViewById<ImageView>(R.id.iv_user_info_profile) }
    private val etUserInfoName by lazy { findViewById<EditText>(R.id.et_user_info_name) }
    private val tvUserInfoNameErrorMsg by lazy { findViewById<TextView>(R.id.tv_user_info_name_error_msg) }
    private val etUserInfoIntroduce by lazy { findViewById<EditText>(R.id.et_user_info_introduce) }
    private val spnUserInfoCharacter1 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_1) }
    private val spnUserInfoCharacter2 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_2) }
    private val spnUserInfoCharacter3 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_3) }
    private val btnUserInfoSummitInfo by lazy { findViewById<Button>(R.id.btn_user_info_summit_info) }
    private lateinit var idValue: String
    private lateinit var pwValue: String
    private lateinit var nameValue: String
    private var profileImageUri: Uri? = Uri.parse("drawable://" + R.drawable.default_profile)
    private var userNameInput = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        getExtra()
        initInputFields()
        initButton()
    }

    private fun getExtra() {
        idValue = intent.getStringExtra(EXTRA_TO_USER_INFO_ID) ?: ""
        pwValue = intent.getStringExtra(EXTRA_PW) ?: ""
    }

    private fun initInputFields() {
        initProfileImageChange()
        inspectName()
    }

    private fun initButton() {
        val userManager = com.android.hikers.data.UserManager.newInstance()

        btnUserInfoSummitInfo.setOnClickListener {
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shake_error)

            if (userNameInput) {
                intent.putExtra(EXTRA_TO_SIGN_UP_ID, idValue)
                userManager.addNewUser(idValue, pwValue, nameValue)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                tvUserInfoNameErrorMsg.showErrMsg("이름을 입력해주세요")
                tvUserInfoNameErrorMsg.startAnimation(animShake)
                etUserInfoName.background = getDrawable(R.drawable.edit_text_background_error)
            }
        }
    }

    private fun initProfileImageChange() {
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    profileImageUri = result.data?.data

                    ivUserInfoProfile?.apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        setImageURI(profileImageUri)
                    }
                }
            }
        ivUserInfoProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)

            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }
    }

    private fun inspectName() {
        etUserInfoName.apply {
            leaveEmpty(tvUserInfoNameErrorMsg, "이름을 입력해주세요")
            doAfterTextChanged {
                nameValue = etUserInfoName.text.toString()

                if (nameValue.isNotEmpty()) {
                    tvUserInfoNameErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                    btnUserInfoSummitInfo.background = getDrawable(R.drawable.default_button_enable)
                    userNameInput = true
                }

                if (nameValue.isEmpty()) {
                    tvUserInfoNameErrorMsg.showErrMsg("이름을 입력해주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    btnUserInfoSummitInfo.background =
                        getDrawable(R.drawable.default_button_disable)
                    userNameInput = false
                }
            }
        }
    }

    fun TextView.showErrMsg(msg: String) {
        setText(msg)
        visibility = View.VISIBLE
    }

    fun EditText.leaveEmpty(errMsg: TextView, string: String) {
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (text.isEmpty()) {
                    errMsg.showErrMsg(string)
                    background = getDrawable(R.drawable.edit_text_background_error)
                }
            }
        }
    }

}