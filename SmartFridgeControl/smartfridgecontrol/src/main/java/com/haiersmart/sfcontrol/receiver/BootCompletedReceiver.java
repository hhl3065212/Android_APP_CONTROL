package com.haiersmart.sfcontrol.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;

/**
 * Created by tingting on 2016/11/28.
 */

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MyLogUtil.i("sfcontrol BootCompletedReceiver", "onReceive");
        Intent btIntent = new Intent(context, ControlMainBoardService.class);
        btIntent.setAction(ConstantUtil.BOOT_COMPLETED);
//        context.startService(new Intent(context,ControlMainBoardService.class));
        context.startService(btIntent);

    }
}
