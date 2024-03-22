package com.example.fuckui.matisse


import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter


class FUMatisseModule {

    private var emitter: ObservableEmitter<List<Uri>>? = null

    private var isSystemChoose = false

    companion object {

        const val REQUEST_CODE_CHOOSE = 100

        private val instance = FUMatisseModule()

        fun getInstance(): FUMatisseModule {
            return instance
        }
    }

    fun choseImageBySystem(activity: FragmentActivity): Observable<List<Uri>> {
        isSystemChoose = true
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        activity.startActivityForResult(intentToPickPic, REQUEST_CODE_CHOOSE)
        return Observable.create {
            emitter = it
        }
    }

    fun chooseImage(activity: FragmentActivity): Observable<List<Uri>> {
        isSystemChoose = false
        Matisse.from(activity)
            .choose(MimeType.ofImage())
            .countable(false)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false)
            .forResult(REQUEST_CODE_CHOOSE)
        return Observable.create {
            emitter = it
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        emitter ?: return
        if (emitter?.isDisposed == true) {
            return
        }
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            if (isSystemChoose) {
                onActivityResultBySystem(data)
            } else {
                emitter?.onNext(Matisse.obtainResult(data))
                emitter?.onComplete()
            }
        }
    }

    private fun onActivityResultBySystem(intent: Intent?) {
        val url = intent?.data?.toString()
        url ?: return
        emitter?.onNext(listOf(Uri.parse(url)))
        emitter?.onComplete()
    }

}