package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.google.gson.JsonObject;
import com.qkx.service.LocationService;
import com.xys.libzxing.zxing.activity.CaptureActivity;


public class MainpageActivity extends AppCompatActivity {
    private Context context = MainpageActivity.this;

    //如果有建议穿的当季衣服，那么将用此来保存它的编号
    private int ind ;

    //定位相关
    private LocationService locationService = null;

    private TextView suggest_tv = null;
    private TextView detailPosition = null;
    private TextView city = null;
    private TextView temp_tv = null;
    private TextView tap = null;

    private ImageView weatherIcon;
    private ImageView back;
    private ImageView QRCode;

    private String userName;

    private String cityName;
    private String temp;
    private String weather;
    private String detailPos;
    private String suggest;
    private String weatherInCh;
    private String qrcodeContent;
    private BDLocationListener mListener;
    private Button changeCity;
    private TextView noClothes_tv;
    private ImageView suggestClothes_iv;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {     //该用户还没有当前季节的衣服
                noClothes_tv.setVisibility(TextView.VISIBLE);
                suggestClothes_iv.setVisibility(ImageView.INVISIBLE);
                tap.setVisibility(TextView.INVISIBLE);
            } else if (msg.what == 1) {       //该用户有当前季节的衣服，并将衣服图片传回来了
                noClothes_tv.setVisibility(TextView.INVISIBLE);
                suggestClothes_iv.setVisibility(ImageView.VISIBLE);
                tap.setVisibility(TextView.VISIBLE);
                Bitmap bmp = (Bitmap) msg.obj;
                ind = msg.arg1;
                suggestClothes_iv.setImageBitmap(bmp);
            }else if (msg.what == 2) {       //请求取走衣服，返回的结果
                if ("Take Wrong".equals(msg.obj)){
                    new AlertDialog.Builder(context).setTitle("小柜提示您")
                            .setPositiveButton("OK",null).setMessage("失败了，请重试！").show();
                }else if("Take OK".equals(msg.obj)){
                    JsonObject object = new JsonObject();
                    object.addProperty("Flag", "getSuggestClothes");
                    object.addProperty("userName", userName);
                    new MyGetPicThread(context, object, handler).start();
                    new AlertDialog.Builder(context).setTitle("小柜提示您")
                            .setPositiveButton("OK",null).setMessage("您已取走该衣服").show();
                }


            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        suggest_tv = (TextView) findViewById(R.id.suggest_mainpage_tv);
        suggest_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        city = (TextView) findViewById(R.id.cityName_mainpage_tv);
        detailPosition = (TextView) findViewById(R.id.detPosition_mainpage_tv);
        detailPosition.setMovementMethod(ScrollingMovementMethod.getInstance());
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon_mainpage_iv);
        back = (ImageView) findViewById(R.id.imageIcon_mainpage_iv);
        temp_tv = (TextView) findViewById(R.id.temperature_mainpage_tv);
        QRCode = (ImageView) findViewById(R.id.QRCode_title);
        QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanQR();
            }
        });
        noClothes_tv = (TextView) findViewById(R.id.noClothes_tv);
        suggestClothes_iv = (ImageView) findViewById(R.id.suggestClothes_iv);
        tap = (TextView) findViewById(R.id.tap);
        suggestClothes_iv.setMaxHeight(noClothes_tv.getMaxHeight());
        suggestClothes_iv.setMaxWidth(noClothes_tv.getMaxWidth());
        suggestClothes_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setTitle("小柜提示您")
                        .setPositiveButton("OK",null).setMessage("这件衣服是您的衣柜中编号为"+ind+"的衣服").show();
            }
        });
        suggestClothes_iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                JsonObject object = new JsonObject();
                object.addProperty("Flag", "takeAwayCloth");
                object.addProperty("userName", userName);
                object.addProperty("ind", ind+"");
                new MySocketThread(context, object, handler).start();

                return false;
            }
        });


        changeCity = (Button) findViewById(R.id.changeCity_bt);
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChooseCityActivity.class);
                intent.putExtra("detailPos", detailPos);
                intent.putExtra("userName", userName);
                startActivity(intent);

            }
        });


        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        cityName = intent.getStringExtra("city");
        temp = intent.getStringExtra("temp");
        weather = intent.getStringExtra("weather");
        detailPos = intent.getStringExtra("detailP");
        suggest = intent.getStringExtra("suggest");

        switch (weather) {
            case "晴":
                weatherInCh = weather + ":晴朗的天气更适合户外活动呦！  (∩_∩)";
                break;
            case "多云":
                weatherInCh = weather + ":多云天气，不晒不热，感觉好极了！   (*￣▽￣*)";
                break;
            case "阴":
                weatherInCh = weather + ":阴天了，心情很不爽~~    ︶︿︶";
                break;
            case "小雨":
            case "中雨":
            case "大雨":
            case "小雨-中雨":
            case "中雨-大雨":
            case "阵雨":
            case "雷阵雨":
                weatherInCh = weather + ":其实我挺喜欢下雨的   ^o^";
                break;
            case "雷阵雨伴有冰雹":
                weatherInCh = weather + ":这天气真的不能出门啊！";
                break;


            case "暴雨":
            case "大暴雨":
            case "特大暴雨":
            case "大雨-暴雨":
            case "暴雨-大暴雨":
            case "大暴雨-特大暴雨":
            case "冻雨":
                weatherInCh = weather + ":要注意防灾哦！";
                break;
            case "阵雪":
            case "小雪":
            case "中雪":
            case "大雪":
            case "小雪-中雪":
            case "中雪-大雪":
                weatherInCh = weather + ":好希望雪下的大一点，这样就能出去堆雪人啦！  \\(^o^)/";
                break;
            case "暴雪":
            case "大雪-暴雪":
                weatherInCh = weather + ":雪下太大啦！";
                break;
            case "雨夹雪":
                weatherInCh = weather + ":雨夹雪唉！真的不是一个好天气！";
                break;
            case "浮尘":
                weatherInCh = weather + ":空气略微有浮尘，尽量戴口罩出门哦！";
                break;
            case "扬沙":
                weatherInCh = weather + ":空气不干净，宝宝很难受！";
                break;
            case "强沙尘暴":
                weatherInCh = weather + ":沙尘暴天气，注意防灾哦！";
                break;
            case "霾":
                weatherInCh = weather + ":出门要带口罩哦！";
                break;
            case "雾":
                weatherInCh = weather + ":开车请注意安全，别忘开雾灯哦！";
                break;
            default:
                weatherInCh = "这种天气我没有添加唉~~  T^T";
        }


        weatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, weatherInCh, Toast.LENGTH_SHORT).show();
            }
        });
/**
 * 初始化各个组件
 */
        initContent();


        JsonObject object = new JsonObject();
        object.addProperty("Flag", "getSuggestClothes");
        object.addProperty("userName", userName);
        new MyGetPicThread(context, object, handler).start();


    }

    /**
     * 扫码操作
     */
    private void ScanQR() {
        Intent intent = new Intent(context, CaptureActivity.class);
        startActivityForResult(intent, 0);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            qrcodeContent = data.getStringExtra("result");
            Intent intent = new Intent(context, ShowClothesActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("ind", qrcodeContent);
            startActivity(intent);
        }
    }

    private void initContent() {
        switch (weather) {
            case "晴":
                back.setImageResource(R.drawable.fine);
                weatherIcon.setImageResource(R.drawable.icon_fine);
                break;
            case "多云":
                back.setImageResource(R.drawable.cloudy);
                weatherIcon.setImageResource(R.drawable.icon_cloudy);
                break;
            case "阴":
                back.setImageResource(R.drawable.blank);
                weatherIcon.setImageResource(R.drawable.icon_blank);
                break;
            case "小雨":
            case "中雨":
            case "大雨":
            case "小雨-中雨":
            case "中雨-大雨":
            case "阵雨":
            case "雷阵雨":
            case "暴雨":
            case "大暴雨":
            case "特大暴雨":
            case "大雨-暴雨":
            case "暴雨-大暴雨":
            case "大暴雨-特大暴雨":
            case "冻雨":
            case "雷阵雨伴有冰雹":
                back.setImageResource(R.drawable.rain);
                weatherIcon.setImageResource(R.drawable.icon_rain);
                break;
            case "阵雪":
            case "小雪":
            case "中雪":
            case "大雪":
            case "小雪-中雪":
            case "中雪-大雪":
            case "暴雪":
            case "大雪-暴雪":
                back.setImageResource(R.drawable.snow1);
                weatherIcon.setImageResource(R.drawable.icon_snow);
                break;
            case "雨夹雪":
                back.setImageResource(R.drawable.sleet);
                weatherIcon.setImageResource(R.drawable.icon_sleet);
                break;
            default:
                back.setImageResource(R.drawable.otherweather_back);
                weatherIcon.setVisibility(ImageView.INVISIBLE);
                break;
        }
        city.setText(cityName);
        temp_tv.setText(temp + "℃");
        detailPosition.setText(detailPos);
        suggest_tv.setText(suggest);
    }


}
