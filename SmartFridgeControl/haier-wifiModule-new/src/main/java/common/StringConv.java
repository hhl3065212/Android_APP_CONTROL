package common;

import android.util.Log;

/**
 * Created by Kevin on 2015/7/28.
 */
public class StringConv {
    public static boolean equals(String str1, String str2)
    {
        if (str1 != null && str2 != null)
        {
            return str1.equals(str2);
        }
        else if (str1 == null || str2 == null)
        {
            return false;
        }
        return true;
    }

    public static boolean containsIgnoreCase(String dest, String needle)
    {
        if (dest == null || needle == null)
        {
            return false;
        }
        String lowerDest = dest.toLowerCase();
        String lowerNeedle = needle.toLowerCase();
        return lowerDest.contains(lowerNeedle);
    }

    /**
     * 字符串非空判断
     * @param value
     * @return
     */
    public static boolean isEmpty(String value){
        return value == null ? true : (value.trim().equals("") ? true :false);
    }

    public static boolean isEqual(String v1, String v2)
    {
        if (v1 == null)
        {
            return false;
        }
        return v1.equals(v2);
    }

    public static float parseFloat(String fstr) {
        try {
            return Float.parseFloat(fstr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int parseInt(String fstr) {
        try {
            return Integer.parseInt(fstr);
        } catch (NumberFormatException e) {

            Log.i("sdk","intTranslateToString error "+e.getMessage()+"   /n" +"  "+e.toString());

            e.printStackTrace();
            return 0;
        }
    }

    public static int parseInt(String fstr, int radix) {
        try {
            return Integer.parseInt(fstr, radix);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseLong(String fstr) {
        try {
            return Long.parseLong(fstr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long parseLong(String fstr, int radix) {
        try {
            return Long.parseLong(fstr, radix);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
