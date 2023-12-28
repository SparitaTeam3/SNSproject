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
import com.android.hikers.extention.leaveEmpty
import com.android.hikers.extention.showErrMsg
import com.android.hikers.messages.ErrorMsg

const val EXTRA_ID = "id"
const val EXTRA_PW = "pw"

class SignUpActivity : AppCompatActivity() {
    private val etSignUpId by lazy { findViewById<EditText>(R.id.et_sign_up_id) }
    private val etSignUpPw by lazy { findViewById<EditText>(R.id.et_sign_up_pw) }
    private val etSignUpCheckPw by lazy { findViewById<EditText>(R.id.et_sign_up_check_pw) }
    private val tvSignUpIdErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_id_error_msg) }
    private val tvSignUpPwErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_pw_error_msg) }
    private val tvSignUpCheckPwErrorMsg by lazy { findViewById<TextView>(R.id.tv_sign_up_check_pw_error_msg) }
    private val btnSignUpNext by lazy { findViewById<Button>(R.id.btn_sign_up_next) }
    private val userManager = UserManager.newInstance()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var idValue: String
    private lateinit var pwValue: String
    private lateinit var checkPwValue: String
    private var passInspects = arrayListOf(false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initButton()
        initInputFields()
    }

    private fun initButton() {
        initResultLauncher()
        btnSignUpNext.setOnClickListener {
            if (!checkValidity()) {
                return@setOnClickListener
            }
            val intent = Intent(this, UserInfoActivity::class.java).apply {
                putExtra(EXTRA_ID, idValue)
                putExtra(EXTRA_PW, pwValue)
            }
            resultLauncher.launch(intent)
        }
    }

    private fun initInputFields() {
        inspectId()
        inspectPw()
        inspectCheckPw()
    }

    private fun checkValidity(): Boolean {
        val id = etSignUpId.text.toString()
        val pw = etSignUpPw.text.toString()
        val checkPw = etSignUpCheckPw.text.toString()

        if (id.isEmpty()) {
            failValidity(etSignUpId, tvSignUpIdErrorMsg, ErrorMsg.ID.msg[0])
            return false
        }

        if (!passInspects[0]) {
            failValidity(etSignUpId, tvSignUpIdErrorMsg)
            return false
        }

        if (pw.isEmpty()) {
            failValidity(etSignUpPw, tvSignUpPwErrorMsg, ErrorMsg.PW.msg[0])
            return false
        }

        if (!passInspects[1]) {
            failValidity(etSignUpPw, tvSignUpPwErrorMsg)
            return false
        }

        if (checkPw.isEmpty()) {
            failValidity(etSignUpCheckPw, tvSignUpCheckPwErrorMsg, ErrorMsg.PW.msg[4])
            return false
        }

        if (checkPwValue != pwValue) {
            failValidity(etSignUpCheckPw, tvSignUpCheckPwErrorMsg, ErrorMsg.PW.msg[5])
            return false
        }

        if (!passInspects[2]) {
            failValidity(etSignUpCheckPw, tvSignUpCheckPwErrorMsg)
            return false
        }

        if (userManager.checkUserExist(idValue)) {
            failValidity(etSignUpId, tvSignUpIdErrorMsg, ErrorMsg.ID.msg[2])
            return false
        }
        return true
    }

    private fun inspectId() {
        val idPattern = Regex("^[a-zA-Z0-9]+$")

        etSignUpId.apply {
            leaveEmpty(tvSignUpIdErrorMsg, ErrorMsg.ID.msg[0])
            doAfterTextChanged {
                idValue = text.toString()
                passInspects[0] = true

                if (text.isNotEmpty()) {
                    tvSignUpIdErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (idPattern.containsMatchIn(idValue)) {
                    tvSignUpIdErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (!idPattern.containsMatchIn(idValue)) {
                    tvSignUpIdErrorMsg.showErrMsg(ErrorMsg.ID.msg[4], this)
                    passInspects[0] = false
                }

                if (text.isEmpty()) {
                    tvSignUpIdErrorMsg.showErrMsg(ErrorMsg.ID.msg[0], this)
                    passInspects[0] = false
                }
                changeButtonState()
            }
        }
    }

    private fun inspectPw() {
        val pwPattern = Regex("(?=.*(?!\\d)[!@#\$%^&*()-_=+][{}|;:'\",<.>/?])")

        etSignUpPw.apply {
            leaveEmpty(tvSignUpPwErrorMsg, ErrorMsg.PW.msg[0])
            doAfterTextChanged {
                pwValue = text.toString()
                passInspects[1] = true

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
                    tvSignUpPwErrorMsg.showErrMsg(ErrorMsg.PW.msg[2], this)
                    passInspects[1] = false
                }

                if (!pwPattern.containsMatchIn(pwValue)) {
                    tvSignUpPwErrorMsg.showErrMsg(ErrorMsg.PW.msg[3], this)
                    passInspects[1] = false
                }

                if (text.isEmpty()) {
                    tvSignUpPwErrorMsg.showErrMsg(ErrorMsg.PW.msg[0], this)
                    passInspects[1] = false
                }
                changeButtonState()
            }
        }
    }

    private fun inspectCheckPw() {
        etSignUpCheckPw.apply {
            leaveEmpty(tvSignUpCheckPwErrorMsg, ErrorMsg.PW.msg[4])
            doAfterTextChanged {
                checkPwValue = text.toString()
                passInspects[2] = true

                if (text.isNotEmpty()) {
                    tvSignUpCheckPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (checkPwValue == pwValue) {
                    tvSignUpCheckPwErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                }

                if (checkPwValue != pwValue) {
                    tvSignUpCheckPwErrorMsg.showErrMsg(ErrorMsg.PW.msg[5], this)
                    passInspects[2] = false
                }

                if (text.isEmpty()) {
                    tvSignUpCheckPwErrorMsg.showErrMsg(ErrorMsg.PW.msg[4], this)
                    passInspects[2] = false
                }
                changeButtonState()
            }
        }
    }

    private fun initResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    intent.putExtra(EXTRA_ID, result.data?.getStringExtra(EXTRA_ID))
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
    }

    private fun changeButtonState() {
        if (passInspects.all { it }) {
            btnSignUpNext.background = getDrawable(R.drawable.default_button_enable)
        } else {
            btnSignUpNext.background = getDrawable(R.drawable.default_button_disable)
        }
    }

    private fun failValidity(editText: EditText, errTextView: TextView) {
        val animShake = AnimationUtils.loadAnimation(this, R.anim.shake_error)

        editText.requestFocus()
        editText.background = getDrawable(R.drawable.edit_text_background_error)
        errTextView.startAnimation(animShake)
    }

    private fun failValidity(editText: EditText, errTextView: TextView, msg: String) {
        val animShake = AnimationUtils.loadAnimation(this, R.anim.shake_error)

        editText.requestFocus()
        errTextView.showErrMsg(msg, editText)
        errTextView.startAnimation(animShake)
    }
}