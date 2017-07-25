package com.qkx.wisdomwardrobe;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.google.gson.JsonObject;
import com.qkx.service.LocationService;
import com.thinkland.sdk.android.JuheData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Context context = MainActivity.this;
    private Handler handler;

    private Intent intent;

    private EditText activity_login_et_id;
    private EditText activity_login_et_password;
    private Button activity_login_bt_ok;
    private Button changeCity;
    private TextView activity_login_tv_register;

    private String userName;
    private String password;
    private String permissionInfo = "";
    private String cityName = "";
    private String weatherS = "";
    private String detailPosition = "";
    private String suggest = "";
    private final int SDK_PERMISSION_REQUEST = 127;
    private LocationService locationService;
    private String temp;
    //是否已经定位
    private boolean hasWeather = false;

    private BDLocationListener mListener = new BDLocationListener() {
        public void onConnectHotSpotMessage(String s, int i) {
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            System.out.println(location.getLocType()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(10);
                StringBuffer sb1 = new StringBuffer(100);
                cityName = location.getCity();
                sb.append(cityName);

                sb1.append("您当前的具体位置是：\n    ");
                sb1.append(" " + location.getAddrStr() + "\n");// 地址信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb1.append(poi.getName() + ";");
                    }
                }
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb1.append("\ndescribe : ");
                    sb1.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb1.append("\ndescribe : ");
                    sb1.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb1.append("\ndescribe : ");
                    sb1.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                cityName = sb.toString();
                detailPosition = sb1.toString();
                String responseString = "";

                if (!hasWeather) {
                    // 获取天气预报相关
                    String cityname = null;

                    URLConnection conn = null;
                    try {
                        cityname = URLEncoder.encode(cityName, "utf8");
                        String url = "http://v.juhe.cn/weather/index?cityname=" + cityname + "&dtype=json&format=2&key=61f1d0b9043486e723840f4f693bdea8";
                        URL u = new URL(url);
                        conn = u.openConnection();
                        InputStream in = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        StringBuffer sbu = new StringBuffer();
                        String l = null;
                        if ((l = br.readLine()) != null) {
                            sbu.append(l);
                        }
                        responseString = sbu.toString();
                        System.out.println(responseString + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        br.close();
                        in.close();


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            conn = null;
                        }
                        try {
                            JSONObject object = new JSONObject(responseString);
                            if ("200".equals(object.get("resultcode"))) {
                                object = object.getJSONObject("result");
                                JSONObject object_sk = object.getJSONObject("sk");
                                temp = object_sk.getString("temp");
                                System.out.println(temp + "℃~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                JSONObject object_today = object.getJSONObject("today");
                                suggest = "穿衣建议：\n    " + object_today.getString("dressing_advice");
                                object_today = object_today.getJSONObject("weather_id");
                                String weather = object_today.getString("fa");
                                System.out.println(weather + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                                switch (weather) {
                                    case "00":
                                        weatherS = "晴";
                                        break;
                                    case "01":
                                        weatherS = "多云";
                                        break;
                                    case "02":
                                        weatherS = "阴";
                                        break;
                                    case "03":
                                        weatherS = "阵雨";
                                        break;
                                    case "04":
                                        weatherS = "雷阵雨";
                                        break;
                                    case "05":
                                        weatherS = "雷阵雨伴有冰雹";
                                        break;
                                    case "06":
                                        weatherS = "雨夹雪";
                                        break;
                                    case "07":
                                        weatherS = "小雨";
                                        break;
                                    case "08":
                                        weatherS = "中雨";
                                        break;
                                    case "09":
                                        weatherS = "大雨";
                                        break;
                                    case "10":
                                        weatherS = "暴雨";
                                        break;
                                    case "11":
                                        weatherS = "大暴雨";
                                        break;
                                    case "12":
                                        weatherS = "特大暴雨";
                                        break;
                                    case "13":
                                        weatherS = "阵雪";
                                        break;
                                    case "14":
                                        weatherS = "小雪";
                                        break;
                                    case "15":
                                        weatherS = "中雪";
                                        break;
                                    case "16":
                                        weatherS = "大雪";
                                        break;
                                    case "17":
                                        weatherS = "暴雪";
                                        break;
                                    case "18":
                                        weatherS = "雾";
                                        break;
                                    case "19":
                                        weatherS = "冻雨";
                                        break;
                                    case "20":
                                        weatherS = "沙尘暴";
                                        break;
                                    case "21":
                                        weatherS = "小雨-中雨";
                                        break;
                                    case "22":
                                        weatherS = "中雨-大雨";
                                        break;
                                    case "23":
                                        weatherS = "大雨-暴雨";
                                        break;
                                    case "24":
                                        weatherS = "暴雨-大暴雨";
                                        break;
                                    case "25":
                                        weatherS = "大暴雨-特大暴雨";
                                        break;
                                    case "26":
                                        weatherS = "小雪-中雪";
                                        break;
                                    case "27":
                                        weatherS = "中雪-大雪";
                                        break;
                                    case "28":
                                        weatherS = "大雪-暴雪";
                                        break;
                                    case "29":
                                        weatherS = "浮尘";
                                        break;
                                    case "30":
                                        weatherS = "扬沙";
                                        break;
                                    case "31":
                                        weatherS = "强沙尘暴";
                                        break;
                                    case "53":
                                        weatherS = "霾";
                                        break;

                                    default:
                                        weatherS = "other";
                                        break;

                                }
                                hasWeather = true;
                                Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "天气获取出错了", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        }
    };
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView QRCode = (ImageView) findViewById(R.id.QRCode_title);
        QRCode.setVisibility(ImageView.INVISIBLE);
        activity_login_et_id = (EditText) findViewById(R.id.activity_login_et_id);
        activity_login_et_password = (EditText) findViewById(R.id.activity_login_et_password);
        activity_login_bt_ok = (Button) findViewById(R.id.activity_login_bt_ok);
        activity_login_tv_register = (TextView) findViewById(R.id.activity_login_tv_register);


        activity_login_tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, RegisterActivity.class);
                startActivity(intent);
            }
        });
        // after andrioid m,must request Permiision on runtime
        getPersimmions();


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String mag = (String) msg.obj;
                if ("User name does not exist".equals(mag)) {
                    Toast.makeText(context, "用户名不存在", Toast.LENGTH_SHORT).show();
                    activity_login_et_id.setText("");
                    activity_login_et_id.requestFocus();
                } else if ("wrong passwordt".equals(mag)) {
                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
                    activity_login_et_password.setText("");
                    activity_login_et_password.requestFocus();
                } else if ("Login successfully".equals(mag)) {
                    intent = new Intent(context, MainpageActivity.class);
                    intent.putExtra("from", 0);
                    intent.putExtra("userName", userName);
                    intent.putExtra("city", cityName);
                    intent.putExtra("weather", weatherS);
                    intent.putExtra("detailP", detailPosition);
                    intent.putExtra("suggest", suggest);
                    intent.putExtra("temp", temp);
                    startActivity(intent);
                    finish();
                }
            }
        };


    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void Login(View view) {
        userName = activity_login_et_id.getText().toString();
        password = activity_login_et_password.getText().toString();
        if (!"".equals(userName)) {
            if (!"".equals(password)) {
                JsonObject object = new JsonObject();
                object.addProperty("Flag", "login");
                object.addProperty("userName", userName);
                object.addProperty("password", password);
                MySocketThread mySocketThread = new MySocketThread(context, object, handler);
                mySocketThread.start();
            } else {
                Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "请输入账号", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = ((LocationApplication) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        locationService.start();// 定位SDK
        // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        locationService.setLocationOption(locationService.getOption());
//    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        /**
         * 关闭当前页面正在进行中的请求.
         */
        JuheData.cancelRequests(context);
        super.onDestroy();
    }
}
