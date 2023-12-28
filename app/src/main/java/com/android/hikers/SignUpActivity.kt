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
import androidx.core.widget.doAfterTextChanged
import com.android.hikers.data.UserManager

const val EXTRA_TO_USER_INFO_ID = "toUserInfo"
const val EXTRA_TO_LOGIN_ID = "toLoginId"
const val EXTRA_PW = "pw"

class SignUpActivity : AppCompatActivity() {
    private val etSignUpId by lazy { findViewById<EditText>(R.id.et_sign_up_id) }
    private val etSignUpPw by lazy { findViewById<EditText>(R.id.et_sign_up_pw) }
    private val etSignUpCheckPw by lazy { findViewById<EditText>(R.id.et_sign_up_check_pw) }
    private val tvSignUpIdErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_id_error_msg) }
    private val tvSignUpPwErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_pw_error_msg) }
    private val tvSignUpCheckPwErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_check_pw_error_msg) }
    private val btnSignUpNext by lazy { findViewById<Button>(R.id.btn_sign_up_next) }
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var idValue: String
    private lateinit var pwValue: String
    private lateinit var checkPwValue: String
    private var eachElementsValidity = arrayListOf(false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initButton()
        initResultLauncher()
        initInputFields()
    }

    private fun initButton() {
        btnSignUpNext.setOnClickListener {
            if (!checkValidity()) return@setOnClickListener

            val intent = Intent(this, UserInfoActivity::class.java).apply {
                putExtra(EXTRA_TO_USER_INFO_ID, idValue)
                putExtra(EXTRA_PW, pwValue)
            }
            resultLauncher.launch(intent)
        }
    }

    private fun checkValidity(): Boolean {
        val animShake = AnimationUtils.loadAnimation(this, R.anim.shake_error)

        if (etSignUpId.text.isEmpty()) {
            tvSignUpIdErrorMsg.showErrMsg("아이디를 입력해주세요")
            tvSignUpIdErrorMsg.startAnimation(animShake)
            etSignUpId.background = getDrawable(R.drawable.edit_text_background_error)
            return false
        }

        if (!eachElementsValidity[0]) {
            etSignUpId.requestFocus()
            tvSignUpIdErrorMsg.startAnimation(animShake)
            return false
        }

        if (etSignUpPw.text.isEmpty()) {
            tvSignUpPwErrorMsg.showErrMsg("비밀번호를 입력해주세요")
            tvSignUpPwErrorMsg.startAnimation(animShake)
            etSignUpPw.background = getDrawable(R.drawable.edit_text_background_error)
            return false
        }

        if (!eachElementsValidity[1]) {
            etSignUpPw.requestFocus()
            tvSignUpPwErrorMsg.startAnimation(animShake)
            return false
        }

        if (etSignUpCheckPw.text.isEmpty()) {
            tvSignUpCheckPwErrorMsg.showErrMsg("비밀번호를 확인해주세요")
            tvSignUpCheckPwErrorMsg.startAnimation(animShake)
            etSignUpCheckPw.background = getDrawable(R.drawable.edit_text_background_error)
            return false
        }

        if (checkPwValue != pwValue) {
            tvSignUpCheckPwErrorMsg.showErrMsg("비밀번호 정보가 일치하지 않습니다")
            etSignUpCheckPw.background = getDrawable(R.drawable.edit_text_background_error)
        }

        if (!eachElementsValidity[2]) {
            etSignUpCheckPw.requestFocus()
            tvSignUpCheckPwErrorMsg.startAnimation(animShake)
            return false
        }

        if (UserManager.newInstance().checkUserExist(idValue)) {
            tvSignUpIdErrorMsg.showErrMsg("이미 가입된 아이디입니다")
            tvSignUpIdErrorMsg.startAnimation(animShake)
            etSignUpId.background = getDrawable(R.drawable.edit_text_background_error)
            return false
        }
        return true
    }

    private fun initInputFields() {
        inspectId()
        inspectPw()
        inspectCheckPw()
    }

    private fun inspectId() {
        val idPattern = Regex("^[a-zA-Z0-9]+$")

        etSignUpId.apply {
            leaveEmpty(tvSignUpIdErrorMsg, "아이디를 입력해주세요")
            doAfterTextChanged {
                idValue = text.toString()
                eachElementsValidity[0] = true

                if (text.isNotEmpty()) {
                    tvSignUpIdErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (idPattern.containsMatchIn(idValue)) {
                    tvSignUpIdErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (!idPattern.containsMatchIn(idValue)) {
                    tvSignUpIdErrorMsg.showErrMsg("아이디는 영문과 숫자만 사용해주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[0] = false
                }

                if (text.isEmpty()) {
                    tvSignUpIdErrorMsg.showErrMsg("아이디를 입력해주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[0] = false
                }
                changeButtonState()
            }
        }
    }

    private fun inspectPw() {
        val pwPattern = Regex("(?=.*(?!\\d)[!@#\$%^&*()-_=+[ ]{}|;:'\",<.>/?])")

        etSignUpPw.apply {
            leaveEmpty(tvSignUpPwErrorMsg, "비밀번호를 입력해주세요")
            doAfterTextChanged {
                pwValue = text.toString()
                eachElementsValidity[1] = true

                if (text.isNotEmpty()) {
                    tvSignUpPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (pwPattern.containsMatchIn(pwValue)) {
                    tvSignUpPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (pwValue.length >= 8) {
                    tvSignUpPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (pwValue.length < 8) {
                    tvSignUpPwErrorMsg.showErrMsg("비밀번호는 8자리 이상으로 만들어주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[1] = false
                }

                if (!pwPattern.containsMatchIn(pwValue)) {
                    tvSignUpPwErrorMsg.showErrMsg("비밀번호는 반드시 특수문자를 포함해야합니다")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[1] = false
                }



                if (text.isEmpty()) {
                    tvSignUpPwErrorMsg.showErrMsg("비밀번호를 입력해주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[1] = false
                }
                changeButtonState()
            }
        }
    }

    private fun inspectCheckPw() {
        etSignUpCheckPw.apply {
            leaveEmpty(tvSignUpCheckPwErrorMsg, "비밀번호를 확인해주세요")
            doAfterTextChanged {
                checkPwValue = text.toString()
                eachElementsValidity[2] = true

                if (text.isNotEmpty()) {
                    tvSignUpCheckPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (checkPwValue == pwValue) {
                    tvSignUpCheckPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (checkPwValue != pwValue) {
                    tvSignUpCheckPwErrorMsg.showErrMsg("비밀번호 정보가 일치하지 않습니다")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[2] = false
                }

                if (text.isEmpty()) {
                    tvSignUpCheckPwErrorMsg.showErrMsg("비밀번호를 확인해주세요")
                    background = getDrawable(R.drawable.edit_text_background_error)
                    eachElementsValidity[2] = false
                }
                changeButtonState()
            }
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    intent.putExtra(EXTRA_TO_LOGIN_ID, result.data?.getStringExtra(EXTRA_TO_SIGN_UP_ID))
                    setResult(RESULT_OK, intent)
                    finish()
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

    private fun changeButtonState() {
        if (eachElementsValidity.all { it == true }) {
            btnSignUpNext.background = getDrawable(R.drawable.default_button_enable)
        } else {
            btnSignUpNext.background = getDrawable(R.drawable.default_button_disable)
        }
    }
}