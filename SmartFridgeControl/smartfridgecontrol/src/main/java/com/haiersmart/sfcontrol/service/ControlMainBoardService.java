package com.haiersmart.sfcontrol.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.haiersmart.sfcontrol.application.ControlApplication;
import com.haiersmart.sfcontrol.constant.ConstantUtil;
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
import com.haiersmart.sfcontrol.utilslib.SpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlMainBoardService extends Service {
    static final String TAG = "ControlMainBoardService";
    private DBOperation mDBHandle;
    private MainBoardParameters mMBParams;
    private PowerProcessData mProcessData;
    private ControlMainBoardInfo mBoardInfo;
    private ModelFactory mModelFactory;
    private ModelBase mModel;
    private boolean mIsModelReady = false;
    private boolean mIsServiceRestart = false;
    private static int readyCounts = 0;


    public  ControlMainBoardService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyLogUtil.i(TAG,"Bind Service");
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
        if(intent == null) {
            //while system auto restart this service, this case should be run
            MyLogUtil.i(TAG,"onStartCommand intent=NULL to handle quick cold or freeze timer event!");
            mIsServiceRestart = true;
        } else {
            String action = intent.getAction();
            MyLogUtil.i(TAG, "onStartCommand action=" + action);
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ConstantUtil.QUERY_CONTROL_READY://查询service是否准备好
                        MyLogUtil.i(TAG, readyCounts + " sendControlCmdResponse before main board is " + mIsModelReady);
                        readyCounts++;
                        sendControlReadyInfo();
                        break;
                    case ConstantUtil.BROADCAST_ACTION_QUERY_BACK:
                        handleQueryData();
                        break;
                    case ConstantUtil.BROADCAST_ACTION_STATUS_BACK:
//                MyLogUtil.d(TAG, "onStartCommand status back");
                        mModel.handleStatusDataResponse();
                        if (!mIsModelReady) {
                            mIsModelReady = true;
                        }
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
                            mModel.setCold(temperCold);
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
                            mModel.setFreeze(temperCold);
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
                            mModel.setCustomArea(temperCold);
                        } else {
                            MyLogUtil.i(TAG, "onStartCommand action changed to QUERY_CONTROL_READY due to init not finished");
                            sendControlReadyInfo();
                        }
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
    public void onDestroy() {
        super.onDestroy();
        mProcessData.PowerProcDataStop();
        stopColdOnTime();
        stopFreezeOnTime();
    }

    public void initService() {
        MyLogUtil.i(TAG,"kill initService");
        //Create Database
        mDBHandle =  DBOperation.getInstance();

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
        MyLogUtil.i(TAG,"kill initModel");
        mModelFactory = new ModelFactory(this);
        mModel = mModelFactory.createModel(modeName);
        mModel.init();
    }


   private void handleActions(String action, int ... value) {

        switch (action) {
            case ConstantUtil.MODE_SMART_ON://智能开
                MyLogUtil.i(TAG, "handleActions smartOn");
                mModel.smartOn();
                break;
            case ConstantUtil.MODE_SMART_OFF://智能关
                MyLogUtil.i(TAG, "handleActions smartOff");
                mModel.smartOff();
                break;
            case ConstantUtil.MODE_FREEZE_ON://速冻开
                mModel.freezeOn();
                startFreezeOnTime();
                break;
            case ConstantUtil.MODE_FREEZE_OFF://速冻关
                mModel.freezeOff();
                stopFreezeOnTime();
                break;
            case ConstantUtil.MODE_HOLIDAY_ON://假日开
                mModel.holidayOn();
                break;
            case ConstantUtil.MODE_HOLIDAY_OFF://假日关
                mModel.holidayOff();
                break;
            case ConstantUtil.MODE_COLD_ON://变温开
                mModel.coldOn();
                startColdOnTime();
                break;
            case ConstantUtil.MODE_COLD_OFF://变温关
                mModel.coldOff();
                stopColdOnTime();
                break;
            case ConstantUtil.REFRIGERATOR_OPEN://冷藏开
                mModel.refrigeratorOpen();
                break;
            case ConstantUtil.REFRIGERATOR_CLOSE://冷藏关
                mModel.refrigeratorClose();
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
            case ConstantUtil.QUERY_FRIDGE_TEMP_RANGE://查询冷藏室温度档位范围
                provideFridgeTempRange();
                break;
            case ConstantUtil.QUERY_CHANGE_TEMP_RANGE://查询变温室温度档位范围
                provideChangeTempRange();
                break;
            case ConstantUtil.QUERY_FREEZE_TEMP_RANGE://查询冷冻室温度档位范围
                provideFreezeTempRange();
                break;
            default:
                break;
        }
    }

   private void handleQueryData(){
       MyLogUtil.i(TAG, "handleQueryData");
       String fridgeId = mBoardInfo.getFridgeId();
       String fridgeType = mBoardInfo.getFridgeType();
       MyLogUtil.i(TAG,"handleQueryData fridgeId=" +fridgeId);
       MyLogUtil.i(TAG,"handleQueryData fridgeType=" +fridgeType);

       if( mModel==null ) {
           initModel(fridgeType);
           //broadcast fridgeId to app
           sendFridgeInfoResponse();
       }

       //update database if value changed
        List<FridgeInfoEntry> infoEntryList = mDBHandle.getInfoDbMgr().query();
        if(infoEntryList.size() > 0) {
            String fridgeVersion= mBoardInfo.getFridgeVersion();
            String fridgeFactory = mBoardInfo.getFridgeFactory();
            String fridgeSn = mBoardInfo.getFridgeSn();

            if(infoEntryList.get(0).value != fridgeId ||
                    infoEntryList.get(1).value != fridgeVersion ||
                    infoEntryList.get(2).value != fridgeFactory ||
                    infoEntryList.get(3).value != fridgeSn) {
                //update to db
                ArrayList<FridgeInfoEntry> tempList = new ArrayList<FridgeInfoEntry>();
                tempList.add(new FridgeInfoEntry("fridgeId",fridgeId));
                tempList.add(new FridgeInfoEntry("fridgeVersion",fridgeVersion));
                tempList.add(new FridgeInfoEntry("fridgeFactory",fridgeFactory));
                tempList.add(new FridgeInfoEntry("fridgeSn",fridgeSn));
                mDBHandle.getInfoDbMgr().updateAll(tempList);
            }
        }
    }

    public void sendFridgeInfoResponse() {
        //broadcast fridgeId to app
        Intent intent = new Intent();
        String fridgeId = mBoardInfo.getFridgeId();
        String fridgeType = mBoardInfo.getFridgeType();
        intent.putExtra(ConstantUtil.KEY_FRIDGE_ID, fridgeId);
        intent.putExtra(ConstantUtil.KEY_FRIDGE_TYPE, fridgeType);
        intent.setAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_INFO);
        sendBroadcast(intent);
    }

    public void sendControlReadyInfo() {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_READY);
        intent.putExtra(ConstantUtil.KEY_READY, mIsModelReady);
        sendBroadcast(intent);
        MyLogUtil.i(TAG,readyCounts+" sendControlCmdResponse main board is "+mIsModelReady);
    }

    public void sendControlCmdResponse() {
        MyLogUtil.d(TAG, "sendControlCmdResponse in");
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_CONTROL);
//        intent.putExtra(ConstantUtil.KEY_CONTROL_INFO,(Serializable)mModel.getControlEntries());
        String controlJson = JSON.toJSONString(mModel.getControlEntries());
        intent.putExtra(ConstantUtil.KEY_CONTROL_INFO,controlJson);
        sendBroadcast(intent);
        MyLogUtil.d(TAG, "sendControlCmdResponse out");
    }

    public void notifyTemperChanged( ArrayList<FridgeStatusEntry> statusEntries) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_TEMPER);
//        MyLogUtil.i(TAG,"notifyTemperChanged statusEntries.size="+statusEntries.size());
//        intent.putExtra(ConstantUtil.KEY_TEMPER,(Serializable)statusEntries);
//        Bundle bundle = new Bundle();
//        intent.putExtra("key",bundle);
//        bundle.putSerializable(ConstantUtil.KEY_TEMPER, (Serializable)statusEntries);
        String statusJson = JSON.toJSONString(statusEntries); // [{"id":123,“name”：“”，“value”，}, {{"id":123,“name”：“”，“value”，}, {  }]
        intent.putExtra(ConstantUtil.KEY_TEMPER,statusJson);
        sendBroadcast(intent);
    }

    public void notifyErrorOccurred(List<FridgeStatusEntry> statusEntries) {
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_ERROR);
//        intent.putExtra(ConstantUtil.KEY_ERROR,(Serializable)statusEntries);
        String controlJson = JSON.toJSONString(statusEntries);
        intent.putExtra(ConstantUtil.KEY_ERROR,controlJson);
        sendBroadcast(intent);
    }

    public void provideFridgeTempRange(){
        Intent intent = new Intent();
        intent.setAction(ConstantUtil.BROADCAST_ACTION_FRIDGE_RANGE);
        intent.putExtra(ConstantUtil.FRIDGE_TEMP_MAX, mMBParams.getTargetTempRange().getFridgeMaxValue());
        intent.putExtra(ConstantUtil.FRIDGE_TEMP_MIN, mMBParams.getTargetTempRange().getFridgeMinValue());
        sendBroadcast(intent);
        MyLogUtil.i(TAG,"provideFridgeTempRange MaxValue="+mMBParams.getTargetTempRange().getFridgeMaxValue());
        MyLogUtil.i(TAG,"provideFridgeTempRange MinValue="+mMBParams.getTargetTempRange().getFridgeMinValue());
        MyLogUtil.i(TAG,"provideFridgeTempRange out");
    }

    public void provideChangeTempRange(){
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
    }


    public FridgeControlEntry getEntryByName(EnumBaseName name) {
        return mModel.getControlEntryByName(name);
    }

//    public static final long COLDTIME = 60 * 60 * 3;
    //TODO:test use 1 min for quick cold time out
    public static final long COLDTIME = 60 * 11;
//    public static final long FREEZETIME = 60 * 60 * 50;
    //TODO:test use 2 min for quick freeze time out
    public static final long FREEZETIME = 60 * 12;
    private static long coldCount = 0;
    private static long freezeCount = 0;
    private ScheduledExecutorService sExService = Executors.newScheduledThreadPool(2);
    private RunnableFuture<?> sColdOnFuture;
    private RunnableFuture<?> sFreezeOnFuture;
    private Runnable coldOnRunnable = new Runnable() {
        @Override
        public void run() {
            coldCount++;
            MyLogUtil.i(TAG, "coldOnRunnable coldCount=" + coldCount);
            if(coldCount % 6 == 0) {//1min
                SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDCOUNT,coldCount / 1l);
                //10min
                //TODO:确认是否还需要校准时间
                if(coldCount %  60 == 0 ) {
                    long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDTIME, 0l);
                    long currentTime = System.currentTimeMillis();
                    long delta = currentTime - oldTime;
                    if (delta > 0) {
                        delta = delta / 10000;
                        MyLogUtil.i(TAG, "cold delta=" + delta);
                        if (delta > coldCount) {
                            coldCount = delta;
                            MyLogUtil.i(TAG, "delta coldCount=" + coldCount);
                        }
                    }
                }
            }

            if ((coldCount*10) >= COLDTIME) {
                MyLogUtil.i(TAG, "stop coldCount  =" + coldCount);
                stopColdOnTime();
                mModel.coldOff();
            }

        }//end of run
    };

    public void startColdOnTime() {
        MyLogUtil.i(TAG, "startColdOnTime Count");
        sColdOnFuture = (RunnableScheduledFuture<?>) sExService.scheduleAtFixedRate(coldOnRunnable,10,10, TimeUnit.SECONDS);//10s周期
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDTIME, System.currentTimeMillis() / 1l);
    }
    
    public void stopColdOnTime() {
        MyLogUtil.i(TAG, "stopColdOnTime cancel runnable Count");
        if(sColdOnFuture != null) {
            boolean res = sColdOnFuture.cancel(true);
            MyLogUtil.i(TAG, "stopColdOnTime cancel runnable Count sColdOnFuture res="+res);
        }
        coldCount = 0;
        MyLogUtil.i(TAG, "stopColdOnTime cancel runnable coldCount="+coldCount );
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDCOUNT, 0l);
    }

    private Runnable freezeOnRunnable = new Runnable() {
        @Override
        public void run() {
            freezeCount++;
            MyLogUtil.i(TAG, "freezeOnRunnable freezeCount=" + freezeCount);
            if(freezeCount % 6 == 0) {//1min
                SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZECOUNT,freezeCount / 1l);
                //10min
                //TODO:确认是否还需要校准时间
                if(freezeCount %  60 == 0 ) {
                    long oldTime = (long) SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZETIME, 0l);
                    long currentTime = System.currentTimeMillis();
                    long delta = currentTime - oldTime;
                    if (delta > 0) {
                        delta = delta / 10000;
                        if (delta > freezeCount) {
                            freezeCount = delta;
                        }
                    }
                }
            }
            if ((freezeCount*10) >= FREEZETIME) {
                MyLogUtil.i(TAG, "freezeOnRunnable stop freezeCount=" + freezeCount);
                stopFreezeOnTime();
                mModel.freezeOff();
            }
        }//end of run
    };

    public void startFreezeOnTime() {
        MyLogUtil.i(TAG, "startFreezeOnTime Count");
        sFreezeOnFuture = (RunnableScheduledFuture<?>) sExService.scheduleAtFixedRate(freezeOnRunnable,10,10, TimeUnit.SECONDS);//10s周期
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZETIME, System.currentTimeMillis() / 1l);
//        sExService.schedule(new Runnable() {
//            @Override
//            public void run() {
//                sColdOnFuture.cancel(true);
//                coldCount = 0;
//                //更新数据库
//                SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDCOUNT,0l);
//                //退速冻
//                mModel.coldOff();
//            }
//        },COLDTIME,TimeUnit.SECONDS);
    }

    public void stopFreezeOnTime() {
        MyLogUtil.i(TAG, "stopFreezeOnTime cancel runnable Count");
        if(sFreezeOnFuture != null) {
            boolean res = sFreezeOnFuture.cancel(true);
            MyLogUtil.i(TAG, "stopFreezeOnTime cancel runnable Count sFreezeOnFuture res="+res);
        }
        freezeCount = 0;
        MyLogUtil.i(TAG, "stopFreezeOnTime cancel runnable freezeCount="+freezeCount );
        SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZECOUNT, 0l);
    }

    private void handleBootEvent() {
        MyLogUtil.i(TAG,"handleBootEvent");

        FridgeControlEntry coldEntry = new FridgeControlEntry("quickColdMode");
        getControlDbMgr().queryByName(coldEntry);
        //速冷on
        if(coldEntry.value == 1) {
            //重新计时，重新计算coldCount
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDTIME, System.currentTimeMillis() / 1l);
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.COLDCOUNT, 0l);
            //重发速冻命令
//            mModel.coldOn();
            startColdOnTime();
        }

        FridgeControlEntry freezeEntry = new FridgeControlEntry("quickFreezeMode");
        getControlDbMgr().queryByName(freezeEntry);
        //速冻on
        if(freezeEntry.value == 1) {
            //重新计时，重新计算freezeCount
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZETIME, System.currentTimeMillis() / 1l);
            SpUtils.getInstance(ControlApplication.getInstance()).put(ConstantUtil.FREEZECOUNT, 0l);
            //重发速冻命令
//            mModel.freezeOn();
            startFreezeOnTime();
        }
    }

    private void handleServiceRestartEvent() {
        MyLogUtil.i(TAG,"handleServiceRestartEvent in");
        FridgeControlEntry coldEntry = new FridgeControlEntry("quickColdMode");
        getControlDbMgr().queryByName(coldEntry);
        //速冻on
        if(coldEntry.value == 1) {
            MyLogUtil.i(TAG, "handleServiceRestartEvent cold");
            //继续计时，累加restart前coldCount值
            coldCount = (long)SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.COLDCOUNT, 0l);
//            mModel.coldOn();
            startColdOnTime();
        }

        FridgeControlEntry freezeEntry = new FridgeControlEntry("quickFreezeMode");
        getControlDbMgr().queryByName(freezeEntry);
        //速冻on
        if(freezeEntry.value == 1) {
            MyLogUtil.i(TAG, "handleServiceRestartEvent freeze");
            //继续计时，累加restart前freezeCount值
            freezeCount = (long)SpUtils.getInstance(ControlApplication.getInstance()).get(ConstantUtil.FREEZECOUNT, 0l);
            //重发速冻命令
//            mModel.freezeOn();
            startFreezeOnTime();
        }
        MyLogUtil.i(TAG,"handleServiceRestartEvent out");
    }

    public void sendUserCommand(EnumBaseName enumBaseName,int value){
        switch (enumBaseName){
            case smartMode:
                if(value == 1) {
                    mModel.smartOn();
                }else {
                    mModel.smartOff();
                }
                break;
            case holidayMode:
                if(value == 1) {
                    mModel.holidayOn();
                }else {
                    mModel.holidayOff();
                }
                break;
            case quickColdMode:
                if(value == 1) {
                    mModel.coldOn();
                    startColdOnTime();
                }else {
                    mModel.coldOff();
                    stopColdOnTime();
                }
                break;
            case quickFreezeMode:
                if(value == 1) {
                    mModel.freezeOn();
                    startFreezeOnTime();
                }else {
                    mModel.freezeOff();
                    stopFreezeOnTime();
                }
                break;
            case fridgeSwitch:
                if(value == 1) {
                    mModel.refrigeratorOpen();
                }else {
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
            default:
                mModel.getControlEntries();
        }

    }

} //End of class
