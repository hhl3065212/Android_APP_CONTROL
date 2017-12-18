package com.holy.demo.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.holy.demo.R;
import com.holy.demo.constant.Bind;
import com.holy.demo.constant.HorizonVerticalViewPager;
import com.holy.demo.constant.ViewBinder;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "Main2Activity";
    @Bind(R.id.text_cellareate_type)
    TextView CellType;
    @Bind(R.id.btn_version)
    RadioButton btnVersion;
    @Bind(R.id.btn_factory)
    RadioButton btnFactory;
    @Bind(R.id.btn_status)
    RadioButton btnStatus;
    @Bind(R.id.btn_array)
    RadioGroup btn_Array;
    @Bind(R.id.btn_return)
    Button btnReturn;
    @Bind(R.id.viewpager_content)
    HorizonVerticalViewPager vpContent;
    @Bind(R.id.btn_camera)
    RadioButton btnCamera;

    private Fragment currentFragment;
    private Fragment version = new VersionFragment();
    private Fragment factory = new FactoryFragment();
    private Fragment status = new StatusFragment();
    private Fragment camera = new CameraFragment();
    private List<Fragment> mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ViewBinder.bind(this);
        initFlagSize();
        btnVersion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vpContent.setCurrentItem(0);
                }
            }
        });
        btnFactory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vpContent.setCurrentItem(1);
                }
            }
        });
        btnStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vpContent.setCurrentItem(2);
                }
            }
        });
        btnCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vpContent.setCurrentItem(3);
                }
            }
        });
        btnReturn.setOnClickListener(this);
        btnVersion.setChecked(true);

    }

    void initFlagSize() {
        int widthMax = btn_Array.getMeasuredWidth();
        int heightMax = btn_Array.getMeasuredHeight();
        mListView = new ArrayList<>();
        mListView.add(version);
        mListView.add(factory);
        mListView.add(status);
        mListView.add(camera);
        vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mListView.get(position);
            }

            @Override
            public int getCount() {
                return mListView.size();
            }
        });
        vpContent.setCurrentItem(1, false);
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    btnVersion.setChecked(true);
                else if (position == 1)
                    btnFactory.setChecked(true);
                else if (position == 2)
                    btnStatus.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void replaceContent(Fragment fragment) {
        FragmentTransaction FT = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            FT.hide(currentFragment);
        }
        if (!fragment.isAdded()) {
            FT.add(R.id.fragment_content, fragment).commit();
        } else {
            FT.show(fragment).commit();
        }
        currentFragment = fragment;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return:
                finish();
                break;
        }
    }
}
