package com.qkx.wisdomwardrobe;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by 高信朋 on 2017/4/1.
 */

public class MyArrayAdapter<T> extends ArrayAdapter<T> {

    public MyArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Object[] objects) {
        super(context, resource, (T[]) objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view.findViewById(android.R.id.text1);

        tv.setTextColor(Color.GRAY);


        return view;
    }
}
