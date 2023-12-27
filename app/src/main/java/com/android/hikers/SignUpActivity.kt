package com.android.hikers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

const val EXTRA_ID = "id"
const val EXTRA_PW = "pw"

class SignUpActivity : AppCompatActivity() {
    private val etSignUpId by lazy { findViewById<EditText>(R.id.et_sign_up_id) }
    private val etSignUpPw by lazy { findViewById<EditText>(R.id.et_sign_up_pw) }
    private val etSignUpCheckPw by lazy { findViewById<EditText>(R.id.et_sign_up_check_pw) }
    private val tvSignUpIdErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_id_error_msg) }
    private val tvSignUpPwErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_pw_error_msg) }
    private val btnSignUpNext by lazy { findViewById<Button>(R.id.btn_sign_up_next) }
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var signUpFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initButton()
        initResultLauncher()
        initInputFields()
    }

    private fun initButton() {
        btnSignUpNext.setOnClickListener {
//            if (!checkValidity()) return@setOnClickListener

            val intent = Intent(this, UserInfoActivity::class.java).apply {
                intent.putExtra(EXTRA_ID, etSignUpId.text.toString())
                intent.putExtra(EXTRA_PW, etSignUpPw.text.toString())
            }
            resultLauncher.launch(intent)
        }
    }

    //    private fun checkValidity(): Boolean {
//
//    }

    private fun initInputFields() {
        inspectId()
        inspectPw()
        inspectCheckPw()
    }

    private fun inspectId() {
        etSignUpId.apply {
            
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    intent.putExtra(EXTRA_ID, etSignUpId.text.toString())
                    signUpFinish = true
                    setResult(RESULT_OK, intent)
                    finish()
                    // 유저정보 처리 코드 입력
                }
//            etLoginId.setText(받아온 데이터로 입력하는 코드)
//            etLoginPw.setText(받아온 데이터로 입력하는 코드)
            }
    }
}