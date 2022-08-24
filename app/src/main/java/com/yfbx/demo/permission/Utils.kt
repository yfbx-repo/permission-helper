package com.yfbx.demo.permission

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Date: 2022-08-24
 * Author: Edward
 * Desc:
 */

/**
 * 请求权限之前,弹窗说明用途
 */
fun Context.showTipDialog(tip: String, callback: (Boolean) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage(tip)
        .setNegativeButton("取消") { dialog, _ ->
            callback.invoke(false)
            dialog.dismiss()
        }
        .setPositiveButton("确认") { dialog, _ ->
            callback.invoke(true)
            dialog.dismiss()
        }
        .show()
}

/**
 * 拒绝授权后，告知用户功能不可用，可跳转设置授权
 */
fun Context.showAlertDialog(tip: String) {
    AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage(tip)
        .setNegativeButton("去设置") { dialog, _ ->
            toSettings()
            dialog.dismiss()
        }
        .setPositiveButton("知道了") { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}


/**
 * 跳转系统设置
 */
fun Context.toSettings() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}
