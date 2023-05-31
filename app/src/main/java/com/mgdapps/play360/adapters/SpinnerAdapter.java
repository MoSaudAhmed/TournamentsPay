package com.mgdapps.play360.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mgdapps.play360.R;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    List<String> itemsList;

    public SpinnerAdapter(List<String> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_row, parent, false);
        TextView tv_spinner_row = convertView.findViewById(R.id.tv_spinner_row);
        tv_spinner_row.setText(itemsList.get(position));
        return convertView;
    }
}