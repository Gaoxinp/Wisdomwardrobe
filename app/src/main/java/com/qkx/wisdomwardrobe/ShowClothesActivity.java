package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.File;
import java.net.URL;

public class ShowClothesActivity extends AppCompatActivity {
    private Context context = ShowClothesActivity.this;
    private String userName;
    private String qrcodeContent;
    private String color;
    private String size;
    private String texture;
    private String season;
    private String style;
    private String location;
    private boolean hasClothes;
    private ImageView picClothes_iv;
    private TextView color_tv;
    private TextView size_tv;
    private TextView texture_tv;
    private TextView season_tv;
    private TextView style_tv;
    private TextView addToChest_show_tv;
    private ImageView QRCode;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {     //服务器传回了衣服照片转换的字符串
                Bitmap bmp = (Bitmap) msg.obj;
                picClothes_iv.setImageBitmap(bmp);
            } else if (msg.what == 0) {        //服务器传回了衣服的参数
                JsonParser ps = new JsonParser();
                String jsonString = (String) msg.obj;
                JsonObject object = ps.parse(jsonString).getAsJsonObject();
                color = object.get("color").getAsString();
                size = object.get("size").getAsString();
                texture = object.get("texture").getAsString();
                season = object.get("season").getAsString();
                style = object.get("style").getAsString();
                hasClothes = object.get("hasClothes").getAsBoolean();
                location = object.get("location").getAsString();
                if (hasClothes == false) {
                    addToChest_show_tv.setVisibility(TextView.VISIBLE);
                    addToChest_show_tv.setEnabled(true);
                }else{
                    addToChest_show_tv.setVisibility(TextView.VISIBLE);
                    addToChest_show_tv.setText("已在衣柜");
                    addToChest_show_tv.setEnabled(false);

                }
                color_tv.setText(color);
                size_tv.setText(size);
                texture_tv.setText(texture);
                season_tv.setText(season);
                style_tv.setText(style);
            } else if (msg.what == 2) {       //服务器传回了 “添加到衣柜” 操作的结果：Succeed  Failure
                String result = (String) msg.obj;
                if (result.equals("Succeed")) {
                    addToChest_show_tv.setText("已在衣柜");
//                    Toast.makeText(context, "又添新衣服啦！好开森\\(^o^)/", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(context).setTitle("又添新衣服啦！好开森\\(^o^)/")
                            .setMessage("您的衣服已经添加进第2号位置").setPositiveButton("OK",null).show();
                    addToChest_show_tv.setEnabled(false);
                } else if (result.equals("Failure")) {
                    Toast.makeText(context, "不知道为什么，这件衣服不想进你的衣柜  ╥﹏╥...", Toast.LENGTH_SHORT).show();

                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_clothes);
        picClothes_iv = (ImageView) findViewById(R.id.picClothes_show_iv);
        color_tv = (TextView) findViewById(R.id.color_tv);
        size_tv = (TextView) findViewById(R.id.size_tv);
        texture_tv = (TextView) findViewById(R.id.texture_tv);
        season_tv = (TextView) findViewById(R.id.season_tv);
        style_tv = (TextView) findViewById(R.id.style_tv);
        addToChest_show_tv = (TextView) findViewById(R.id.addToChest_show_tv);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        qrcodeContent = intent.getStringExtra("ind");
        JsonObject object1 = new JsonObject();

        object1.addProperty("Flag", "getPic");
        object1.addProperty("userName", userName);
        object1.addProperty("ind", qrcodeContent);
        System.out.println(object1.toString());
        new MyGetPicThread(context, object1, handler).start();
        JsonObject object2 = new JsonObject();
        object2.addProperty("Flag", "getArg");
        object2.addProperty("userName", userName);
        object2.addProperty("ind", qrcodeContent);
        System.out.println(object2.toString());
        new MySocketThread(context, object2, handler).start();


        addToChest_show_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObject object = new JsonObject();
                object.addProperty("Flag", "addToChest");
                object.addProperty("userName", userName);
                object.addProperty("ind", qrcodeContent);
                object.addProperty("color", color);
                object.addProperty("size", size);
                object.addProperty("style", style);
                object.addProperty("season", season);
                object.addProperty("texture", texture);
                object.addProperty("location", location);
                System.out.println(object.toString());
                new MySocketThread(context, object, handler).start();
            }
        });
        QRCode = (ImageView) findViewById(R.id.QRCode_title);
        QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanQR();
            }
        });

    }
    /**
     * 扫码操作
     */
    private void ScanQR() {
        Intent intent = new Intent(context, CaptureActivity.class);
        startActivityForResult(intent, 0);


    }
}
