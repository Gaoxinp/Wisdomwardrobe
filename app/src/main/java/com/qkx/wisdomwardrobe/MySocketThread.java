package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import Decoder.BASE64Decoder;

import static vi.com.gdi.bgl.android.java.EnvDrawText.bmp;

/**
 * Created by 高信朋 on 2016/10/10.
 */
public class MySocketThread extends Thread {
    private JsonObject object = null;
    private Context context = null;
    private Socket socket = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private Handler handler = null;


    public MySocketThread(Context context, JsonObject object, Handler handler) {
        this.context = context;
        this.object = object;
        this.handler = handler;
    }

    @Override
    public void run() {

        try {
            socket = new Socket("172.26.236.1", 9000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(object.toString());
            String mag = in.readUTF();
            System.out.println(mag);
            StringBuffer sb = new StringBuffer();
            JsonParser ps = new JsonParser();
            object = ps.parse(mag).getAsJsonObject();
            if (object.get("Flag").getAsString().equals("register")) {          //注册
//                如果注册成功，由子线程给主线程传递信息，finish掉注册界面
                if (object.get("Message").getAsString().equals("Registered successfully")) {
                    Message message = new Message();
                    message.obj = "SUCCEED";
                    handler.sendMessage(message);
                }
                Looper.prepare();
                Toast.makeText(context, object.get("Message").getAsString(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            if (object.get("Flag").getAsString().equals("login")) {         //登录
                Message message = new Message();
                message.obj = object.get("Message").getAsString();
                handler.sendMessage(message);
            }
            if (object.get("Flag").getAsString().equals("getArg")) {        //获取衣服参数
                Message message = new Message();
                message.what = 0;       // 0 表示服务器传回了衣服的参数
                message.obj = mag;
                handler.sendMessage(message);
            }
            if (object.get("Flag").getAsString().equals("addToChest")) {     //添加到我的衣柜
                Message message = new Message();
                message.what = 2;
                message.obj = object.get("result").getAsString();
                handler.sendMessage(message);
            } if (object.get("Flag").getAsString().equals("takeAwayCloth")) {     //添加到我的衣柜
                Message message = new Message();
                message.what = 2;
                message.obj = object.get("Message").getAsString();
                handler.sendMessage(message);
            }
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
