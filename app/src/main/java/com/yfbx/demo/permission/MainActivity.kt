package com.yfbx.demo.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yfbx.permission.helper.require

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener {
            startScan()
        }
    }


    /**
     * 方式一(推荐)
     */
    private fun startScan() = requireScan {
        //调用相机
    }

    /**
     * 方式二(默认样式的弹窗提示)
     */
    private fun takePhoto() {
        require(Manifest.permission.CAMERA, "我们需要使用您的相机，以实现扫码功能", "未获得权限，扫码功能不可用") {
            //调用相机
        }
    }

    /**
     * 方式三(自定义)
     */
    private fun request() {
        require(Manifest.permission.CAMERA) {
            //请求权限之前,弹窗说明用途(可以不调用),需要返回值，返回true才请求权限
            showTip {
                false
            }
            //获得授权
            onGrant {

            }
            //拒绝授权
            onDeny {

            }
        }
    }
}