package com.yfbx.permission.helper

import android.app.AlertDialog
import android.content.Context
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Date: 2022-08-23
 * Author: Edward
 * Desc:
 */
/**
 * 请求权限之前,弹窗说明用途
 */
suspend fun Context.showTipDialog(tip: String) = suspendCoroutine<Boolean> {
    AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage(tip)
        .setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
            it.resume(false)
        }
        .setPositiveButton("确认") { dialog, _ ->
            it.resume(true)
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
