package com.android.hikers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged

class LoginActivity : AppCompatActivity() {
    private val etLoginId by lazy { findViewById<EditText>(R.id.et_login_id) }
    private val etLoginPw by lazy { findViewById<EditText>(R.id.et_login_pw) }
    private val btnLogin by lazy { findViewById<Button>(R.id.btn_login) }
    private val btnSignUp by lazy { findViewById<ConstraintLayout>(R.id.bottom_sign_up) }
    private val tvErrorMsg by lazy { findViewById<TextView>(R.id.tv_login_error_msg) }
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initButton()
        initInputFields()
    }

    private fun initButton() {
        initResultLauncher()
        initSignUp()
        initLogin()
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val test = result.data?.getStringExtra(EXTRA_ID) ?: ""

                    etLoginId.setText(test)
                    // 유저정보 처리 코드 입력
                }
//            etLoginId.setText(받아온 데이터로 입력하는 코드)
//            etLoginPw.setText(받아온 데이터로 입력하는 코드)
            }
    }

    private fun initSignUp() {
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun initInputFields() {
        etLoginId.apply {
            doAfterTextChanged {
                checkButtonEnable()
                background = getDrawable(R.drawable.edit_text_background)
            }
        }
        etLoginPw.apply {
            doAfterTextChanged {
                checkButtonEnable()
                background = getDrawable(R.drawable.edit_text_background)
            }
        }
    }

    private fun checkButtonEnable() {
        tvErrorMsg.visibility = View.GONE

        if (etLoginId.text.isNotEmpty() && etLoginPw.text.isNotEmpty()) {
            btnLogin.background = getDrawable(R.drawable.default_button_enable)
        } else {
            btnLogin.background = getDrawable(R.drawable.default_button_disable)
        }
    }

    private fun initLogin() {
        btnLogin.setOnClickListener {
            if (!checkValidity()) return@setOnClickListener

//            val intent = Intent(this, 메인액티비티이름::class.java)
//            startActivity(intent)
        }
    }

    private fun checkValidity(): Boolean {
        if (etLoginId.text.isEmpty()) {
            etLoginId.showError("아이디를 입력해 주세요.")
            return false
        }

        if (etLoginPw.text.isEmpty()) {
            etLoginPw.showError("비밀번호를 입력해 주세요.")
            return false
        }

        // 아이디가 Users에 없을때
        // 비밀번호가 일치하지 않을때

        return true
    }

    private fun EditText.showError(string: String) {
        val animShake = AnimationUtils.loadAnimation(context, R.anim.shake_error)
        this.requestFocus()
        this.background = getDrawable(R.drawable.edit_text_background_error)

        with(tvErrorMsg) {
            visibility = View.VISIBLE
            text = string
            startAnimation(animShake)
        }
    }
}