package com.haiersmart.sfcontrol;

import com.haiersmart.sfcontrol.utilslib.MyByte;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void testPrint()throws Exception{
        byte[] b = new byte[]{1,12,3,4,5,6,7,8,9};
        MyByte a = new MyByte(0x10);
        a.setBit0();
        String string = PrintUtil.ByteToString(a.getValue(),PrintUtil.HEX);
        MyLogUtil.i("a = "+ string);
    }
}