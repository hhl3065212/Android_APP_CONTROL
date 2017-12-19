package com.holy.demo.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holy.demo.R;
import com.holy.demo.constant.Bind;
import com.holy.demo.constant.ViewBinder;

import static android.os.Build.VERSION;

/**
 * A simple {@link Fragment} subclass.
 */
public class VersionFragment extends Fragment {

    @Bind(R.id.txt_system_version)
    TextView txtSystemVersion;
    @Bind(R.id.txt_app_version)
    TextView txtAppVersion;
    @Bind(R.id.txt_mac)
    TextView txtMac;
    public VersionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_version, null);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onStart() {
        super.onStart();
        ViewBinder.bind(this);
        String VersionSystem = String.format("%s", VERSION.RELEASE);
        txtSystemVersion.setText(String.format("系统版本：%s", VersionSystem));
        String VersionApp = "";
        try {
            VersionApp = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txtAppVersion.setText(String.format("应用版本：%s", VersionApp));
        String mac="";
        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            mac = wifiManager.getConnectionInfo().getMacAddress();
        }
        txtMac.setText(String.format("MAC地址：%s", mac));
    }
}
