# permission-helper

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
