package com.example.openlink

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors
import kotlin.system.exitProcess

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val executor = Executors.newSingleThreadExecutor()
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val btnEnter: Button = findViewById(R.id.btn_main_enter)
        val btnExit: Button = findViewById(R.id.btn_main_exit)
        val labelUrl: TextView = findViewById(R.id.txt_main_label_url)
        val txtUrl: TextView = findViewById(R.id.txt_main_txt_url)
        val startAuto: Switch = findViewById(R.id.swt_setting_auto)
        val editEnabled: Switch = findViewById(R.id.swt_setting_edit)
        val setLabelUrl: EditText = findViewById(R.id.etx_setting_label_url)
        val setTxtUrl: EditText = findViewById(R.id.etx_setting_txt_url)

        setLabelUrl.setText(sharedPreferences.getString("label_url", ""))
        setTxtUrl.setText(sharedPreferences.getString("txt_url", ""))
        startAuto.isChecked = sharedPreferences.getBoolean("start_auto", false)
        labelUrl.text = setLabelUrl.text.toString()
        txtUrl.text = setTxtUrl.text.toString()

        btnEnter.setOnClickListener {
            if (setLabelUrl.toString().isNotEmpty() && setTxtUrl.toString().isNotEmpty()) {
                openBrowser(setTxtUrl.text.toString())
                finish()
            }
        }

        startAuto.setOnCheckedChangeListener {_, isChecked ->
            saveChecked("start_auto", isChecked)
        }

        editEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                setLabelUrl.isEnabled = true
                setTxtUrl.isEnabled = true
                startAuto.isEnabled = true
            } else {
                setLabelUrl.isEnabled = false
                setTxtUrl.isEnabled = false
                startAuto.isEnabled = false
            }
        }

        setLabelUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val inputValue = setLabelUrl.text.toString().trim()
                saveInputValue("label_url", inputValue)
                labelUrl.text = inputValue
            }
        }

        setTxtUrl.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                val inputValue = setTxtUrl.text.toString().trim()
                saveInputValue("txt_url", inputValue)
                txtUrl.text = inputValue
            }
        }

        executor.execute {
            Thread.sleep(5000) // Delay of 5 seconds
            runOnUiThread {
                if (startAuto.isChecked) {
                    openBrowser(setTxtUrl.text.toString())
                }
            }
        }

        btnExit.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }

    private fun saveInputValue(key: String, value: String) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun saveChecked(key: String, value: Boolean) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    private fun openBrowser(url: String) {
        val url = url
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
        finish()
    }
}