package com.yfbx.permission.helper

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.declaredMembers

/**
 * Date: 2022-08-18
 * Author: Edward
 * Desc:
 */


private val mNextLocalRequestCode = AtomicInteger()
typealias VoidCallback = () -> Unit
typealias PermissionsCallback = (MutableMap<String, Boolean>) -> Unit

/**
 * 请求权限
 */
fun Context.require(
    vararg permissions: String,
    build: PermissionBuilder.() -> Unit,
) {
    val builder = PermissionBuilder().apply(build)
    MainScope().launch {
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

/**
 * 请求权限，返回结果集
 */
suspend fun Context.require(vararg permissions: String): MutableMap<String, Boolean> {
    require(this is ComponentActivity) { "Context must be ComponentActivity" }
    return suspendCoroutine { continuation ->
        val launcher = registerForPermissions { continuation.resume(it) }
        launcher.launch(permissions)
    }
}

/**
 * 注册权限请求，回调之后立即解除注册
 */
fun ComponentActivity.registerForPermissions(callback: PermissionsCallback): ActivityResultLauncher<Array<out String>> {
    var launcher: ActivityResultLauncher<Array<out String>>? = null
    launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            callback.invoke(it)
            //回调之后立即解除注册
            launcher?.unregister()
        }
    return launcher
}

/**
 * LifecycleOwners must call register before they are STARTED.
 *
 * When calling this, you must call {@link ActivityResultLauncher#unregister()} on the
 * returned {@link ActivityResultLauncher} when the launcher is no longer needed to
 * release any values that might be captured in the registered callback.
 *
 */
fun <I, O> ComponentActivity.registerForResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    //调用这个方法不用传生命周期，但需要调用{@link ActivityResultLauncher#unregister()}解除注册
    return activityResultRegistry.register(
        "activity_rq#" + mNextLocalRequestCode.getAndIncrement(), contract, callback
    )
}

class PermissionBuilder {

    internal var onGrantCallback: VoidCallback? = null
    internal var onDenyCallback: VoidCallback? = null


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