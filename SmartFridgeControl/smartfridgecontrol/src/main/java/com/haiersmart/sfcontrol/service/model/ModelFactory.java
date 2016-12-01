package com.haiersmart.sfcontrol.service.model;

import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.service.ControlMainBoardService;

/**
 * Created by tingting on 2016/11/2.
 */

public class ModelFactory {
    private ControlMainBoardService mService;

    public ModelFactory(ControlMainBoardService service) {
        mService = service;
    }

    public ModelBase createModel(String type) {
        ModelBase model = null;
        if(type.equals(ConstantUtil.BCD251_SN)) {
            model = new TwoFiveOneModel(mService);
        } else if (type.equals(ConstantUtil.BCD256_MODE)) {

        }else if (type.equals(ConstantUtil.BCD325_MODE) ) {

        } else if (type.equals(ConstantUtil.BCD401_MODE )) {

        } else if(type.equals(ConstantUtil.BCD630_MODE )) {

        }else if(type.equals(ConstantUtil.BCD476_MODE )) {

        } else {
            model = new TwoFiveOneModel(mService);// default 251
        }
        return model;
    }

}
