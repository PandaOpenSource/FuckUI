package com.example.fuckui.permission

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.fuckui.AppSetting
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.core.Observable

class FURxPermission(private val activity: FragmentActivity) {

    private val permission = RxPermissions(activity)

    fun applyDrawOverlaysPermission(): Observable<Boolean> {
        return Observable.create {
            it.onNext(AppSetting.hasDrawOverlaysPermission(activity))
            it.onComplete()
        }.map {
            if (!it) {
                Toast.makeText(activity, "需要打开悬浮窗权限", Toast.LENGTH_LONG).show()
                AppSetting.openDrawOverlaysSetting(activity)
            }
            return@map it
        }
    }

    fun applyPermission(vararg permissions: String): Observable<Boolean> {
        if (isPermissionGrand(*permissions)) {
            return Observable.just(true)
        }
        return permission.request(*permissions).flatMap {
            if (it) {
                return@flatMap Observable.just(true)
            }
            return@flatMap permission.shouldShowRequestPermissionRationale(activity, *permissions).map { shouldShow ->
                if (!shouldShow) {
                    AppSetting.openAppDetailSetting(activity)
                }
                return@map false
            }
        }
    }

    private fun isPermissionGrand(vararg permissions: String): Boolean {
        var result = true
        permissions.forEach { item ->
            if (!permission.isGranted(item)) {
                result = false
            }
        }
        return result
    }

}

