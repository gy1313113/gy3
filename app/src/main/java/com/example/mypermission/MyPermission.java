package com.example.mypermission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * 使用AndPermission获取权限
 */
public class MyPermission {
    
    @SuppressLint("WrongConstant")
    public void getMyPermission(Context myContext){
        AndPermission.with(myContext)
            .runtime()
            .permission(Permission.Group.STORAGE)// 我这里请求了单个权限组，这里也可以请求权限或多个权限或多个权限组，不过要用逗号隔开。
            // Group.STORAGE代表存储权限的权限组
            // 准备方法，和 okhttp 的拦截器一样，在请求权限之前先运行改方法，已经拥有权限不会触发该方法
            .rationale((context, permissions, executor) -> {
                // 此处可以选择显示提示弹窗
                executor.execute();
            })
            // 用户给权限了
            .onGranted(permissions -> Toast.makeText(myContext,"用户给权限了！",Toast.LENGTH_SHORT).show())
            // 用户拒绝权限，包括不再显示权限弹窗也在此列
            .onDenied(permissions -> {
                // 判断用户是不是不再显示权限弹窗了，若不再显示的话进入权限设置页
                // 如果用户总是拒绝设置权限
                if (AndPermission.hasAlwaysDeniedPermission(myContext, permissions)) {
                    Toast.makeText(myContext,"用户，你tm没这权限啊！",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(myContext,"用户拒绝了！",Toast.LENGTH_SHORT).show();
            })
            .start();
    }
}
