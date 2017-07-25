package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private Context context = RegisterActivity.this;

    private EditText id_et;
    private EditText password_et;
    private Button regist_bt;

    private String userName = "";
    private String password = "";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ImageView QRCode = (ImageView) findViewById(R.id.QRCode_title);
        QRCode.setVisibility(ImageView.INVISIBLE);
        id_et = (EditText) findViewById(R.id.activity_register_et_id);
        password_et = (EditText) findViewById(R.id.activity_register_et_password);
        regist_bt = (Button) findViewById(R.id.activity_register_bt_ok);

        regist_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(userName, password);
            }
        });
//        由子线程给主线程传递信息时使用，目的是注册成功后finish掉注册界面
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String mag = (String) msg.obj;
                if (mag.equals("SUCCEED")) {
                    finish();
                }
            }
        };
    }
    private void register(String userName, String password) {
        userName = id_et.getText().toString();
        password = password_et.getText().toString();
        System.out.println(userName + "~~~~~~~~~~~~~~~~" + password + "~~~~~~~~~~~~~~~~" );
        if (userName.charAt(0)>='a'&&userName.charAt(0)<='z'){
            JsonObject object = new JsonObject();
            object.addProperty("Flag", "register");
            object.addProperty("id", userName);
            object.addProperty("password", password);
            MySocketThread mySocketThread = new MySocketThread(RegisterActivity.this, object, handler);
            mySocketThread.start();
        }else{
            Toast.makeText(context,"用户名必须以小写字母开头",Toast.LENGTH_SHORT).show();
            id_et.setText("");
        }


    }
}
