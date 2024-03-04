package com.yeel.drc.activity.signup

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.yeel.drc.R
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog
import com.yeel.drc.model.SignUpData
import com.yeel.drc.timeout.BaseActivity
import com.yeel.drc.utils.CommonFunctions
import com.yeel.drc.utils.RegisterFunctions

class ChooseLinkOrNewActivity : BaseActivity() {

    var signUpData: SignUpData? = null
    var registerFunctions: RegisterFunctions? = null
    var commonFunctions: CommonFunctions? = null
    private var llCreate: LinearLayout? = null
    private var llLink: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_link_or_new)
        intent.getSerializableExtra("signUpData") as SignUpData?
        setToolBar()
        initViews()
        setItemListeners()
        if (signUpData == null) {
            signUpData = SignUpData()
            signUpData!!.countryCode = registerFunctions?.countryCode
            signUpData!!.mobileNumber = registerFunctions?.mobileNumber
        }
    }

    private fun setItemListeners() {
        llCreate?.setOnClickListener {
            commonFunctions?.page = "create_account"
            registerFunctions?.saveRegisterDetails(signUpData)
            val intent = Intent(applicationContext, CurrencySelectionActivity::class.java)
            intent.putExtra("signUpData", signUpData)
            startActivity(intent)
        }

        llLink?.setOnClickListener {
            commonFunctions?.page = "link_account"
            registerFunctions?.saveRegisterDetails(signUpData)

        }
    }

    private fun initViews() {
        commonFunctions = CommonFunctions(applicationContext)
        llCreate = findViewById(R.id.ll_create)
        llLink = findViewById(R.id.ll_link)

    }

    private fun setToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray)
        val tvTitle = findViewById<TextView>(R.id.tv_title)
        tvTitle.text = ""
    }

    //back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isTaskRoot) {
                    val clearSignUpProgressDialog =
                        ClearSignUpProgressDialog(this@ChooseLinkOrNewActivity, commonFunctions)
                    clearSignUpProgressDialog.show()
                } else {
                    finish()
                }
                return true
            }
        }
        return false
    }
}

