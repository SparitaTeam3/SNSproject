package com.android.hikers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.android.hikers.data.UserManager

class LoginActivity : AppCompatActivity() {
    private val etLoginId by lazy { findViewById<EditText>(R.id.et_login_id) }
    private val etLoginPw by lazy { findViewById<EditText>(R.id.et_login_pw) }
    private val btnLogin by lazy { findViewById<Button>(R.id.btn_login) }
    private val btnSignUp by lazy { findViewById<ConstraintLayout>(R.id.bottom_sign_up) }
    private val tvErrorMsg by lazy { findViewById<TextView>(R.id.tv_login_error_msg) }
    private val userManager = UserManager.newInstance()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initButton()
        initInputFields()
    }

    private fun initButton() {
        initSignUp()
        initLogin()
    }

    private fun initInputFields() {
        etLoginId.checkIsNotEmpty()
        etLoginPw.checkIsNotEmpty()
    }

    private fun initSignUp() {
        initResultLauncher()
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun initLogin() {
        btnLogin.setOnClickListener {
            Log.i("user_info_update_test", "${userManager.findUserByID(etLoginId.text.toString())}")
            if (!checkValidity()) {
                return@setOnClickListener
            }
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("userID", etLoginId.text.toString())
            }
            startActivity(intent)
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val signUpUser = userManager.findUserByID(
                        result.data?.getStringExtra(EXTRA_ID) ?: ""
                    )
                    etLoginId.setText(signUpUser?.ID)
                    etLoginPw.setText(signUpUser?.password)
                }
            }
    }

    private fun EditText.checkIsNotEmpty() {
        this.doAfterTextChanged {
            tvErrorMsg.visibility = View.GONE
            background = getDrawable(R.drawable.edit_text_background)

            if (etLoginId.text.isNotEmpty() && etLoginPw.text.isNotEmpty()) {
                btnLogin.background = getDrawable(R.drawable.default_button_enable)
            } else {
                btnLogin.background = getDrawable(R.drawable.default_button_disable)
            }
        }
    }

    private fun checkValidity(): Boolean {
        val id = etLoginId.text.toString()
        val pw = etLoginPw.text.toString()

        if (id.isEmpty()) {
            etLoginId.showError("아이디를 입력해 주세요")
            return false
        }

        if (pw.isEmpty()) {
            etLoginPw.showError("비밀번호를 입력해 주세요")
            return false
        }

        if (!userManager.checkUserExist(id)) {
            etLoginId.showError("가입되지 않은 아이디입니다")
            return false
        }

        if (userManager.findUserByID(id)?.password != pw) {
            etLoginPw.showError("비밀번호가 맞지 않습니다")
            return false
        }
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