package com.example.fuckui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.FragmentActivity


object AppSetting {

    fun hasDrawOverlaysPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun openDrawOverlaysSetting(activity: FragmentActivity) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + activity.packageName)
        activity.startActivity(intent)
    }

    fun openAppDetailSetting(context: Context) {
        val packageName = context.packageName
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}