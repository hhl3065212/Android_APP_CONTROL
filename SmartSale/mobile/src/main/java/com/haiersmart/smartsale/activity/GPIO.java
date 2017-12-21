package com.haiersmart.smartsale.activity;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by tingting on 2017/12/19.
 */


public class GPIO {
    /**

     * @param args

     */

    private static int num, gpio_number;

    private static String gpio_num = null;

    private static String exportPath;

    private static String directionPath;

    private static String valuePath;

    static Process process = null;

    static DataOutputStream dos = null;

    public static int gpio_crtl(int gpio_bank, int direction, int level){

        gpio_number = gpio_bank * 32 + direction;

        Log.i("gpio_crtl", "gpio_number = " + gpio_number);
        exportPath = "echo " + gpio_number + " > /sys/class/gpio/export";

        directionPath = "echo out > " + " /sys/class/gpio/gpio" + gpio_number + "/direction";

        valuePath ="echo " + level + " > /sys/class/gpio/gpio" + gpio_number + "/value";

        System.out.printf(exportPath + "\n" + directionPath + "\n" + valuePath + "\n");

        try {
            process = Runtime.getRuntime().exec("sh");
//            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(exportPath+"\n");
            dos.flush();
            dos.writeBytes(directionPath+"\n");
            dos.flush();
            dos.writeBytes(valuePath +"\n");
//            dos.writeBytes("echo 1 > /sys/class/gpio/gpio263/value"+ "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return 0;
    }


}
