package com.example.fuckui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import com.example.fuckui.matisse.FUMatisseModule
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers


class KunImageSelectActivity : AppCompatActivity() {

    companion object {
        private const val IS_SYSTEM = "is_system"
        fun start(context: Context?) {
            context ?: return
            val intent = resolveIntent(context)
            intent.putExtra(IS_SYSTEM, false)
            context.startActivity(intent)
        }

        private fun resolveIntent(context: Context): Intent {
            val intent = Intent(context, KunImageSelectActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return intent
        }

        fun startSystem(context: Context?) {
            context ?: return
            val intent = resolveIntent(context)
            intent.putExtra(IS_SYSTEM, true)
            context.startActivity(intent)
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainKunService.stop(this)
        val isSystem = intent.getBooleanExtra(IS_SYSTEM, false)
        if (isSystem) {
            FUMatisseModule.getInstance().choseImageBySystem(this).observeOn(AndroidSchedulers.mainThread()).subscribe({
                onChooseImage(it.firstOrNull())
            }, {})
        } else {
            FUMatisseModule.getInstance().chooseImage(this).observeOn(AndroidSchedulers.mainThread()).subscribe({
                onChooseImage(it.firstOrNull())
            }, {})
        }
    }

    private fun onChooseImage(uri: Uri?) {
        val url = uri?.toString()
        url ?: return
        try {
            val cr = this.contentResolver
            val bitmap: Bitmap = BitmapFactory.decodeStream(cr.openInputStream(Uri.parse(url)))
            val sp = IkunSharedPreferences.newInstance(this)
            sp.putString(IkunKey.IKUN_IMAGE, url)
            sp.putInt(IkunKey.IKUN_IMAGE_WIDTH, bitmap.width)
            sp.putInt(IkunKey.IKUN_IMAGE_HEIGHT, bitmap.height)
            MenuServices.stop(this)
            MainKunService.show(this, url, Size(bitmap.width, bitmap.height))
            finish()
        } catch (_: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FUMatisseModule.getInstance().onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            finish()
        }
    }

}