package com.android.hikers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.android.hikers.data.UserManager
import com.android.hikers.extention.showError
import com.android.hikers.messages.ErrorMsg

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
        etLoginId.inputIsFull()
        etLoginPw.inputIsFull()
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
            if (!checkValidity()) {
                return@setOnClickListener
            }
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_ID, etLoginId.text.toString())
            }
            startActivity(intent)
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val id = result.data?.getStringExtra(EXTRA_ID) ?: ""
                    val signUpUser = userManager.findUserByID(id)

                    etLoginId.setText(signUpUser?.ID)
                    etLoginPw.setText(signUpUser?.password)
                }
            }
    }

    private fun EditText.inputIsFull() {
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
            etLoginId.showError(ErrorMsg.ID.msg[0], tvErrorMsg)
            return false
        }

        if (pw.isEmpty()) {
            etLoginPw.showError(ErrorMsg.PW.msg[0], tvErrorMsg)
            return false
        }

        if (!userManager.checkUserExist(id)) {
            etLoginId.showError(ErrorMsg.ID.msg[1], tvErrorMsg)
            return false
        }

        if (userManager.findUserByID(id)?.password != pw) {
            etLoginPw.showError(ErrorMsg.PW.msg[1], tvErrorMsg)
            return false
        }
        return true
    }
}