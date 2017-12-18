package com.holy.demo.ui;


import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.holy.demo.R;
import com.holy.demo.constant.Bind;
import com.holy.demo.constant.ViewBinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    @Bind(R.id.seek_screen_bright)
    SeekBar seekScreenBright;
    @Bind(R.id.txt_screen_bright)
    TextView txtScreenBright;
    @Bind(R.id.check_screen_bright)
    CheckBox checkScreenBright;
    @Bind(R.id.sw_1)
    Switch sw1;

    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewBinder.bind(this);
        seekScreenBright.setMax(255);
        seekScreenBright.setOnSeekBarChangeListener(onSeekBakCL);
        checkScreenBright.setOnCheckedChangeListener(onCheckCL);
        if(getSystemBrightness() <0){
            checkScreenBright.setChecked(true);
        }else {
            checkScreenBright.setChecked(false);
            seekScreenBright.setProgress(getSystemBrightness());
        }
    }


    SeekBar.OnSeekBarChangeListener onSeekBakCL = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            txtScreenBright.setText(Integer.toString(progress));
            changeAppBrightness(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    CompoundButton.OnCheckedChangeListener onCheckCL = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                seekScreenBright.setEnabled(false);
                changeAppBrightness(-1);
//                seekScreenBright.setProgress(0);
            }else {
                seekScreenBright.setEnabled(true);
                changeAppBrightness(seekScreenBright.getProgress());
            }
        }
    };

    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    private void changeAppBrightness(int brightness) {
        Window window = this.getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

}
