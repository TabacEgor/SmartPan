package com.example.smartpan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import com.example.smartpan.SmartPanControlActivity.Companion.DEVICE_ID
import com.example.smartpan.databinding.ActivityStartBinding
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConnect.setOnClickListener {
            val smartPanControlIntent = Intent(this, SmartPanControlActivity::class.java)
            val bundle = Bundle()
            bundle.putString(DEVICE_ID, etDeviceId.text.toString())
            startActivity(smartPanControlIntent, bundle)
        }
    }
}