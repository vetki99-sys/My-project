package com.smartcalculator.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smartcalculator.databinding.ActivityGraphBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * نشاط الرسم البياني
 * يعرض واجهة رسم الدوال الرياضية
 */
@AndroidEntryPoint
class GraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGraphView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupGraphView() {
        // TODO: إعداد عرض الرسم البياني
    }
}
