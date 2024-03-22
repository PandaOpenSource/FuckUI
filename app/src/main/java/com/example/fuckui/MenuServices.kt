package com.example.fuckui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.example.fuckui.databinding.LayoutMenuServicesBinding
import com.example.fuckui.permission.FURxPermission
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlin.system.exitProcess

class MenuServices : Service() {

    private var startId: Int = 0

    private lateinit var binding: LayoutMenuServicesBinding

    private lateinit var winManager: WindowManager
    private var x: Int = 0
    private var y: Int = 0

    companion object {
        var mStartActivity: FragmentActivity? = null
        fun show(context: Context) {
            mStartActivity = context as? FragmentActivity
            val intent = Intent(context, MenuServices::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            mStartActivity = null
            val intent = Intent(context, MenuServices::class.java)
            context.stopService(intent)
        }
    }

    private val windowParams: WindowManager.LayoutParams = WindowManager.LayoutParams().also { params ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        params.format = PixelFormat.RGBA_8888
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.END
    }


    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        val res = super.onStartCommand(intent, flags, startId)
        if (startId == 0) {
            initView()
        }
        startId = id
        return res
    }

    private fun initView() {
        winManager = application.getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(applicationContext)
        binding = LayoutMenuServicesBinding.inflate(inflater)
        winManager.addView(binding.root, windowParams)
        setClick()
        setMoveTouch()
        startKunAnimation()
    }

    @SuppressLint("ClickableViewAccessibility", "CheckResult")
    private fun setClick() {
        binding.iconIkun.setOnClickListener {
            if (binding.contentKun.visibility == View.VISIBLE) {
                binding.contentKun.visibility = View.GONE
            } else {
                binding.contentKun.visibility = View.VISIBLE
            }
        }
        binding.startKun.setOnClickListener {
            if (MainKunService.showOrDismiss(it.context)) {
                stopSelf(startId)
            }
            binding.contentKun.visibility = View.GONE
        }
        binding.startPixel.setOnClickListener {
            if (PixelService.showOrDismiss(it.context)) {
                stopSelf(startId)
            }
            binding.contentKun.visibility = View.GONE
        }
        binding.changeKun.setOnLongClickListener { view ->
            checkExternalStoragePermission().observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it) {
                    KunImageSelectActivity.start(view.context)
                }
            }, {})
            binding.contentKun.visibility = View.GONE
            true
        }
        binding.changeKun.setOnClickListener { view ->
            checkExternalStoragePermission().observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it) {
                    KunImageSelectActivity.startSystem(view.context)
                }
            }, {})
            binding.contentKun.visibility = View.GONE
        }

        binding.finish.setOnClickListener {
            binding.contentKun.visibility = View.GONE
            exitProcess(0)
        }
    }

    private fun checkExternalStoragePermission(): Observable<Boolean> {
        val activity = mStartActivity ?: return Observable.just(false)
        // Android 13
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            mutableListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return FURxPermission(activity).applyPermission(*permissions.toTypedArray())
    }


    override fun onDestroy() {
        startId = 0
        winManager.removeView(binding.root)
        super.onDestroy()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMoveTouch() {
        binding.iconIkun.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }

                MotionEvent.ACTION_MOVE -> {
                    val currentX = event.rawX.toInt()
                    val currentY = event.rawY.toInt()
                    val offsetX = currentX - x
                    val offsetY = currentY - y
                    x = currentX
                    y = currentY
                    windowParams.x = windowParams.x - offsetX
                    windowParams.y = windowParams.y + offsetY
                    winManager.updateViewLayout(binding.root, windowParams)
                }
            }
            false
        }
    }

    private fun startKunAnimation() {
        val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.kun_rotate_animation)
        binding.iconIkun.startAnimation(animation)
    }

    override fun onBind(p0: Intent?): IBinder {
        return Binder()
    }

}