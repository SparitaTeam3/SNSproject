package com.android.hikers

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.android.hikers.data.UserManager
import com.android.hikers.extention.leaveEmpty
import com.android.hikers.extention.showErrMsg
import com.android.hikers.messages.ErrorMsg

class UserInfoActivity : AppCompatActivity() {
    private val ivUserInfoProfile by lazy { findViewById<ImageView>(R.id.iv_user_info_profile) }
    private val etUserInfoName by lazy { findViewById<EditText>(R.id.et_user_info_name) }
    private val tvUserInfoNameErrorMsg by lazy { findViewById<TextView>(R.id.tv_user_info_name_error_msg) }
    private val etUserInfoIntroduce by lazy { findViewById<EditText>(R.id.et_user_info_introduce) }
    private val spnUserInfoCharacter1 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_1) }
    private val spnUserInfoCharacter2 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_2) }
    private val spnUserInfoCharacter3 by lazy { findViewById<Spinner>(R.id.spn_user_info_character_3) }
    private val btnUserInfoSummitInfo by lazy { findViewById<Button>(R.id.btn_user_info_summit_info) }
    private val userManager = UserManager.newInstance()
    private lateinit var idValue: String
    private lateinit var pwValue: String
    private lateinit var nameValue: String
    private var profileImageUri: Uri? = null
    private var userNameInput = false
    private var userCharacterValue = MutableList(3) { "" }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        setValue()
        setup()

    }

    private fun setup() {
        initButton()
        initInputFields()
        initSpinner()
    }

    private fun setValue() {
        idValue = intent.getStringExtra(EXTRA_ID) ?: ""
        pwValue = intent.getStringExtra(EXTRA_PW) ?: ""
    }

    private fun initInputFields() {
        setProfileImage()
        inspectName()
    }

    private fun initButton() {
        btnUserInfoSummitInfo.setOnClickListener {
            val animShake = AnimationUtils.loadAnimation(this, R.anim.shake_error)

            if (!userNameInput) {
                tvUserInfoNameErrorMsg.showErrMsg(ErrorMsg.NAME.show(this, 0), etUserInfoName)
                tvUserInfoNameErrorMsg.startAnimation(animShake)
                return@setOnClickListener
            }

            if (userManager.checkUserExist(idValue)) {
                updateUserInfo(idValue)
            } else {
                userManager.addNewUser(idValue, pwValue, nameValue)
                updateUserInfo(idValue)
            }
            intent.putExtra(EXTRA_ID, idValue)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun setProfileImage() {
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    profileImageUri = result.data?.data
                    grantUriPermission(
                        "com.android.hikers", profileImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    ivUserInfoProfile?.run {
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
            leaveEmpty(tvUserInfoNameErrorMsg, ErrorMsg.NAME.show(context, 0))
            doAfterTextChanged {
                nameValue = etUserInfoName.text.toString()

                if (nameValue.isNotEmpty()) {
                    tvUserInfoNameErrorMsg.visibility = View.GONE
                    background = getDrawable(R.drawable.edit_text_background)
                    btnUserInfoSummitInfo.background = getDrawable(R.drawable.default_button_enable)
                    userNameInput = true
                }

                if (nameValue.isEmpty()) {
                    tvUserInfoNameErrorMsg.showErrMsg(ErrorMsg.NAME.show(context, 0), this)
                    btnUserInfoSummitInfo.background =
                        getDrawable(R.drawable.default_button_disable)
                    userNameInput = false
                }
            }
        }
    }

    private fun updateUserInfo(id: String) {
        userManager.changeUserInfo(
            id = id,
            newName = etUserInfoName.text.toString(),
            newIntro = etUserInfoIntroduce.text.toString(),
            newCharacter = userCharacterValue.filterNot { it == "" }.toMutableList(),
            newProfileImage = profileImageUri
        )
    }

    private fun initSpinner() {
        initArrayAdapter(spnUserInfoCharacter1, 0)
        initArrayAdapter(spnUserInfoCharacter2, 1)
        initArrayAdapter(spnUserInfoCharacter3, 2)
    }

    private fun initArrayAdapter(spinner: Spinner, index: Int) {
        val characterDetails = resources.getStringArray(R.array.characters)

        ArrayAdapter.createFromResource(this, R.array.characters, R.layout.spinner_layout_item)
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.spinner_layout_dropdown)
                spinner.adapter = adapter
            }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (position) {
                    0 -> userCharacterValue[index] = ""
                    1 -> userCharacterValue[index] = characterDetails[1]
                    2 -> userCharacterValue[index] = characterDetails[2]
                    3 -> userCharacterValue[index] = characterDetails[3]
                    4 -> userCharacterValue[index] = characterDetails[4]
                    5 -> userCharacterValue[index] = characterDetails[5]
                    6 -> userCharacterValue[index] = characterDetails[6]
                    7 -> userCharacterValue[index] = characterDetails[7]
                    8 -> userCharacterValue[index] = characterDetails[8]
                    9 -> userCharacterValue[index] = characterDetails[9]
                    10 -> userCharacterValue[index] = characterDetails[10]
                    11 -> userCharacterValue[index] = characterDetails[11]
                    12 -> userCharacterValue[index] = characterDetails[12]
                    13 -> userCharacterValue[index] = characterDetails[13]
                    14 -> userCharacterValue[index] = characterDetails[14]
                    15 -> userCharacterValue[index] = characterDetails[15]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}