package com.holy.demo.constant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.demo.R;

import java.util.List;
import java.util.Map;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/10/18
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class WifiViewAdapter extends RecyclerView.Adapter<WifiViewAdapter.WifiViewHolder> {

    private List<Map<String, Object>> mData;
    private Context mContext;

    public WifiViewAdapter(Context context, List<Map<String, Object>> mData) {
        this.mContext = context;
        this.mData = mData;
    }

    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WifiViewHolder holder;
        holder = new WifiViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_wifi_scan, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(WifiViewHolder holder, final int position) {
        holder.wifi_ssid.setText(mData.get(position).get("ssid").toString());
        //        holder.wifi_bssid.setText(mData.get(position).get("bssid").toString());
        holder.wifi_rssi.setText(mData.get(position).get("rssi").toString());
        if (!(mData.get(position).get("state").toString().equals(""))) {
            holder.wifi_state.setVisibility(View.VISIBLE);
            holder.wifi_state.setText(mData.get(position).get("state").toString());
        } else {
            holder.wifi_state.setVisibility(View.GONE);
        }
        holder.wifi_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mData.get(position).toString();
                Toast.makeText(mContext, string,Toast.LENGTH_SHORT).show();
            }
        });
        holder.ll_wifi_ssid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = mData.get(position).get("ssid").toString();
                Toast.makeText(mContext, "my name is "+string,Toast.LENGTH_SHORT).show();
            }
        });
        //        holder.wifi_key.setText(mData.get(position).get("key").toString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class WifiViewHolder extends RecyclerView.ViewHolder {
        TextView wifi_ssid, wifi_bssid, wifi_rssi, wifi_state, wifi_key, wifi_more;
        LinearLayout ll_wifi_ssid;

        public WifiViewHolder(View view) {
            super(view);
            wifi_ssid = (TextView) view.findViewById(R.id.wifi_ssid);
            wifi_bssid = (TextView) view.findViewById(R.id.wifi_bssid);
            wifi_rssi = (TextView) view.findViewById(R.id.wifi_rssi);
            wifi_state = (TextView) view.findViewById(R.id.wifi_state);
            wifi_key = (TextView) view.findViewById(R.id.wifi_key);
            wifi_more = (TextView) view.findViewById(R.id.wifi_more);
            ll_wifi_ssid = (LinearLayout) view.findViewById(R.id.ll_wifi_ssid);
        }
    }


}
