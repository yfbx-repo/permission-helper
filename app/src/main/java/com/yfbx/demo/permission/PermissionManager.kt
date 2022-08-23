package com.yfbx.demo.permission

import android.Manifest
import android.content.Context
import com.yfbx.permission.helper.require

/**
 * Date: 2022-08-02
 * Author: Edward
 * Desc:  统一管理全局权限请求
 */


/**
 * 请求扫描权限
 */
fun Context.requireScan(callback: () -> Unit) = require(
    Manifest.permission.CAMERA,
    tip = "我们需要使用您的相机，以实现扫码功能",
    alert = "未获得授权，扫一扫功能不可用",
    callback = callback
)

/**
 * 请求相机权限
 */
fun Context.requireCamera(callback: () -> Unit) = require(
    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
    tip = "我们需要使用您的相机，以实现拍照上传功能",
    alert = "未获得授权，相机不可用",
    callback = callback
)
