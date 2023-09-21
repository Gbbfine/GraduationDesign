package com.example.graduationdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //跳转文字信息收集
        Button textData = (Button)findViewById(R.id.textData);
        Drawable icon_txt=getResources().getDrawable(R.drawable.icon_txt);
        icon_txt.setBounds(0,0,60,60);//必须设置图片的大小否则没有作用
        textData.setCompoundDrawables(icon_txt,null ,null,null);
        textData.setOnClickListener(new View.OnClickListener() //绑定注冊button单击事件
        {
            @Override
            public void onClick(View arg0) {
                // button跳转
                Intent intent = new Intent(MainActivity.this,textData.class);

                startActivity(intent);
            }

        });


        //跳转图像信息收集
        Button imageData = (Button)findViewById(R.id.imageData);
        Drawable icon_pic=getResources().getDrawable(R.drawable.icon_pic);
        icon_pic.setBounds(0,0,60,60);//必须设置图片的大小否则没有作用
        imageData.setCompoundDrawables(icon_pic,null ,null,null);
        imageData.setOnClickListener(new View.OnClickListener() //绑定注冊button单击事件
        {
            @Override
            public void onClick(View arg0) {
                // button跳转
                Intent intent = new Intent(MainActivity.this,imageData.class);

                startActivity(intent);
            }

        });

        //跳转数据可视化
//        Button visualization = (Button)findViewById(R.id.visualization);
//        visualization.setOnClickListener(new View.OnClickListener() //绑定注冊button单击事件
//        {
//            @Override
//            public void onClick(View arg0) {
//                // button跳转
//                Intent intent = new Intent(MainActivity.this,visualization.class);
//
//                startActivity(intent);
//            }
//
//        });
    }
}