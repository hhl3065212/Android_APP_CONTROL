package com.haiersmart.smartsale.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haiersmart.smartsale.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "TestActivity";
    private TextView mTvPir, mTVDoor;
    private Button mBtnPir, mBtnDoor, mBtnBack;
    private JniPir mPirHandler;
    private int mDoorValue = 0;

    static final int GPIO_A0 = 0;
    static final int GPIO_A1 = 1;
    static final int GPIO_A2 = 2;
    static final int GPIO_A3 = 3;
    static final int GPIO_A4 = 4;
    static final int GPIO_A5 = 5;
    static final int GPIO_A6 = 6;
    static final int GPIO_A7 = 7;
    static final int GPIO_B0 = 8;
    static final int GPIO_B1 = 9;
    static final int GPIO_B2 = 10;
    static final int GPIO_B3 = 11;
    static final int GPIO_B4 = 12;
    static final int GPIO_B5 = 13;
    static final int GPIO_B6 = 14;
    static final int GPIO_B7 = 15;
    static final int GPIO_C0 = 16;
    static final int GPIO_C1 = 17;
    static final int GPIO_C2 = 18;
    static final int GPIO_C3 = 19;
    static final int GPIO_C4 = 20;
    static final int GPIO_C5 = 21;
    static final int GPIO_C6 = 22;
    static final int GPIO_C7 = 23;
    static final int GPIO_D0 = 24;
    static final int GPIO_D1 = 25;
    static final int GPIO_D2 = 26;
    static final int GPIO_D3 = 27;
    static final int GPIO_D4 = 28;
    static final int GPIO_D5 = 29;
    static final int GPIO_D6 = 30;
    static final int GPIO_D7 = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTvPir = (TextView) findViewById(R.id.tx_test_pir);
        mTVDoor = (TextView) findViewById(R.id.tx_test_door);

        mBtnPir = (Button) findViewById(R.id.btn_test_pir);
        mBtnPir.setOnClickListener(this);

        mBtnDoor = (Button) findViewById(R.id.btn_test_door);
        mBtnDoor.setOnClickListener(this);

        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);

        mPirHandler = new JniPir();
//        String w = "none";
//        try {
//            mPirHandler.mFileOutputStream.write(w.getBytes());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mPirHandler.closeGpioDev();
//        initPirGPIO();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_pir:
                updatePirValue();
                break;
            case R.id.btn_test_door:
                setDoorValue();
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPirHandler.closeGpioDev();
        super.onDestroy();
    }

    public void updatePirValue() {
//        int value = mPirHandler.add(227, 0);
//        Log.i(TAG, "probe gpio value = "+value);
//        value = mPirHandler.getGpio(227);
        byte[] b= new byte[64];
        int value = 0;
//        value = mPirHandler.getData(b);
        value = mPirHandler.getGpio(0);
        Log.i(TAG, "get gpio value = " + value);
        if(value > 0) {
            Log.i(TAG, "get gpio b=" + new String(b, 0, value));
            String pirText = "PIR GPIO Value: " + new String(b, 0, value);
            mTvPir.setText(pirText);
        }

//        GetPirGPIOValue();
    }

    public void initPirGPIO() {
        DataOutputStream dos = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            //Process process = runtime.exec("su");
            Process process = runtime.exec("sh");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes("echo 227 > /sys/class/gpio/export" + "\n");
            dos.flush();

            //设置引脚功能为输出
            dos.writeBytes("echo in > /sys/class/gpio/gpio227/direction" + "\n");
            dos.flush();
            dos.close();
            System.out.println("echo 227 > /sys/class/gpio/export " + " echo in > /sys/class/gpio/gpio227/direction");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }

    public int GetPirGPIOValue() {
        String res = read("sys/class/gpio/gpio227/value");
        int gpioValue = -1;
        if (!res.equals(null)) {
            gpioValue = Integer.parseInt(res);
            Log.i(TAG, "gpioValue = " + gpioValue);
        }
        return gpioValue;
    }

    public void setDoorValue() {
        if (mDoorValue == 0) {
            Log.i(TAG, "setDoorValue 1" );
            GPIO.gpio_crtl(8, GPIO_A7, 1);
            mDoorValue = 1;
        } else {
            Log.i(TAG, "setDoorValue 0" );
            GPIO.gpio_crtl(8, GPIO_A7, 0);
            mDoorValue = 0;
        }
        String res = read("sys/class/gpio/gpio263/value");
        int gpioValue = -1;
        if (res != null) {
            gpioValue = Integer.parseInt(res);
        }
        Log.i(TAG, "gpioValue = " + gpioValue);
    }


    public static String read(String sys_path) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cat " + sys_path); // 此处进行读操作
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while (null != (line = br.readLine())) {
                Log.w(TAG, "read data ---> " + line);
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "*** ERROR *** Here is what I know: " + e.getMessage());
        }
        return null;
    }

//    public static void writeSysFile(String sys_path){
//
//        Process p = null;
//        DataOutputStream os = null;
//        try {
//            p = Runtime.getRuntime().exec("sh");
//            os = new DataOutputStream(p.getOutputStream());
//            os.writeBytes("echo 1 > "+sys_path + "\n");
//            os.writeBytes("exit\n");
//            os.flush();
//            Log.e(TAG, " write " + sys_path + " success" );
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, " can't write " + sys_path+e.getMessage());
//        } finally {
//            if(p != null){
//                p.destroy();
//            }
//            if(os != null){
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}
