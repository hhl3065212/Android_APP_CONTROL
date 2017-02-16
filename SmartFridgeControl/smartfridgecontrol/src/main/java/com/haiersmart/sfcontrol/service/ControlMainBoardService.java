package com.haiersmart.sfcontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
import com.haiersmart.sfcontrol.constant.ConstantWifiUtil;
import com.haiersmart.sfcontrol.constant.EnumBaseName;
import com.haiersmart.sfcontrol.database.DBOperation;
import com.haiersmart.sfcontrol.database.FridgeControlDbMgr;
import com.haiersmart.sfcontrol.database.FridgeControlEntry;
import com.haiersmart.sfcontrol.database.FridgeInfoEntry;
import com.haiersmart.sfcontrol.database.FridgeStatusEntry;
import com.haiersmart.sfcontrol.service.model.ModelBase;
import com.haiersmart.sfcontrol.service.model.ModelFactory;
import com.haiersmart.sfcontrol.service.powerctl.PowerProcessData;
import com.haiersmart.sfcontrol.utilslib.MyLogUtil;
import com.haiersmart.sfcontrol.utilslib.PrintUtil;
import com.haiersmart.sfcontrol.utilslib.SpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;

public class ControlMainBoardService extends Service {
    static final String TAG = "ControlMainBoardService";
    private DBOperation mDBHandle;
    private MainBoardParameters mMBParams;
    private PowerProcessData mProcessData;
    private ControlMainBoardInfo mBoardInfo;
    private ModelFactory mModelFactory;
    private ModelBase mModel;
    private boolean mIsModelReady = false;
    private boolean mIsServiceRestart = true;
    private static int readyCounts = 0;


    public ControlMainBoardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLogUtil.i(TAG, "Bind Service");
        return new CmbBinder();
    }

    public class CmbBinder extends Binder {
        public ControlMainBoardService getService() {
            return ControlMainBoardService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            //while system auto restart this service, this case should be run
            MyLogUtil.i(TAG, "onStartCommand intent=NULL to handle quick cold or freeze timer event!");
            mIsServiceRestart = true;
        } else {
            String action = intent.getAction();
            //            MyLogUtil.i(TAG, "onStartCommand action=" + action);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ConstantUtil.QUERY_CONTROL_READY://查询service是否准备好
                        MyLogUtil.i(TAG, readyCounts + " sendControlCmdResponse before main board is " + mIsModelReady);
                        readyCounts++;
                        sendControlReadyInfo();
                        break;
                    case ConstantUtil.BROADCAST_ACTION_QUERY_BACK:
                        handleQueryData();
                        if (!mIsModelReady) {
                            mIsModelReady = true;
                            //                            sendQuery();
                        }
                        break;
                    case ConstantUtil.BROADCAST_ACTION_STATUS_BACK:
                        //                MyLogUtil.d(TAG, "onStartCommand status back");
                        if (mIsModelReady) {
                            mModel.handleStatusDataResponse();
                        }
                        //                        MyLogUtil.i(TAG,"handleServiceRestartEvent mIsServiceRestart is "+mIsServiceRestart);
                        if (mIsServiceRestart) {
                            handleServiceRestartEvent();
                            mIsServiceRestart = false;
                        }
                        break;
                    case ConstantUtil.TEMPER_SETCOLD://冷藏区温控
                    {
                        if (mModel != null) {
                            int temperCold = intent.getIntExtra(ConstantUtil.KEY_SET_FRIDGE_LEVEL, 0);
                            MyLogUtil.i(TAG, "onStartCommand TEMPER_SETCOLD temperCold=" + temperCold);
                            if(temperCold < mMBParams.getTargetTempRange().getFridgeMinValue()){
                                temperCold = mMBParams.getTargetTempRange().getFridgeMinValue();
                            }else if(temperCold > mMBParams.getTargetTempRange().getFridgeMaxValue()){
                                temperCold = mMBParams.getTargetTempRange().getFridgeMaxValue();
                            }
                            mModel.setCold(temperCold);
                            MyLogUtil.d("printSerialString", "fridge target");
                            sendQuery();
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }

                    }
                    break;
                    case ConstantUtil.TEMPER_SETFREEZE://冷冻区温控
                    {
                        if (mModel != null) {
                            int temperCold = intent.getIntExtra(ConstantUtil.KEY_SET_FREEZE_LEVEL, 0);
                            if(temperCold < mMBParams.getTargetTempRange().getFreezeMinValue()){
                                temperCold = mMBParams.getTargetTempRange().getFreezeMinValue();
                            }else if(temperCold > mMBParams.getTargetTempRange().getFreezeMaxValue()){
                                temperCold = mMBParams.getTargetTempRange().getFreezeMaxValue();
                            }
                            mModel.setFreeze(temperCold);
                            MyLogUtil.d("printSerialString", "freeze target");
                            sendQuery();
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }

                    }
                    break;
                    case ConstantUtil.TEMPER_SETCUSTOMAREA://变温区温控
                    {
                        if (mIsModelReady) {
                            int temperCold = intent.getIntExtra(ConstantUtil.KEY_SET_COLD_LEVEL, 0);
                            if(temperCold < mMBParams.getTargetTempRange().getChangeMinValue()){
                                temperCold = mMBParams.getTargetTempRange().getChangeMinValue();
                            }else if(temperCold > mMBParams.getTargetTempRange().getChangeMaxValue()){
                                temperCold = mMBParams.getTargetTempRange().getChangeMaxValue();
                            }
                            mModel.setCustomArea(temperCold);
                            MyLogUtil.d("printSerialString", "change target");
                            sendQuery();
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }

                    }
                    break;
                    case ConstantUtil.MODE_STERILIZE_ON://杀菌模式控制
                    {
                        if (mIsModelReady) {
                            int sterilizeStep = intent.getIntExtra(ConstantUtil.MODE_UV, 0);
                            MyLogUtil.d("str::sterilizeStep = "+sterilizeStep);
                            mModel.setSterilizeMode(sterilizeStep);
                            sendQuery();
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }
                    }
                    break;
                    case ConstantUtil.QUERY_STERILIZE_STATUS://杀菌剩余时间
                        if (mIsModelReady) {
                            sendSterilizeStatus();
                        }
                        break;
                    case ConstantUtil.BOOT_COMPLETED: {
                        MyLogUtil.i(TAG, "boot completed received!");
                        handleBootEvent();
                    }
                    break;
                    default:
                        if (mIsModelReady) {
                            handleActions(action);
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }
                        break;
                }
            }

        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
        //  return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProcessData.PowerProcDataStop();

        stopColdOnTime();
        stopFreezeOnTime();
        stopSterilize();
    }

    public void initService() {
        MyLogUtil.i(TAG, "kill initService");
        //Create Database
        mDBHandle = DBOperation.getInstance();

        //Create serial port data which manage main board and android board logic class
        try {
            mProcessData = new PowerProcessData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create main board Parameters
        mMBParams = MainBoardParameters.getInstance();

        mBoardInfo = new ControlMainBoardInfo(mMBParams);

    }

    public FridgeControlDbMgr getControlDbMgr() {
        return mDBHandle.getControlDbMgr();
    }


    public ControlMainBoardInfo getMainBoardInfo() {
        return mBoardInfo;
    }

    private void initModel(String modeName) {
        MyLogUtil.i(TAG, "kill initModel");
        mModelFactory = new ModelFactory(this);
        mModel = mModelFactory.createModel(modeName);
        mModel.init();
    }


    private void handleActions(String action, int... value) {

        switch (action) {
            case ConstantUtil.MODE_SMART_ON://智能开
                MyLogUtil.i(TAG, "handleActions smartOn");
                mModel.smartOn();
                MyLogUtil.d("printSerialString", "smartOn");
                sendQuery();
                break;
            case ConstantUtil.MODE_SMART_OFF://智能关
                MyLogUtil.i(TAG, "handleActions smartOff");
                mModel.smartOff();
                MyLogUtil.d("printSerialString", "smartOff");
                sendQuery();
                break;
            case ConstantUtil.MODE_FREEZE_ON://速冻开
                mModel.freezeOn();
                startFreezeOnTime(true);
                MyLogUtil.d("printSerialString", "quick freeze on");
                sendQuery();
                break;
            case ConstantUtil.MODE_FREEZE_OFF://速冻关
                mModel.freezeOff();
                stopFreezeOnTime();
                MyLogUtil.d("printSerialString", "quick freeze off");
                sendQuery();
                break;
            case ConstantUtil.MODE_HOLIDAY_ON://假日开
                mModel.holidayOn();
                MyLogUtil.d("printSerialString", "holiday on");
                sendQuery();
                break;
            case ConstantUtil.MODE_HOLIDAY_OFF://假日关
                mModel.holidayOff();
                MyLogUtil.d("printSerialString", "holiday off");
                sendQuery();
                break;
            case ConstantUtil.MODE_COLD_ON://变温开
                mModel.coldOn();
                startColdOnTime(true);
                MyLogUtil.d("printSerialString", "quick cold on");
                sendQuery();
                break;
            case ConstantUtil.MODE_COLD_OFF://变温关
                mModel.coldOff();
                stopColdOnTime();
                MyLogUtil.d("printSerialString", "quick cold off");
                sendQuery();
                break;
            case ConstantUtil.REFRIGERATOR_OPEN://冷藏开
                mModel.refrigeratorOpen();
                MyLogUtil.d("printSerialString", "fridge on");
                sendQuery();
                break;
            case ConstantUtil.REFRIGERATOR_CLOSE://冷藏关
                mModel.refrigeratorClose();
                MyLogUtil.d("printSerialString", "fridge off");
                sendQuery();
                break;
            case ConstantUtil.QUERY_FRIDGE_INFO:
                sendFridgeInfoResponse();
                break;
            case ConstantUtil.QUERY_CONTROL_INFO://查询档位和控制信息
                sendControlCmdResponse();
                break;
            case ConstantUtil.QUERY_TEMPER_INFO://查询温度值信息
                notifyTemperChanged(mModel.getTempEntries());
                break;
            case ConstantUtil.QUERY_ERROR_INFO://查询故障信息
                notifyErrorOccurred(mModel.getErrorEntries());
                break;
            case ConstantUtil.QUERY_TEMP_RANGE:
                provideTempRange();
                break;
            case ConstantUtil.KEY_QUERY:
                MyLogUtil.d("printSerialString", "query");
                sendQuery();
                break;
            case ConstantUtil.MODE_TIDBIT_ON:
                mModel.tidbitOn();
                sendQuery();
                break;
            case ConstantUtil.MODE_TIDBIT_OFF:
                mModel.tidbitOff();
                sendQuery();
                break;
            case ConstantUtil.MODE_PURIFY_ON:
                mModel.purifyOn();
                sendQuery();
                break;
            case ConstantUtil.MODE_PURIFY_OFF:
                mModel.purifyOff();
                sendQuery();
                break;
            default:
                break;
        }
    }

    private void handleQueryData() {
        MyLogUtil.i(TAG, "handleQueryData");
        String fridgeId = mBoardInfo.getFridgeId();
        String fridgeType = mBoardInfo.getFridgeType();
        MyLogUtil.i(TAG, "handleQueryData fridgeId=" + fridgeId);
        MyLogUtil.i(TAG, "handleQueryData fridgeType=" + fridgeType);

        if (mModel == null) {
            initModel(fridgeType);
            //broadcast fridgeId to app
            sendFridgeInfoResponse();
        }

        //update database if value changed
        List<FridgeInfoEntry> infoEntryList = mDBHandle.getInfoDbMgr().query();
        if (infoEntryList.size() > 0) {
            String fridgeVersion = mBoardInfo.getFridgeVersion();
            String fridgeFactory = mBoardInfo.getFridgeFactory();
            String fridgeSn = mBoardInfo.getFridgeSn();

            if (infoEntryList.get(0).value != fridgeId ||
                    infoEntryList.get(1).value != fridgeVersion ||
                    infoEntryList.get(2).value != fridgeFactory ||
                    infoEntryList.get(3).value != fridgeSn) {
                //update to db
                ArrayList<FridgeInfoEntry> tempList = new ArrayList<FridgeInfoEntry>();
                tempList.add(new FridgeInfoEntry("fridgeId", fridgeId));
                tempList.add(new FridgeInfoEntry("fridgeVersion", fridgeVersion));
                tempList.add(new FridgeInfoEntry("fridgeFactory", fridgeFactory));
                tempList.add(new FridgeInfoEntry("fridgeSn", fridgeSn));
                mDBHandle.getInfoDbMgr().updateAll(tempList);
            }
        }
    }

    public void sendFridgeInfoResponse() {
        //broadcast fridgeId to app
        Intent intent = new Intent();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(ConstantUtil.KEY_TYPE_ID, mBoardInfo.getTypeId());
        hashMap.put(ConstantUtil.KEY_FRIDGE_TYPE, mBoardInfo.getFridgeType());
        String infoString = JSON.toJSONString(hashMap);
        intent.putExtra(ConstantUtil.KEY_INFO, infoString);
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        sendBroadcast(intent);
    }

    public void sendControlReadyInfo() {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        intent.putExtra(ConstantUtil.KEY_READY, mIsModelReady);
        sendBroadcast(intent);
        MyLogUtil.i(TAG, readyCounts + " sendControlCmdResponse main board is " + mIsModelReady);
    }

    public void sendSterilizeStatus(){
        HashMap<String,Integer> sterilizeHashMap = new HashMap<>();
        sterilizeHashMap.put("time",countsSterilizeRun);
        sterilizeHashMap.put("run",nSterilizeRun);
        String sterilizeJson = JSON.toJSONString(sterilizeHashMap);
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        intent.putExtra(ConstantUtil.KEY_STERILIZE_STATUS, sterilizeJson);
        sendBroadcast(intent);
    }

    public void sendControlCmdResponse() {
        MyLogUtil.d(TAG, "sendControlCmdResponse in");
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        //        intent.putExtra(ConstantUtil.KEY_CONTROL_INFO,(Serializable)mModel.getControlEntries());
        String controlJson = JSON.toJSONString(mModel.getControlEntries());
        intent.putExtra(ConstantUtil.KEY_CONTROL_INFO, controlJson);
        sendBroadcast(intent);
        MyLogUtil.d(TAG, "sendControlCmdResponse out");
    }

    public void notifyTemperChanged(ArrayList<FridgeStatusEntry> statusEntries) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        //        MyLogUtil.i(TAG,"notifyTemperChanged statusEntries.size="+statusEntries.size());
        //        intent.putExtra(ConstantUtil.KEY_TEMPER,(Serializable)statusEntries);
        //        Bundle bundle = new Bundle();
        //        intent.putExtra("key",bundle);
        //        bundle.putSerializable(ConstantUtil.KEY_TEMPER, (Serializable)statusEntries);
        String statusJson = JSON.toJSONString(statusEntries); // [{"id":123,“name”：“”，“value”，}, {{"id":123,“name”：“”，“value”，}, {  }]
        intent.putExtra(ConstantUtil.KEY_TEMPER, statusJson);
        sendBroadcast(intent);
    }

    public void notifyErrorOccurred(List<FridgeStatusEntry> statusEntries) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        //        intent.putExtra(ConstantUtil.KEY_ERROR,(Serializable)statusEntries);
        String controlJson = JSON.toJSONString(statusEntries);
        intent.putExtra(ConstantUtil.KEY_ERROR, controlJson);
        sendBroadcast(intent);
    }

    public void provideTempRange() {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        HashMap<String, Integer> hashMapRange = new HashMap<>();
        hashMapRange.put("fridgeMinValue", mMBParams.getTargetTempRange().getFridgeMinValue());
        hashMapRange.put("fridgeMaxValue", mMBParams.getTargetTempRange().getFridgeMaxValue());
        hashMapRange.put("freezeMinValue", mMBParams.getTargetTempRange().getFreezeMinValue());
        hashMapRange.put("freezeMaxValue", mMBParams.getTargetTempRange().getFreezeMaxValue());
        hashMapRange.put("changeMinValue", mMBParams.getTargetTempRange().getChangeMinValue());
        hashMapRange.put("changeMaxValue", mMBParams.getTargetTempRange().getChangeMaxValue());
        String rangeJson = JSON.toJSONString(hashMapRange);
        intent.putExtra(ConstantUtil.KEY_RANGE, rangeJson);
        sendBroadcast(intent);
    }

    /*public void provideFridgeTempRange(){
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.SERVICE_NOTICE);
        intent.putExtra(ConstantUtil.FRIDGE_TEMP_MAX, mMBParams.getTargetTempRange().getFridgeMaxValue());
        intent.putExtra(ConstantUtil.FRIDGE_TEMP_MIN, mMBParams.getTargetTempRange().getFridgeMinValue());
        sendBroadcast(intent);
        MyLogUtil.i(TAG,"provideFridgeTempRange MaxValue="+mMBParams.getTargetTempRange().getFridgeMaxValue());
        MyLogUtil.i(TAG,"provideFridgeTempRange MinValue="+mMBParams.getTargetTempRange().getFridgeMinValue());
        MyLogUtil.i(TAG,"provideFridgeTempRange out");
    }*/

    /*public void provideChangeTempRange(){
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_CHANGE_RANGE);
        intent.putExtra(ConstantUtil.CHANGE_TEMP_MAX, mMBParams.getTargetTempRange().getChangeMaxValue());
        intent.putExtra(ConstantUtil.CHANGE_TEMP_MIN, mMBParams.getTargetTempRange().getChangeMinValue());
        sendBroadcast(intent);
    }

    public void provideFreezeTempRange(){
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_FREEZE_RANGE);
        intent.putExtra(ConstantUtil.FREEZE_TEMP_MAX, mMBParams.getTargetTempRange().getFreezeMaxValue());
        intent.putExtra(ConstantUtil.FREEZE_TEMP_MIN, mMBParams.getTargetTempRange().getFreezeMinValue());
        sendBroadcast(intent);
    }*/


    public FridgeControlEntry getEntryByName(EnumBaseName name) {
        return mModel.getControlEntryByName(name);
    }

    public static final long COLDTIME = 60 * 60 * 3;
    //TODO:test use 1 min for quick cold time out
    //    public static final long COLDTIME = 60 * 60;
    public static final long FREEZETIME = 60 * 60 * 50;
    //TODO:test use 2 min for quick freeze time out
    //    public static final long FREEZETIME = 60 * 60;
    private static long coldCount = 0;
    private static long freezeCount = 0;
    private ScheduledExecutorService sExService = Executors.newScheduledThreadPool(2);
    private RunnableFuture<?> sColdOnFuture;
    private RunnableFuture<?> sFreezeOnFuture;
    private Timer timerColdOn, timerFreezeOn;
    private TimerTask taskColdOn, taskFreezeOn;


    /**
     * 开始速冷计时
     *
     * @param reset ture:重新开始计时 false:继续计时
     */
    public void startColdOnTime(boolean reset) {
        MyLogUtil.i(TAG, "startColdOnTime Count");
        if (reset) {
            coldCount = 0;
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDTIME, System.currentTimeMillis() / 1l);
        } else {
            coldCount = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDCOUNT, 0l);
            long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDTIME, 0l);
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - oldTime;
            if (delta > 0) {
                delta = delta / 10000;
                MyLogUtil.i(TAG, "cold delta count=" + delta);
                if (delta > coldCount) {
                    coldCount = delta;
                    MyLogUtil.i(TAG, "delta cold count=" + coldCount);
                }
            }
        }
        if (timerColdOn == null) {
            timerColdOn = new Timer();
        }
        if (taskColdOn == null) {
            taskColdOn = new TimerTask() {
                @Override
                public void run() {
                    coldCount++;
                    MyLogUtil.i(TAG, "coldOnRunnable coldCount=" + coldCount);
                    if (coldCount % 6 == 0) {//1min
                        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDCOUNT, coldCount / 1l);
                        //10min
                        //TODO:确认是否还需要校准时间
                        if (coldCount % 60 == 0) {
                            long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDTIME, 0l);
                            long currentTime = System.currentTimeMillis();
                            long delta = currentTime - oldTime;
                            if (delta > 0) {
                                delta = delta / 10000;
                                MyLogUtil.i(TAG, "cold delta count=" + delta);
                                if (delta > coldCount) {
                                    coldCount = delta;
                                    MyLogUtil.i(TAG, "delta cold count=" + coldCount);
                                }
                            }
                        }
                    }

                    if ((coldCount * 10) >= COLDTIME) {
                        MyLogUtil.i(TAG, "stop cold count =" + coldCount);
                        stopColdOnTime();
                        mModel.coldOff();
                    }
                }
            };
            timerColdOn.schedule(taskColdOn, 10000, 10000);
        }
        //        sColdOnFuture = (RunnableScheduledFuture<?>) sExService.scheduleAtFixedRate(coldOnRunnable,10,10, TimeUnit.SECONDS);//10s周期
//        timerColdOn.schedule(taskColdOn, 10000, 10000);
    }

    public void stopColdOnTime() {
        MyLogUtil.i(TAG, "stopColdOnTime cancel runnable Count");
        if (taskColdOn != null) {
            taskColdOn.cancel();
            taskColdOn = null;
        }
        if (timerColdOn != null) {
            timerColdOn.cancel();
            timerColdOn = null;
        }
        long currentTime = System.currentTimeMillis();
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDEND, currentTime);

    }


    public void startFreezeOnTime(boolean reset) {
        MyLogUtil.i(TAG, "startFreezeOnTime Count");
        //        sFreezeOnFuture = (RunnableScheduledFuture<?>) sExService.scheduleAtFixedRate(freezeOnRunnable,10,10, TimeUnit.SECONDS);//10s周期
        if (reset) {
            freezeCount = 0;
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZETIME, System.currentTimeMillis() / 1l);
        } else {
            freezeCount = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZECOUNT, 0l);
            long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZETIME, 0l);
            long currentTime = System.currentTimeMillis();
            long delta = currentTime - oldTime;
            if (delta > 0) {
                delta = delta / 10000;
                MyLogUtil.i(TAG, "freeze delta count=" + delta);
                if (delta > freezeCount) {
                    freezeCount = delta;
                    MyLogUtil.i(TAG, "delta freeze count=" + freezeCount);
                }
            }
        }
        if (timerFreezeOn == null) {
            timerFreezeOn = new Timer();
        }
        if (taskFreezeOn == null) {
            taskFreezeOn = new TimerTask() {
                @Override
                public void run() {
                    freezeCount++;
                    MyLogUtil.i(TAG, "freezeOnRunnable freezeCount=" + freezeCount);
                    if (freezeCount % 6 == 0) {//1min
                        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZECOUNT, freezeCount / 1l);
                        //10min
                        //TODO:确认是否还需要校准时间
                        if (freezeCount % 60 == 0) {
                            long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZETIME, 0l);
                            long currentTime = System.currentTimeMillis();
                            long delta = currentTime - oldTime;
                            if (delta > 0) {
                                delta = delta / 10000;
                                MyLogUtil.i(TAG, "freeze delta count=" + delta);
                                if (delta > freezeCount) {
                                    freezeCount = delta;
                                    MyLogUtil.i(TAG, "delta freeze count=" + freezeCount);
                                }
                            }
                        }
                    }
                    if ((freezeCount * 10) >= FREEZETIME) {
                        MyLogUtil.i(TAG, "freezeOnRunnable stop freezeCount=" + freezeCount);
                        stopFreezeOnTime();
                        mModel.freezeOff();
                    }
                }
            };
            timerFreezeOn.schedule(taskFreezeOn, 10000, 10000);
        }

    }

    public void stopFreezeOnTime() {
        MyLogUtil.i(TAG, "stopFreezeOnTime cancel runnable Count");
        if (taskFreezeOn != null) {
            taskFreezeOn.cancel();
            taskFreezeOn = null;
        }
        if (timerFreezeOn != null) {
            timerFreezeOn.cancel();
            timerFreezeOn = null;
        }
        long currentTime = System.currentTimeMillis();
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZEEND, currentTime);
    }

    private void handleBootEvent() {
        MyLogUtil.i(TAG, "handleBootEvent");

        FridgeControlEntry coldEntry = new FridgeControlEntry("quickColdMode");
        getControlDbMgr().queryByName(coldEntry);
        //速冷on
        if (coldEntry.value == 1) {
            startColdOnTime(true);
        }

        FridgeControlEntry freezeEntry = new FridgeControlEntry("quickFreezeMode");
        getControlDbMgr().queryByName(freezeEntry);
        //速冻on
        if (freezeEntry.value == 1) {
            startFreezeOnTime(true);
        }

        FridgeControlEntry sterilizeEntry = new FridgeControlEntry(EnumBaseName.SterilizeMode.name());
        getControlDbMgr().queryByName(sterilizeEntry);
        //杀菌on
        if (sterilizeEntry.value != 0) {
            startSterilize(sterilizeEntry.value,true);
        }
    }

    private void handleServiceRestartEvent() {
        MyLogUtil.i(TAG, "handleServiceRestartEvent in");
        FridgeControlEntry coldEntry = new FridgeControlEntry("quickColdMode");
        getControlDbMgr().queryByName(coldEntry);
        //速冷on
        if (coldEntry.value == 1) {
            MyLogUtil.i(TAG, "handleServiceRestartEvent cold");
            //继续计时，累加restart前coldCount值
            //            coldCount = (long)SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDCOUNT, 0l);
            //            mModel.coldOn();
            startColdOnTime(false);
        }

        FridgeControlEntry freezeEntry = new FridgeControlEntry("quickFreezeMode");
        getControlDbMgr().queryByName(freezeEntry);
        //速冻on
        if (freezeEntry.value == 1) {
            MyLogUtil.i(TAG, "handleServiceRestartEvent freeze");
            //继续计时，累加restart前freezeCount值
            //            freezeCount = (long)SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZECOUNT, 0l);
            //重发速冻命令
            //            mModel.freezeOn();
            startFreezeOnTime(false);
        }
        MyLogUtil.i(TAG, "handleServiceRestartEvent out");

        FridgeControlEntry sterilizeEntry = new FridgeControlEntry(EnumBaseName.SterilizeMode.name());
        getControlDbMgr().queryByName(sterilizeEntry);
        //杀菌on
        if (sterilizeEntry.value != 0) {
            startSterilize(sterilizeEntry.value,false);
        }
    }

    public void sendUserCommand(EnumBaseName enumBaseName, int value) {
        switch (enumBaseName) {
            case smartMode:
                if (value == 1) {
                    mModel.smartOn();
                } else {
                    mModel.smartOff();
                }
                break;
            case holidayMode:
                if (value == 1) {
                    mModel.holidayOn();
                } else {
                    mModel.holidayOff();
                }
                break;
            case quickColdMode:
                if (value == 1) {
                    mModel.coldOn();
                    startColdOnTime(true);
                } else {
                    mModel.coldOff();
                    stopColdOnTime();
                }
                break;
            case quickFreezeMode:
                if (value == 1) {
                    mModel.freezeOn();
                    startFreezeOnTime(true);
                } else {
                    mModel.freezeOff();
                    stopFreezeOnTime();
                }
                break;
            case fridgeSwitch:
                if (value == 1) {
                    mModel.refrigeratorOpen();
                } else {
                    mModel.refrigeratorClose();
                }
                break;
            case fridgeTargetTemp:
                mModel.setCold(value);
                break;
            case freezeTargetTemp:
                mModel.setFreeze(value);
                break;
            case changeTargetTemp:
                mModel.setCustomArea(value);
                break;
            case tidbitMode:
                if (value == 1) {
                    mModel.tidbitOn();
                } else {
                    mModel.tidbitOff();
                }
                break;
            case purifyMode:
                if (value == 1) {
                    mModel.purifyOn();
                } else {
                    mModel.purifyOff();
                }
                break;
            case SterilizeMode:
                if (value < 0) {
                    value = 0;
                }
                if (value > 9) {
                    value = 9;
                }
                mModel.setSterilizeMode(value);
            default:
                mModel.getControlEntries();
        }
        MyLogUtil.d("printSerialString", "6");
        sendQuery();
    }

    public void sendQuery() {
        Intent intent = new Intent();
        intent.setAction(ConstantWifiUtil.ACTION_CONTROL);
        intent.putExtra(ConstantWifiUtil.KEY_GETSTATE, getQueryResult());
        byte[] bytes = mMBParams.getDataBaseToBytes();
        if (bytes != null) {
            intent.putExtra(ConstantWifiUtil.KEY_GETBYTES, bytes);
        }
        MyLogUtil.i("printSerialString", PrintUtil.BytesToString(bytes, 16));
        intent.putExtra(ConstantWifiUtil.KEY_TYPE_ID, mMBParams.getTypeId());
        MyLogUtil.i("printSerialString", mMBParams.getFridgeId());
        sendBroadcast(intent);
        Log.d(TAG, "send query");
    }

    private HashMap getQueryResult() {
        HashMap<String, String> stateList = new HashMap<>();
        FridgeControlEntry fridgeControlEntry = new FridgeControlEntry(EnumBaseName.SterilizeMode.toString());
        mDBHandle.getControlDbMgr().queryByName(fridgeControlEntry);
        int valueSterilization = fridgeControlEntry.value;
        stateList.put(ConstantWifiUtil.QUERY_GOOD_FOOD, "65278");
        stateList.put(ConstantWifiUtil.QUERY_UV, "" + valueSterilization);
        return stateList;
    }

    /**
     * 杀菌功能启用这个
     */
    private final int SterilizeRun = 30 * 60;//杀菌运行时间 默认30分钟 1800s
    private final int[] SterilizeInterval = //杀菌循环时间
            {0, 6*60 * 60, 4 * 60 * 60, 3 * 60 * 60, 4 * 60 * 60, 5 * 60 * 60, 6 * 60 * 60, 7 * 60 * 60, 8 * 60 * 60, 9 * 60 * 60};
//    private final int SterilizeRun = 10 * 60;//杀菌运行时间 默认10分钟 1800s 测试用
//    private final int[] SterilizeInterval = //杀菌循环时间 测试用
//            {0, 60 * 60, 40 * 60, 30 * 60, 40 * 60, 50 * 60, 60 * 60, 70 * 60, 80 * 60, 90 * 60};
    private Timer timerSterilize;
    private TimerTask timerTaskSterilize;
    private int countsSterilizeInterval;
    private int countsSterilizeRun;
    private int nSterilizeRun = 0;
    private int timeSterilizeInterval;
    private void continueSterilize(){
        long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.STERILIZETIME, (long) 1);
        long currentTime = System.currentTimeMillis();
        int detal = (int) ((currentTime - oldTime) / 1000);
        countsSterilizeInterval = detal;

        FridgeControlEntry sterilizeEntry = new FridgeControlEntry(EnumBaseName.SterilizeSwitch.name());
        getControlDbMgr().queryByName(sterilizeEntry);
        if(sterilizeEntry.value ==1){
            countsSterilizeRun = SterilizeRun;
        }else {
            countsSterilizeRun = -1;
        }
    }
    public void startSterilize(final int mode,boolean reset) {
        if (mode != 0) {
            timeSterilizeInterval = SterilizeInterval[mode];
            MyLogUtil.i(TAG, "ster::timeSterilizeInterval " + timeSterilizeInterval);
            if(reset){
                MyLogUtil.i(TAG,"ster::reset");
                countsSterilizeRun = SterilizeRun;
                countsSterilizeInterval = 0;
                long currentTime = System.currentTimeMillis();
                SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.STERILIZETIME, currentTime);
                SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.STERILIZETIMEINTERVALCOUNT, countsSterilizeInterval);
            }else {
                continueSterilize();
            }
            nSterilizeRun = 0;

            if (timerSterilize == null) {
                timerSterilize = new Timer();
            }
            if (timerTaskSterilize == null) {
                timerTaskSterilize = new TimerTask() {
                    @Override
                    public void run() {

                        countsSterilizeInterval++;
                        MyLogUtil.i(TAG, "ster::countsSterilizeInterval " + countsSterilizeInterval+" "+timeSterilizeInterval);

                        /**
                         * 倒计时时间到，停止杀菌，如果有开门事件暂停倒计时
                         */
                        if (mBoardInfo.getFridgeDoorStatus() == 0) {
                            if(nSterilizeRun == 0) {//运行状态改变发送恢复倒计时广播
                                nSterilizeRun = 1;
                                sendSterilizeStatus();
                            }
                            if (countsSterilizeRun > 0) {
                                countsSterilizeRun--;
                                MyLogUtil.i(TAG, "ster::countsSterilizeRun " + countsSterilizeRun);
                            } else if (countsSterilizeRun == 0) {
                                countsSterilizeRun--;
                                mModel.sterilizeSwitchOff();//倒计时结束 发送关闭
                            }
                        }else {
                            if(nSterilizeRun == 1) {//运行状态改变发送暂停倒计时广播
                                nSterilizeRun = 0;
                                sendSterilizeStatus();//
                            }
                        }
                        /**
                         * 1分钟做一次时间校准并记录sp
                         */
                        if (countsSterilizeInterval % 60 == 0) {
                            MyLogUtil.i(TAG, "ster::detal counts ");
                            long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.STERILIZETIME, (long) 1);
                            long currentTime = System.currentTimeMillis();
                            int detal = (int) ((currentTime - oldTime) / 1000);
                            MyLogUtil.i(TAG, "ster::detal " + detal);
                            if (detal != countsSterilizeInterval) {
                                countsSterilizeInterval = detal;
                            }
                            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.STERILIZETIMEINTERVALCOUNT, countsSterilizeInterval);
                        }
                        /**
                         * 当间隔时间到时重新开启倒计时，并重新记录启示时间
                         */
                        if (countsSterilizeInterval >= timeSterilizeInterval) {
                            countsSterilizeInterval = 0;
                            countsSterilizeRun = SterilizeRun;
                            mModel.sterilizeSwitchOn();
                            long currentTime = System.currentTimeMillis();
                            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.STERILIZETIME, currentTime);
                            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.STERILIZETIMEINTERVALCOUNT, countsSterilizeInterval);
                        }
                    }
                };
                timerSterilize.schedule(timerTaskSterilize, 1000, 1000);
            }
        }
    }

    public void stopSterilize() {
//        mModel.sterilizeSwitchOff();
        if (timerTaskSterilize != null) {
            timerTaskSterilize.cancel();
            timerTaskSterilize = null;
        }
        if (timerSterilize != null) {
            timerSterilize.cancel();
            timerSterilize = null;
        }
    }


} //End of class
