package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class ChooseCityActivity extends AppCompatActivity {
    private Context context = ChooseCityActivity.this;

    private Spinner provinceSpinner = null;
    private Spinner citySpinner = null;
    private Spinner areaSpinner = null;
    private Spinner straightAreaSpinner = null;
    private Spinner straightCitySpinner = null;

    private LinearLayout normalLinerLayout = null;
    private LinearLayout straightProvinceLinearLayout = null;
    private Button selectWeatherButton = null;

    private String strProvince = "";
    private String strCity = "";
    private String strArea = "";
    String[] array = null;

    private ArrayAdapter<String> adapter;
    private String responseString = "";
    private String weatherS = "";
    private String temp = "";
    private String suggest = "";
    //选择的值
    String value = "";
    private boolean hasWeather = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){         //如果获取天气成功了
                Bundle bundle = (Bundle) msg.obj;
                Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, MainpageActivity.class);
                intent.putExtra("from", 0);
                intent.putExtra("userName", getIntent().getStringExtra("userName"));
                intent.putExtra("city", strCity);
                intent.putExtra("weather", bundle.getString("weather"));
                intent.putExtra("detailP", getIntent().getStringExtra("detailPos"));
                intent.putExtra("suggest", bundle.getString("suggest"));
                intent.putExtra("temp", bundle.getString("temp"));
                //FLAG_ACTIVITY_CLEAR_TASK :如果在调用Context.startActivity时传递这个标记，
                // 将会导致任何用来放置该activity的已经存在的task里面的已经存在的activity先清空，
                // 然后该activity再在该task中启动，也就是说，
                // 这个新启动的activity变为了这个空tas的根activity.所有老的activity都结束掉。
                // 该标志必须和FLAG_ACTIVITY_NEW_TASK一起使用。
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(context, "天气获取出错了", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        provinceSpinner = (Spinner) findViewById(R.id.provinceSpinner);
        citySpinner = (Spinner) findViewById(R.id.citySpinner);
        areaSpinner = (Spinner) findViewById(R.id.areaSpinner);
        straightCitySpinner = (Spinner) findViewById(R.id.straightCitySpinner);
        straightAreaSpinner = (Spinner) findViewById(R.id.straightAreaSpinner);
        normalLinerLayout = (LinearLayout) findViewById(R.id.normalProvinceLinearLayout);
        straightProvinceLinearLayout = (LinearLayout) findViewById(R.id.straightProvinceLinearLayout);
        selectWeatherButton = (Button) findViewById(R.id.selectCarStopButton);
        array = this.getResources().getStringArray(R.array.省份);

        System.out.println(array[0]);
        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        provinceSpinner.setAdapter(adapter);
        straightCitySpinner.setAdapter(adapter);
        array = getResources().getStringArray(R.array.默认市);
        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        citySpinner.setAdapter(adapter);
        citySpinner.setEnabled(false);

        array = getResources().getStringArray(R.array.默认区县);
        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        areaSpinner.setAdapter(adapter);
        straightAreaSpinner.setAdapter(adapter);
        areaSpinner.setEnabled(false);

        //初始化城市，根据所选省/直辖市决定
        //初始化区县，根据所选市/直辖市决定
        initCity();

        selectWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectWeather();

            }
        });


    }

    private void selectWeather() {
        if (normalLinerLayout.getVisibility() == LinearLayout.VISIBLE) {
            strProvince = provinceSpinner.getSelectedItem().toString();
            strCity = citySpinner.getSelectedItem().toString();
            strArea = areaSpinner.getSelectedItem().toString();
        } else if (straightProvinceLinearLayout.getVisibility() == LinearLayout.VISIBLE) {
            strProvince = "";
            strCity = straightCitySpinner.getSelectedItem().toString();
            strArea = straightAreaSpinner.getSelectedItem().toString();
        }
        if (strProvince.equals("（省/直辖市）") || strCity.equals("（市）") || strArea.equals("（区/县）")) {
            Toast.makeText(context, "请选择地址", Toast.LENGTH_SHORT).show();
            return;
        }
        getWeather();


    }

    private void getWeather() {

new GetWeatherThread(strCity,context,handler).start();

    }


    //初始化城市，根据所选省/直辖市决定
    //初始化区县，根据所选市/直辖市决定
    private void initCity() {
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取省份选择的值
                value = (String) provinceSpinner.getSelectedItem();
                if (provinceSpinner.getSelectedItemPosition() != 0) {
                    citySpinner.setEnabled(true);

                    //如果选择的省份是 直辖市 或者 特别行政区 或者 台湾
                    if ("北京".equals(value) || "上海".equals(value) || "天津".equals(value) || "重庆".equals(value) || "香港特别行政区".equals(value) || "澳门特别行政区".equals(value) || "台湾".equals(value)) {
                        normalLinerLayout.setVisibility(LinearLayout.INVISIBLE);
                        straightProvinceLinearLayout.setVisibility(LinearLayout.VISIBLE);
                        straightCitySpinner.setSelection(provinceSpinner.getSelectedItemPosition());
                        straightAreaSpinner.setEnabled(true);

                        switch (provinceSpinner.getSelectedItemPosition()) {
                            case 1:
                                array = getResources().getStringArray(R.array.北京);
                                break;
                            case 2:
                                array = getResources().getStringArray(R.array.天津);
                                break;
                            case 3:
                                array = getResources().getStringArray(R.array.上海);
                                break;
                            case 4:
                                array = getResources().getStringArray(R.array.重庆);
                                break;
                            case 32:
                                array = getResources().getStringArray(R.array.香港特别行政区);
                                break;
                            case 33:
                                array = getResources().getStringArray(R.array.澳门特别行政区);
                                break;
                            case 34:
                                array = getResources().getStringArray(R.array.台湾);
                                break;
                            default:
                        }
                        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        straightAreaSpinner.setAdapter(adapter);

                    }
                    //如果选择的省份是一般省份
                    else {
                        switch (provinceSpinner.getSelectedItemPosition()) {
                            case 5:
                                array = getResources().getStringArray(R.array.安徽);
                                break;
                            case 6:
                                array = getResources().getStringArray(R.array.福建);
                                break;
                            case 7:
                                array = getResources().getStringArray(R.array.甘肃);
                                break;
                            case 8:
                                array = getResources().getStringArray(R.array.广东);
                                break;
                            case 9:
                                array = getResources().getStringArray(R.array.广西壮族自治区);
                                break;
                            case 10:
                                array = getResources().getStringArray(R.array.贵州);
                                break;
                            case 11:
                                array = getResources().getStringArray(R.array.海南);
                                break;
                            case 12:
                                array = getResources().getStringArray(R.array.河北);
                                break;
                            case 13:
                                array = getResources().getStringArray(R.array.黑龙江);
                                break;
                            case 14:
                                array = getResources().getStringArray(R.array.河南);
                                break;
                            case 15:
                                array = getResources().getStringArray(R.array.湖北);
                                break;
                            case 16:
                                array = getResources().getStringArray(R.array.湖南);
                                break;
                            case 17:
                                array = getResources().getStringArray(R.array.江苏);
                                break;
                            case 18:
                                array = getResources().getStringArray(R.array.江西);
                                break;
                            case 19:
                                array = getResources().getStringArray(R.array.吉林);
                                break;
                            case 20:
                                array = getResources().getStringArray(R.array.辽宁);
                                break;
                            case 21:
                                array = getResources().getStringArray(R.array.内蒙古自治区);
                                break;
                            case 22:
                                array = getResources().getStringArray(R.array.宁夏回族自治区);
                                break;
                            case 23:
                                array = getResources().getStringArray(R.array.青海);
                                break;
                            case 24:
                                array = getResources().getStringArray(R.array.陕西);
                                break;
                            case 25:
                                array = getResources().getStringArray(R.array.山东);
                                break;
                            case 26:
                                array = getResources().getStringArray(R.array.山西);
                                break;
                            case 27:
                                array = getResources().getStringArray(R.array.四川);
                                break;
                            case 28:
                                array = getResources().getStringArray(R.array.新疆维吾尔族自治区);
                                break;
                            case 29:
                                array = getResources().getStringArray(R.array.西藏自治区);
                                break;
                            case 30:
                                array = getResources().getStringArray(R.array.云南);
                                break;
                            case 31:
                                array = getResources().getStringArray(R.array.浙江);
                                break;
                            default:
                        }

                        //为 区县 设置适配器
                        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        citySpinner.setAdapter(adapter);


                    }
                } else {
                    array = getResources().getStringArray(R.array.默认市);
                    adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    citySpinner.setAdapter(adapter);
                    citySpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (citySpinner.getSelectedItemPosition() != 0) {
                    areaSpinner.setEnabled(true);
                    switch (provinceSpinner.getSelectedItemPosition()) {
                        case 5:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.合肥);
                                    break;
                            }
                            break;

                        case 6:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.福州);
                                    break;
                            }
                            break;
                        case 7:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.兰州);
                                    break;
                            }
                            break;

                        case 8:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.广州);
                                    break;
                            }
                            break;

                        case 9:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.南宁);
                                    break;
                            }
                            break;

                        case 10:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.贵阳);
                                    break;
                            }
                            break;

                        case 11:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.海口);
                                    break;
                            }
                            break;

                        case 12:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.石家庄);
                                    break;
                                case 2:
                                    array = getResources().getStringArray(R.array.唐山);
                                    break;
                                case 3:
                                    array = getResources().getStringArray(R.array.秦皇岛);
                                    break;
                                case 4:
                                    array = getResources().getStringArray(R.array.邯郸);
                                    break;
                                case 5:
                                    array = getResources().getStringArray(R.array.邢台);
                                    break;
                                case 6:
                                    array = getResources().getStringArray(R.array.保定);
                                    break;
                                case 7:
                                    array = getResources().getStringArray(R.array.张家口);
                                    break;
                                case 8:
                                    array = getResources().getStringArray(R.array.承德);
                                    break;
                                case 9:
                                    array = getResources().getStringArray(R.array.廊坊);
                                    break;
                                case 10:
                                    array = getResources().getStringArray(R.array.衡水);
                                    break;
                                case 11:
                                    array = getResources().getStringArray(R.array.沧州);
                                    break;
                            }
                            break;

                        case 13:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.哈尔滨);
                                    break;
                            }
                            break;

                        case 14:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.郑州);
                                    break;
                            }
                            break;

                        case 15:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.武汉);
                                    break;
                            }
                            break;

                        case 16:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.长沙);
                                    break;
                            }
                            break;

                        case 17:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.南京);
                                    break;
                            }
                            break;

                        case 18:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.南昌);
                                    break;
                            }
                            break;

                        case 19:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.长春);
                                    break;
                            }
                            break;

                        case 20:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.沈阳);
                                    break;
                            }
                            break;

                        case 21:

                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.呼和浩特);
                                    break;
                            }
                            break;

                        case 22:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.银川);
                                    break;
                            }
                            break;

                        case 23:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.西宁);
                                    break;
                            }
                            break;

                        case 24:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.西安);
                                    break;
                            }
                            break;

                        case 25:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.济南);
                                    break;
                            }
                            break;

                        case 26:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.太原);
                                    break;
                            }
                            break;

                        case 27:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.成都);
                                    break;
                            }
                            break;

                        case 28:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.乌鲁木齐);
                                    break;
                            }
                            break;

                        case 29:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.拉萨);
                                    break;
                            }
                            break;

                        case 30:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.昆明);
                                    break;
                            }
                            break;

                        case 31:
                            switch (citySpinner.getSelectedItemPosition()) {
                                case 1:
                                    array = getResources().getStringArray(R.array.杭州);
                                    break;
                            }
                            break;
                    }
                    adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    areaSpinner.setAdapter(adapter);
                } else {
                    array = getResources().getStringArray(R.array.默认区县);
                    adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    areaSpinner.setAdapter(adapter);
                    areaSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
        straightCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //获取省份选择的值
                value = (String) straightCitySpinner.getSelectedItem();
                if (straightCitySpinner.getSelectedItemPosition() != 0) {
                    straightAreaSpinner.setEnabled(true);

                    //选择了 直辖市 或者 特别行政区 或者 台湾
                    if ("北京".equals(value) || "上海".equals(value) || "天津".equals(value) || "重庆".equals(value) || "香港特别行政区".equals(value) || "澳门特别行政区".equals(value) || "台湾".equals(value)) {
                        System.out.println("选择了特殊省份~~~~~~~~~~~~~~");
                        switch (straightCitySpinner.getSelectedItemPosition()) {
                            case 1:
                                array = getResources().getStringArray(R.array.北京);
                                break;
                            case 2:
                                array = getResources().getStringArray(R.array.天津);
                                break;
                            case 3:
                                array = getResources().getStringArray(R.array.上海);
                                break;
                            case 4:
                                array = getResources().getStringArray(R.array.重庆);
                                break;
                            case 32:
                                array = getResources().getStringArray(R.array.香港特别行政区);
                                break;
                            case 33:
                                array = getResources().getStringArray(R.array.澳门特别行政区);
                                break;
                            case 34:
                                array = getResources().getStringArray(R.array.台湾);
                                break;
                            default:
                        }
                        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        straightAreaSpinner.setAdapter(adapter);
                    }
                    //选择了一般的省份
                    else {
                        System.out.println("选择了一般省份~~~~~~~~~~~~~~");
                        normalLinerLayout.setVisibility(LinearLayout.VISIBLE);
                        straightProvinceLinearLayout.setVisibility(LinearLayout.INVISIBLE);
                        provinceSpinner.setSelection(straightCitySpinner.getSelectedItemPosition());
                        citySpinner.setEnabled(true);

                        switch (straightCitySpinner.getSelectedItemPosition()) {
                            case 5:
                                array = getResources().getStringArray(R.array.安徽);
                                break;
                            case 6:
                                array = getResources().getStringArray(R.array.福建);
                                break;
                            case 7:
                                array = getResources().getStringArray(R.array.甘肃);
                                break;
                            case 8:
                                array = getResources().getStringArray(R.array.广东);
                                break;
                            case 9:
                                array = getResources().getStringArray(R.array.广西壮族自治区);
                                break;
                            case 10:
                                array = getResources().getStringArray(R.array.贵州);
                                break;
                            case 11:
                                array = getResources().getStringArray(R.array.海南);
                                break;
                            case 12:
                                array = getResources().getStringArray(R.array.河北);
                                break;
                            case 13:
                                array = getResources().getStringArray(R.array.黑龙江);
                                break;
                            case 14:
                                array = getResources().getStringArray(R.array.河南);
                                break;
                            case 15:
                                array = getResources().getStringArray(R.array.湖北);
                                break;
                            case 16:
                                array = getResources().getStringArray(R.array.湖南);
                                break;
                            case 17:
                                array = getResources().getStringArray(R.array.江苏);
                                break;
                            case 18:
                                array = getResources().getStringArray(R.array.江西);
                                break;
                            case 19:
                                array = getResources().getStringArray(R.array.吉林);
                                break;
                            case 20:
                                array = getResources().getStringArray(R.array.辽宁);
                                break;
                            case 21:
                                array = getResources().getStringArray(R.array.内蒙古自治区);
                                break;
                            case 22:
                                array = getResources().getStringArray(R.array.宁夏回族自治区);
                                break;
                            case 23:
                                array = getResources().getStringArray(R.array.青海);
                                break;
                            case 24:
                                array = getResources().getStringArray(R.array.陕西);
                                break;
                            case 25:
                                array = getResources().getStringArray(R.array.山东);
                                break;
                            case 26:
                                array = getResources().getStringArray(R.array.山西);
                                break;
                            case 27:
                                array = getResources().getStringArray(R.array.四川);
                                break;
                            case 28:
                                array = getResources().getStringArray(R.array.新疆维吾尔族自治区);
                                break;
                            case 29:
                                array = getResources().getStringArray(R.array.西藏自治区);
                                break;
                            case 30:
                                array = getResources().getStringArray(R.array.云南);
                                break;
                            case 31:
                                array = getResources().getStringArray(R.array.浙江);
                                break;
                            default:
                        }

                        //为 区县 设置适配器
                        adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        citySpinner.setAdapter(adapter);


                    }


                }
                //选择了第一个
                else {

                    array = getResources().getStringArray(R.array.默认区县);
                    adapter = new MyArrayAdapter<String>(context, R.layout.spinner_text_item, array);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    straightAreaSpinner.setAdapter(adapter);
                    straightAreaSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


}
