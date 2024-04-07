package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityButtonBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ButtonActivity : AppCompatActivity() {
    private lateinit var binding:ActivityButtonBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityButtonBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        binding.loadingButton.onClick {
//            binding.loadingButton.startLoading()
//            Handler(Looper.getMainLooper()).postDelayed({
//                binding.loadingButton.stopLoading()
//            },3000)
//        }

        binding.loadingButton.setOnClickListener {
            lifecycleScope.launch {
                binding.loadingButton.startLoading()
                delay(3000)
                binding.loadingButton.stopLoading()
            }
        }
    }
}