# permission-helper

使用新API`registerForActivityResult`进行权限请求.
解决 `LifecycleOwners must call register before they are STARTED.` 问题.
- 核心代码
```
fun <I, O> ComponentActivity.registerForResult(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>
): ActivityResultLauncher<I> {
    //调用这个方法不用传生命周期，但需要调用{@link ActivityResultLauncher#unregister()}解除注册
    return activityResultRegistry.register(
        "activity_rq#" + mNextLocalRequestCode.getAndIncrement(), contract, callback
    )
}
```

注意，此方法未关联生命周期，拿到`ActivityResultLauncher`后，需要手动调用`unregister`解除注册.
可以再写一个扩展，在结果回调之后立即解除注册：

```
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
```
- 使用方法
```
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
    
```
