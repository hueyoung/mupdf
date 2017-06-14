package com.artifex.mupdfdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Author: HueYoung
 * E-mail: yangtaolue@xuechengjf.com
 * Date: 2016/12/19.
 * <p/>
 * Description : pdf文件解析
 * 使用方法：
 * （1）直接打开MainActivity
 * （2）通过Fragment添加
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mupdf);
    }
}
