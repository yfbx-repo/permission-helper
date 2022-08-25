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
            requestCamera()
        }
    }


    private fun requestCamera() {
        require(Manifest.permission.CAMERA) {
            onGrant {
                //获得授权
            }

            onDeny {
                //拒绝授权
            }
        }
    }

    /**
     * 统一管理，推荐方式
     */
    private fun takePhoto() = requireCamera {
        //调用相机
    }
}