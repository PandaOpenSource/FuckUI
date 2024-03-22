package com.example.fuckui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fuckui.databinding.ActivityMainBinding
import com.example.fuckui.permission.FURxPermission
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btStart.setOnClickListener {
            checkOverlayPermission()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        MenuServices.show(this)
                    }
                }, {})
        }
    }

    private fun checkOverlayPermission(): Observable<Boolean> {
        return FURxPermission(this).applyDrawOverlaysPermission()
    }


}