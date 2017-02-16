/**
 * Copyright 2016, Smart Haier. All rights reserved.
 * Description:
 * Author:  Holy.Han
 * Date:  2016/12/13
 * ModifyBy:
 * ModifyDate:
 * ModifyDes :
 */
package com.haiersmart.sfcontrol.utilslib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>function: </p>
 * <p>description:  </p>
 * history:  1. 2016/12/13
 * Author: Holy.Han
 * modification:
 */
public class SystemCmdUtil {
    protected final String TAG = "SystemCmdUtil";
    public static String ERROR = "ERROR";
    private static StringBuilder sb = new StringBuilder("");

    public SystemCmdUtil() {
    }

    public static String getOutput() {
        return sb.toString();
    }

    public static int execCommand(String[] command) throws IOException, OutOfMemoryError {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        sb.delete(0, sb.length());

        try {
            if(proc.waitFor() != 0) {
                Log.i("MTK", "exit value = " + proc.exitValue());
                sb.append(ERROR + proc.exitValue());
                return -1;
            } else {
                String e = bufferedreader.readLine();
                if(e == null) {
                    return 0;
                } else {
                    sb.append(e);

                    while(true) {
                        e = bufferedreader.readLine();
                        if(e == null) {
                            return 0;
                        }

                        sb.append('\n');
                        sb.append(e);
                    }
                }
            }
        } catch (InterruptedException var7) {
            Log.i("MTK", "exe fail " + var7.toString());
            sb.append(ERROR + var7.toString());
            return -1;
        }
    }
    public static String runCMD(String cmd) {
        String result = null;
        try {
            String[] cmdx = {"/system/bin/sh", "-c", cmd}; // file must

            int ret = execCommand(cmdx);

            result = getOutput();

        } catch (IOException e) {

            result = "ERR.JE";
        } catch (OutOfMemoryError e) {
            System.gc();
            System.runFinalization();

            result = "ERR.JE";
        }
        return result;
    }
    public static boolean RootCCTCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("cct");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.d("C300_DBG" , e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
            }
        }
        Log.d("C300_DBG", "CCTCommand ");
        return true;
    }
}
