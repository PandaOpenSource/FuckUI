package com.example.fuckui

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.fuckui.databinding.LayoutPixelBinding

/**
 * @功能:应用外打开Service 有局限性 特殊界面无法显示
 * @User Lmy
 * @Creat 4/15/21 5:28 PM
 * @Compony 永远相信美好的事情即将发生
 */

class PixelService : Service() {

    private lateinit var binding: LayoutPixelBinding
    private lateinit var winManager: WindowManager

    private var startId: Int = 0

    //设置悬浮窗口长宽数据
    private val floatWindowParams: WindowManager.LayoutParams
        get() {
            val params = WindowManager.LayoutParams()
            //设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_PHONE
            }
            //设置可以显示在状态栏上
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            params.format = PixelFormat.RGBA_8888
            //设置悬浮窗口长宽数据
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            return params
        }

    companion object {

        private var isShowing = false

        fun showOrDismiss(context: Context): Boolean {
            if (isShowing) {
                stop(context)
            } else {
                show(context)
            }
            return isShowing
        }

        private fun show(context: Context) {
            isShowing = true
            val intent = Intent(context, PixelService::class.java)
            context.startService(intent)
        }

        private fun stop(context: Context) {
            isShowing = false
            val intent = Intent(context, PixelService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        val res = super.onStartCommand(intent, flags, id)
        if (startId == 0) {
            initView()
        }
        startId = id
        delayStartMenu()
        return res
    }

    private fun initView() {
        winManager = application.getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(applicationContext)
        binding = LayoutPixelBinding.inflate(inflater)
        binding.addAlpha.setOnClickListener {
            binding.pixelView.setPixel(binding.pixelView.getPixel() + 1)
            setCurrentPixel(binding.pixelView.getPixel())
        }
        binding.subAlpha.setOnClickListener {
            binding.pixelView.setPixel(binding.pixelView.getPixel() - 1)
            setCurrentPixel(binding.pixelView.getPixel())
        }
        setCurrentPixel(binding.pixelView.getPixel())
        winManager.addView(binding.root, floatWindowParams)
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentPixel(pixel: Int) {
        binding.tvCurrentPixel.text = "像素:${pixel}/网格"
    }

    private fun delayStartMenu() {
        binding.root.postDelayed({ MenuServices.show(binding.root.context) }, 500)
    }


    override fun onDestroy() {
        startId = 0
        winManager.removeView(binding.root)
        super.onDestroy()
    }
}