package com.holy.demo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.holy.demo.R;
import com.holy.demo.constant.TestItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2017, Smart Haier. All rights reserved.
 * Description:
 * Author: hanhongliang@smart-haier.com (Han Holy)
 * Date: 2017/10/23
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mTestList;
    private TestAdapter mTestAdapter;
    private GridLayoutManager layoutManager;
    private TestItemDecoration mItemDecoration;
    private final int mListNum = 13;
    private int mSpanSize = 1;
    private int mSpanShowSize = 5;
    private int SpanCounts = mSpanSize;
    private int mSpace = 10;
    private List<String> mData;
    private Button testadd, testromove,addrow,deleterow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testadd = (Button) findViewById(R.id.testadd);
        testromove = (Button) findViewById(R.id.testromove);
        addrow = (Button) findViewById(R.id.addrow);
        deleterow = (Button) findViewById(R.id.deleterow);
        initData();
        testadd.setOnClickListener(this);
        testromove.setOnClickListener(this);
        addrow.setOnClickListener(this);
        deleterow.setOnClickListener(this);

        mSpanSize = mSpanShowSize * 2;
        mTestList = (RecyclerView) findViewById(R.id.testlist);
        layoutManager = new GridLayoutManager(TestActivity.this, mSpanSize);
        SpanCounts = mData.size() < mSpanShowSize ? mData.size() : mSpanShowSize;
        layoutManager.setSpanSizeLookup(new TestSpanSizeLookup(SpanCounts));
        mTestList.setLayoutManager(layoutManager);
        mTestList.addItemDecoration(mItemDecoration = new TestItemDecoration(SpanCounts, mSpanShowSize, mSpace));
        mTestList.setAdapter(mTestAdapter = new TestAdapter());
    }

    public List<String> initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < mListNum; i++) {
            mData.add(Integer.toString(i));
        }
        return mData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.testadd:
                if (layoutManager != null) {
                    mData.add(Integer.toString(mData.size()));
                    SpanCounts = mData.size() < mSpanShowSize ? mData.size() : mSpanShowSize;
                    layoutManager.setSpanSizeLookup(new TestSpanSizeLookup(SpanCounts));
                    mTestList.setLayoutManager(layoutManager);
                    mTestList.removeItemDecoration(mItemDecoration);
                    mTestList.addItemDecoration(mItemDecoration = new TestItemDecoration(SpanCounts, mSpanShowSize, mSpace));
                    mTestList.setAdapter(mTestAdapter = new TestAdapter());
                }
                break;
            case R.id.testromove:
                if (layoutManager != null) {
                    mData.remove(mData.size() - 1);
                    SpanCounts = mData.size() < mSpanShowSize ? mData.size() : mSpanShowSize;
                    layoutManager.setSpanSizeLookup(new TestSpanSizeLookup(SpanCounts));
                    mTestList.setLayoutManager(layoutManager);
                    mTestList.removeItemDecoration(mItemDecoration);
                    mTestList.addItemDecoration(mItemDecoration = new TestItemDecoration(SpanCounts, mSpanShowSize, mSpace));
                    mTestList.setAdapter(mTestAdapter = new TestAdapter());
                }
                break;
            case R.id.addrow:
                if (layoutManager != null) {
                    if(mSpanShowSize<10) {
                        mSpanShowSize++;
                        SpanCounts = mData.size() < mSpanShowSize ? mData.size() : mSpanShowSize;
                        layoutManager.setSpanSizeLookup(new TestSpanSizeLookup(SpanCounts));
                        layoutManager.setSpanCount(mSpanSize);
                        mTestList.setLayoutManager(layoutManager);
                        mTestList.removeItemDecoration(mItemDecoration);
                        mTestList.addItemDecoration(mItemDecoration = new TestItemDecoration(SpanCounts, mSpanShowSize, mSpace));
                        mTestList.setAdapter(mTestAdapter = new TestAdapter());
                    }
                }
                break;
            case R.id.deleterow:
                if (layoutManager != null) {
                    if(mSpanShowSize > 1){
                        mSpanShowSize--;
                        SpanCounts = mData.size() < mSpanShowSize ? mData.size() : mSpanShowSize;
                        layoutManager.setSpanSizeLookup(new TestSpanSizeLookup(SpanCounts));
                        layoutManager.setSpanCount(mSpanSize);
                        mTestList.setLayoutManager(layoutManager);
                        mTestList.removeItemDecoration(mItemDecoration);
                        mTestList.addItemDecoration(mItemDecoration = new TestItemDecoration(SpanCounts, mSpanShowSize, mSpace));
                        mTestList.setAdapter(mTestAdapter = new TestAdapter());
                    }

                }
                break;
        }
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {
        TextView v;

        public TestViewHolder(View itemView) {
            super(itemView);
            v = (TextView) itemView.findViewById(R.id.item_test);
        }
    }


    public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TestViewHolder holder = new TestViewHolder(LayoutInflater.from(TestActivity.this).inflate(R.layout.item_test, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.v.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public class TestSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        int spanCount = 0;
        int spanNum = 1;

        public TestSpanSizeLookup(int spanCount) {
            this.spanCount = spanCount;
            spanNum = mSpanSize / mSpanShowSize;
        }

        @Override
        public int getSpanSize(int position) {
            if (spanCount < mSpanShowSize) {
                if(spanCount == 1){
                    return mSpanSize;
                }
                int tmp = (mSpanSize - spanNum * spanCount) / 2;
                if (position == 0 || position == (spanCount - 1)) {
                    return tmp + spanNum;
                }
            }
            return spanNum;
        }
    }


}
