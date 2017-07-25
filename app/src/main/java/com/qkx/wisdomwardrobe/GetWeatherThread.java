package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by 高信朋 on 2017/4/20.
 */

public class GetWeatherThread extends Thread {
    private String strCity;
    private String responseString;
    private String temp;
    private String suggest;
    private String weatherS;
    private Context context;
    private boolean hasWeather;
    private Handler handler;

    public GetWeatherThread(String s, Context context, Handler handler) {
        this.strCity = s;
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        // 获取天气预报相关
        String cityname = null;

        URLConnection conn = null;
        try {
            cityname = URLEncoder.encode(strCity + "市", "utf8");
            String url = "http://v.juhe.cn/weather/index?cityname=" + cityname + "&dtype=json&format=2&key=61f1d0b9043486e723840f4f693bdea8";
            System.out.println(url);

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
                    Bundle bundle = new Bundle();
                    bundle.putString("weather", weatherS);
                    bundle.putString("suggest", suggest);
                    bundle.putString("temp", temp);
                    Message message = new Message();
                    message.obj = bundle;
                    message.what = 0;
                    handler.sendMessage(message);

                } else {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
