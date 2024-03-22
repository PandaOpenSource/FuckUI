package com.example.fuckui

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Size
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.example.fuckui.databinding.LayoutUiCheckBinding


/**
 * @功能:应用外打开Service 有局限性 特殊界面无法显示
 * @User Lmy
 * @Creat 4/15/21 5:28 PM
 * @Compony 永远相信美好的事情即将发生
 */
class MainKunService : Service() {

    private lateinit var binding: LayoutUiCheckBinding
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
        const val KEY_IMAGE_PATH = "image_path"
        const val KEY_IMAGE_ASPECT_RATIO = "aspect_ratio"

        private var isShowing = false

        fun showOrDismiss(context: Context): Boolean {
            if (isShowing) {
                stop(context)
            } else {
                show(context)
            }
            return isShowing
        }

        fun show(context: Context, url: String? = null, size: Size? = null) {
            isShowing = true
            val sp = IkunSharedPreferences.newInstance(context)
            val data = if (url?.isNotEmpty() == true) {
                url
            } else {
                sp.getString(IkunKey.IKUN_IMAGE)
            }
            if (data.isEmpty()) {
                return
            }
            val aspectRatio = if (size == null) {
                sp.getInt(IkunKey.IKUN_IMAGE_WIDTH).toFloat() / sp.getInt(IkunKey.IKUN_IMAGE_HEIGHT).toFloat()
            } else {
                size.width.toFloat() / size.height.toFloat()
            }
            val intent = Intent(context, MainKunService::class.java)
            intent.putExtra(KEY_IMAGE_PATH, data)
            intent.putExtra(KEY_IMAGE_ASPECT_RATIO, aspectRatio)
            context.startService(intent)
        }

        fun stop(context: Context) {
            isShowing = false
            val intent = Intent(context, MainKunService::class.java)
            context.stopService(intent)
        }
    }

    private lateinit var imageUri: String
    private var aspectRatio: Float = 0f

    private fun delayStartMenu() {
        binding.root.postDelayed({ MenuServices.show(binding.root.context) }, 500)
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        val res = super.onStartCommand(intent, flags, id)
        if (startId == 0) {
            imageUri = intent?.getStringExtra(KEY_IMAGE_PATH) ?: ""
            aspectRatio = intent?.getFloatExtra(KEY_IMAGE_ASPECT_RATIO, 0f) ?: 0f
            if (imageUri.isNotEmpty()) {
                initView()
            }
        }
        delayStartMenu()
        startId = id
        return res
    }

    private fun initView() {
        winManager = application.getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(applicationContext)
        binding = LayoutUiCheckBinding.inflate(inflater)
        try {
            binding.ivImage.setImageURI(Uri.parse(imageUri))
        } catch (_: Exception) {
            val context = binding.root.context
            Toast.makeText(context, "设置图片错误:$imageUri", Toast.LENGTH_LONG).show()
            stop(context)
        }
        if (aspectRatio > 0f) {
            binding.ivImage.aspectRatio = aspectRatio
        }
        binding.addAlpha.setOnClickListener {
            val alpha = binding.ivImage.alpha
            if (alpha < 1) {
                binding.ivImage.alpha = (alpha + 0.1f)
            }
        }
        binding.subAlpha.setOnClickListener {
            val alpha = binding.ivImage.alpha
            if (alpha > 0) {
                binding.ivImage.alpha = (alpha - 0.1f)
            }
        }
        winManager.addView(binding.root, floatWindowParams)
    }

    override fun onDestroy() {
        startId = 0
        winManager.removeView(binding.root)
        super.onDestroy()
    }
}