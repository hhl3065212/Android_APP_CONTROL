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
        if(type.equals(ConstantUtil.BCD251_MODEL)) {
            model = new TwoFiveOneModel(mService);
        } else if (type.equals(ConstantUtil.BCD256_MODEL)) {

        }else if (type.equals(ConstantUtil.BCD325_MODEL) ) {

        } else if (type.equals(ConstantUtil.BCD401_MODEL )) {

        } else if(type.equals(ConstantUtil.BCD630_MODEL )) {

        }else if(type.equals(ConstantUtil.BCD658_MODEL )) {

        }else if(type.equals(ConstantUtil.BCD476_MODEL )) {
            model = new TwoFiveOneModel(mService);// hack to 251
        } else {
            model = new TwoFiveOneModel(mService);// default 251
        }
        return model;
    }

}
