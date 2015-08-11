package com.iwaykids.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iwaykids.R;
import com.iwaykids.models.Devices;

import java.util.ArrayList;

/**
 * Created by SCORP on 07/07/15.
 */
public class DeviceNamesAdapter extends BaseAdapter {

    private ArrayList<Devices> mItems = new ArrayList<>();
    private Context context;

    public DeviceNamesAdapter(Context context, ArrayList<Devices> mItems){

        this.context = context;
        this.mItems = mItems;
    }

    public void addItem(Context context, Devices yourObject) {

        mItems.add(yourObject);
    }

    public void addItems(ArrayList<Devices> list) {
        mItems.addAll(list);
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view =  LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(mItems.get(position).getDeviceName());

        return view;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_actionbar, null, false);

        TextView textView = (TextView) view.findViewById(R.id.tv_device_name_item);
        textView.setText(mItems.get(i).getDeviceName());

        return view;
    }
}
