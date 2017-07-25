package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by 高信朋 on 2016/12/11.
 */

public class MyGetPicThread extends Thread {
    private Handler handler = null;
    private Context context = null;
    private JsonObject object = null;
    private Socket socket;

    public MyGetPicThread(Context context, JsonObject object, Handler handler) {
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
            int size = in.readInt();
            System.out.println(size);
            if ("getPic".equals(object.get("Flag").getAsString())) {
                byte[] data = new byte[size];
                int len = 0;
                while (len < size) {
                    len += in.read(data, len, size - len);
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            } else if ("getSuggestClothes".equals(object.get("Flag").getAsString())) {
                Message msg = new Message();
                if (size == -1) {
                    msg.what = 0;
                } else {
                    short ind = in.readShort();
                    byte[] data = new byte[size];
                    int len = 0;
                    while (len < size) {
                        len += in.read(data, len, size - len);
                    }
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    msg.what = 1;
                    msg.arg1 = ind;
                    msg.obj = bitmap;
                }
                handler.sendMessage(msg);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        super.run();
    }
}
