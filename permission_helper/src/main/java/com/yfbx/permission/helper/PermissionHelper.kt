package com.yfbx.permission.helper

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
        launcher?.launch(permissions)
    }
}

fun ComponentActivity.registerForPermissions(callback: PermissionsCallback): ActivityResultLauncher<Array<out String>>? {
    var launcher: ActivityResultLauncher<Array<out String>>? = null
    launcher =
        registerForResult(ActivityResultContracts.RequestMultiplePermissions()) {
            callback.invoke(it)
            launcher?.unregister()
        }
    return launcher
}


fun <I, O> ComponentActivity.registerForResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I>? {
    return findRegistry().register(
        "activity_rq#" + mNextLocalRequestCode.getAndIncrement(), contract, callback
    )
}

fun ComponentActivity.findRegistry(): ActivityResultRegistry {
    val fields = this.javaClass.fields
    fields.forEach {
        it.isAccessible = true
        println("field --> ${it.name}")
    }

    println("fields count = ${fields.size}")

    val field = this.javaClass.getDeclaredField("mActivityResultRegistry")
    field.isAccessible = true
    return field.get(this) as ActivityResultRegistry
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