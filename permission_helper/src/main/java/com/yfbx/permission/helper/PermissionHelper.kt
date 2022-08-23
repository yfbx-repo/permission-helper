package com.yfbx.permission.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Date: 2022-08-18
 * Author: Edward
 * Desc:
 */

/**
 * 请求权限，加前后提示
 */
fun Context.require(
    vararg permissions: String,
    tip: String,
    alert: String,
    callback: () -> Unit
) {
    require(this is ComponentActivity) { "Context must be ComponentActivity" }
    require(*permissions) {
        //请求权限之前,弹窗说明用途
        showTip { showTipDialog(tip) }
        //获得授权
        onGrant { callback.invoke() }
        //权限被拒绝弹窗提示
        onDeny { showAlertDialog(alert) }
    }
}

/**
 * 请求权限，加前后提示
 */
fun Context.require(
    vararg permissions: String,
    build: PermissionBuilder.() -> Unit,
) {
    require(this is ComponentActivity) { "Context must be ComponentActivity" }
    val builder = PermissionBuilder().apply(build)
    lifecycleScope.launch {
        //弹窗说明权限用途，如果未设置，默认返回true(允许不弹窗提示)
        val isOK = builder.showTipCallback?.invoke() ?: true
        //用户同意后，请求权限
        if (isOK) {
            val map = require(*permissions)
            //有一个权限拒绝，则视为授权被拒绝
            val isDeny = map.any { !it.value }
            if (isDeny) {
                //权限被拒绝
                builder.onDenyCallback?.invoke()
            } else {
                //成功获得授权
                builder.onGrantCallback?.invoke()
            }
        }
    }
}

/**
 * 请求权限，返回结果集
 */
suspend fun Context.require(vararg permissions: String): MutableMap<String, Boolean> {
    return suspendCoroutine {
        require(this is ComponentActivity) { "Context must be ComponentActivity" }
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            it.resume(result)
        }.launch(permissions)
    }
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


typealias VoidCallback = () -> Unit

class PermissionBuilder {

    internal var showTipCallback: (suspend () -> Boolean)? = null
    internal var onGrantCallback: VoidCallback? = null
    internal var onDenyCallback: VoidCallback? = null

    /**
     * 请求权限之前，说明权限用途
     */
    fun showTip(showTipCallback: suspend () -> Boolean) {
        this.showTipCallback = showTipCallback
    }

    /**
     * 授权允许
     */
    fun onGrant(onGrantCallback: VoidCallback) {
        this.onGrantCallback = onGrantCallback
    }

    /**
     * 授权拒绝
     */
    fun onDeny(onDenyCallback: VoidCallback) {
        this.onDenyCallback = onDenyCallback
    }

}