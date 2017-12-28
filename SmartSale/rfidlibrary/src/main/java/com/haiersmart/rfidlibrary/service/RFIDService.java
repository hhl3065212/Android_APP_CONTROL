package com.haiersmart.rfidlibrary.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;


import com.haiersmart.rfidlibrary.AndroidWakeLock;
import com.haiersmart.rfidlibrary.ConstantUtil;
import com.pow.api.cls.RfidPower;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RFIDService extends Service {
    static final String TAG = "RFIDService";
    private final int mAntPorts = 16;
    private final String mDevPath = "/dev/ttyUSB0";//"/dev/ttyS3";
    private Reader mReader;
    private ReaderParams mReaderParams;
    private RfidPower mRpower;

    private SPconfig mSpf;
    private boolean mNostop = true;// Changed by LTT
    private boolean mNeedreconnect;

    private Handler handler = new Handler( );
    public Map<String,Reader.TAGINFO> mTagsMap=new LinkedHashMap<String,Reader.TAGINFO>(); //有序

    AndroidWakeLock Awl;

    Timer mReadTimer = null;
    TimerTask mReadTimerTask;


    public RFIDService() {
    }

    public class MyBinder extends Binder {
        public RFIDService getService() {
            return RFIDService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private MyBinder binder = new MyBinder();

    @Override
    public void onCreate() {

//        Awl = new AndroidWakeLock((PowerManager) getSystemService(Context.POWER_SERVICE));
//        Awl.WakeLock();
        mReaderParams = new ReaderParams();
        mReader = new Reader();
        //设置平台选择
        RfidPower.PDATYPE PT = RfidPower.PDATYPE.valueOf(0);//select platform , default none
        mRpower = new RfidPower(PT);
        mNeedreconnect = false;
        mSpf = new SPconfig(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }



    @Override
    public void onDestroy() {
//        Awl.ReleaseWakeLock();
        super.onDestroy();
    }

    public void connectRFID() throws Exception {
        Log.d(TAG,"connectRFID in");
        boolean isPowerUp = mRpower.PowerUp();
        if(!isPowerUp) {
            Log.e(TAG,"RFID Power up failed!");
            throw new Exception("RFID Power up failed!");
        }

        Reader.READER_ERR er = mReader.InitReader_Notype(mDevPath, mAntPorts);
        if(er == Reader.READER_ERR.MT_OK_ERR) {
            Log.d(TAG,"RFID InitReader_Notype success!");
        } else {
            Log.e(TAG,"RFID InitReader_Notype failed by reason: " + er);
            throw new Exception(String.valueOf(er));
        }

        er = configProtocol();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            Log.e(TAG,"RFID InitReader_Notype failed by reason: " + er);
            throw new Exception("RFID configProtocol failed");
        }

        er = configAntPower();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            throw new Exception("RFID configAntPower failed");
        }
        
        er = configRegion();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            throw new Exception("RFID configRegion failed");
        }

        er = configOther();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            throw new Exception("RFID configOther failed");
        }
        Log.d(TAG,"connectRFID out");
    }

    public void disconnectRFID() {
        if(mReader != null) {
            mReader.CloseReader();
        }
        boolean isPowerDown = mRpower.PowerDown();
        if(!isPowerDown) {
            Log.e(TAG,"RFID Power down failed!");
        }
    }


    public void startRead() {
        boolean bl = true;
        if (mNeedreconnect) {
            int c = 0;
            do {
                bl = reconnect();
                c++;
                if (c > 0)
                    break;
            } while (true);
        }
        if (!bl)
            return;

        int metaflag = 0;
        metaflag |= 0X0001;//readcount
        metaflag |= 0X0002;//rssi
        metaflag |= 0X0004;//ant
        metaflag |= 0X0008;//fre
        metaflag |= 0X0040;//pro
        mReaderParams.option = (metaflag<<8);
        clearData();
        if(mNostop) {
            Reader.READER_ERR er = mReader.AsyncStartReading(
                    mReaderParams.uants,
                    mReaderParams.uants.length,
                    mReaderParams.option);
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                Log.e(TAG, " startRead AsyncStartReading er="  + String.valueOf(er.value())+ ", er:" + er.toString());
                Toast.makeText(this,
                        ConstantUtil.Constr_nostopreadfailed,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        handler.postDelayed(runnable_read, 0);
    }

    public void stopRead() {
        if (mNostop) {
            Log.d(TAG, " stopRead");
            Reader.READER_ERR er = mReader.AsyncStopReading();
            if (er != Reader.READER_ERR.MT_OK_ERR) {
                Log.e(TAG, " stopRead AsyncStopReading er="  + String.valueOf(er.value())+ ", er:" + er.toString());
                Toast.makeText(this, ConstantUtil.Constr_nostopspreadfailed,
                        Toast.LENGTH_SHORT).show();
            }
        }
        handler.removeCallbacks(runnable_read);
//        Awl.ReleaseWakeLock();
    }

    private Runnable runnable_read = new Runnable() {
        public void run() {
            String[] tag = null;
            int[] tagcnt = new int[1];
            tagcnt[0] = 0;

            synchronized (this) {
                // Log.d(TAG, "runnable_read");
                Reader.READER_ERR er;
                if (mNostop) {
                    er = mReader.AsyncGetTagCount(tagcnt);//quick read mode
                } else {
                    er = mReader.TagInventory_Raw(mReaderParams.uants,
                            mReaderParams.uants.length,
                            (short) mReaderParams.readtime, tagcnt);
                }
//				Log.i(TAG,"read:" + er.toString() + " cnt:"+ String.valueOf(tagcnt[0]));
                if (er == Reader.READER_ERR.MT_OK_ERR) {
                    if (tagcnt[0] > 0) {
                        tag = new String[tagcnt[0]];
                        for (int i = 0; i < tagcnt[0]; i++) {
                            Reader.TAGINFO tfs = mReader.new TAGINFO();
                            if (mNostop)
                                er = mReader.AsyncGetNextTag(tfs);
                            else
                                er = mReader.GetNextTag(tfs);

//							Log.i(TAG,"get tag index:" + String.valueOf(i)+ ", er:" + er.toString());
                            if (er == Reader.READER_ERR.MT_OK_ERR) {
                                if(tfs.EpcId.length>30) {
                                    Log.w("MYINFO","debug tag invalid !!!!!!!!!!!!");
                                } else {
                                    tag[i] = Reader.bytes_Hexstr(tfs.EpcId);
                                    //刷新标签缓存
                                    TagsBufferResh(tag[i], tfs);
                                }
                            } else if (er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET) {
                                mNeedreconnect = true;
                                stopRead();
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                    handler.postDelayed(this, mReaderParams.sleep);
                    try {
                        notifyTagsChange();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_TOO_MANY_RESET ||
                        er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_HIGN_RETURN_LOSS ||
                        er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_HIGN_RETURN_LOSS  ||
                        er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_NO_ANTENNAS ||
                        er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_HIGH_TEMPERATURE ||
                        er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_READER_DOWN ||
                         er == Reader.READER_ERR.MT_HARDWARE_ALERT_ERR_BY_UNKNOWN_ERR) {
                        Log.e(TAG,"GetTag er:" + String.valueOf(er.value())+ ", er:" + er.toString());
                        mNeedreconnect = true;
                        stopRead();
                    } else {
//                        if(er != Reader.READER_ERR.MT_OP_INVALID) {
                            Log.e(TAG,"GetTag er:" + String.valueOf(er.value())+ ", er:" + er.toString());
//                        }
                        handler.postDelayed(this, mReaderParams.sleep);//continue get tags,ignore errs
                    }
                }
            }
        }
    };
    
    private void notifyTagsChange() throws JSONException {

        List<Reader.TAGINFO> tagList = new ArrayList<>();
        Iterator<Map.Entry<String,Reader.TAGINFO>> item = mTagsMap.entrySet().iterator();
        while (item.hasNext()){
            Map.Entry<String,Reader.TAGINFO> mapInfo = item.next();
            Reader.TAGINFO info = mapInfo.getValue();
            tagList.add(info);
        }
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<tagList.size(); i++) {
            Reader.TAGINFO tag = tagList.get(i);
            JSONObject jo = new JSONObject();
            String epcstr = Reader.bytes_Hexstr(tag.EpcId);
            jo.put("EpcId", epcstr);
            jo.put("ReadCnt", tag.ReadCnt);
            jo.put("AntennaID", String.valueOf(tag.AntennaID));
            jo.put("RSSI", String.valueOf(tag.RSSI));
            jo.put("Frequency", String.valueOf(tag.Frequency));
            jsonArray.put(jo);
        }
        String json = jsonArray.toString();
        Intent intent = new Intent();
        intent.setAction("com.haiersmart.action.rfid");
        intent.putExtra("rfidTags", json);
        sendBroadcast(intent);
    }

    public void handleReadTime(int microSecond) {
        if(mReadTimer != null) {
            mReadTimer.cancel();
            mReadTimer.purge();
            mReadTimer = null;
        }

        mReadTimer = new Timer();
        mReadTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("LTT mReadTimerTask", "读取时间到");
              //  updateUIHandler.sendEmptyMessage(2);
                stopRead();
                mReadTimer.cancel();
                mReadTimer.purge();
                mReadTimer = null;
            }
        };
        mReadTimer.schedule(mReadTimerTask,microSecond);//3000

    }

    private void clearData() {
        mTagsMap.clear();
    }

    private Reader.READER_ERR configProtocol() {
        mReaderParams = mSpf.ReadReaderParams();
        if(mReaderParams.invpro.size() < 1 ) {
            mReaderParams.invpro.add("GEN2");
        }
        List<Reader.SL_TagProtocol> ltp = new ArrayList<Reader.SL_TagProtocol>();
        for (int i = 0; i < mReaderParams.invpro.size(); i++) {
            if (mReaderParams.invpro.get(i).equals("GEN2")) {
                ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_GEN2);
            } else if (mReaderParams.invpro.get(i).equals("6B")) {
                ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_ISO180006B);
            } else if (mReaderParams.invpro.get(i).equals("IPX64")) {
                ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX64);
            } else if (mReaderParams.invpro.get(i).equals("IPX256")) {
                ltp.add(Reader.SL_TagProtocol.SL_TAG_PROTOCOL_IPX256);
            }
        }
        //设置盘存操作的协议
        Reader.Inv_Potls_ST ipst = mReader.new Inv_Potls_ST();
        ipst.potlcnt = ltp.size();
        ipst.potls = new Reader.Inv_Potl[ipst.potlcnt];
        Reader.SL_TagProtocol[] stp = ltp.toArray(new Reader.SL_TagProtocol[ipst.potlcnt]);
        Log.i(TAG, "ipst.potlcnt = " + ipst.potlcnt);
        for (int i = 0; i < ipst.potlcnt; i++) {
            Reader.Inv_Potl ipl = mReader.new Inv_Potl();
            ipl.weight = 30;
            ipl.potl = stp[i];
            ipst.potls[0] = ipl;
        }
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_INVPOTL, ipst);
        Log.i(TAG, "Connected set pro:"+er.toString());
        return er;
    }


    private Reader.READER_ERR configAntPower() {
        //设置天线检测配置
        Reader.READER_ERR er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_READER_IS_CHK_ANT,
                new int[]{ mReaderParams.checkant});
        Log.i(TAG, "Connected set checkant:"+er.toString());

        //设置读写器发射功率
        Reader.AntPowerConf apcf = mReader.new AntPowerConf();
        apcf.antcnt= mAntPorts;
        Log.i(TAG, "Connected set apcf.antcnt:"+ mAntPorts);
        for(int i=0;i<apcf.antcnt;i++)
        {
            Reader.AntPower jaap = mReader.new AntPower();
            jaap.antid=i+1;
           // Log.d(TAG, "Connected set jaap.antid:"+jaap.antid);
            jaap.readPower =(short) mReaderParams.rpow[i];
//            jaap.readPower = 2800;
           // Log.d(TAG, "Connected set jaap.readPower:"+jaap.readPower);
            jaap.writePower=(short) mReaderParams.wpow[i];
           // Log.d(TAG, "Connected set jaap.writePower:"+jaap.writePower);
            apcf.Powers[i]=jaap;
        }
        er = mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_RF_ANTPOWER, apcf);
        Log.i(TAG, "Connected set MTR_PARAM_RF_ANTPOWER:"+er.toString());
        return er;
    }

    private Reader.READER_ERR configRegion() {
        Reader.Region_Conf rre;
        Reader.READER_ERR er = Reader.READER_ERR.MT_OK_ERR;
        switch(mReaderParams.region)
        {
            case 0:
                rre = Reader.Region_Conf.RG_PRC;
                break;
            case 1:
                rre = Reader.Region_Conf.RG_NA;
                Log.i(TAG, "Connected set Region_Conf.RG_NA");
                break;
            case 2:
                rre= Reader.Region_Conf.RG_NONE;
                break;
            case 3:
                rre= Reader.Region_Conf.RG_KR;
                break;
            case 4:
                rre= Reader.Region_Conf.RG_EU;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            default:
                rre= Reader.Region_Conf.RG_NONE;
                break;
        }
        if(rre!= Reader.Region_Conf.RG_NONE)
        {
            er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_FREQUENCY_REGION,rre);
            Log.i(TAG, "Connected set MTR_PARAM_FREQUENCY_REGION:"+er.toString());
        }
        return er;
    }

    private Reader.READER_ERR  configOther() {
        Reader.READER_ERR er = Reader.READER_ERR.MT_OK_ERR;
        if(mReaderParams.frelen>0)
        {
            Reader.HoptableData_ST hdst=mReader.new HoptableData_ST();
            hdst.lenhtb=mReaderParams.frelen;
            hdst.htb=mReaderParams.frecys;
            er = mReader.ParamSet
                    (Reader.Mtr_Param.MTR_PARAM_FREQUENCY_HOPTABLE,hdst);
        }

        er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_SESSION,
                new int[]{mReaderParams.session});
        er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_Q,
                new int[]{mReaderParams.qv});
        er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_WRITEMODE,
                new int[]{mReaderParams.wmode});
        er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_MAXEPCLEN,
                new int[]{mReaderParams.maxlen});
        er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_POTL_GEN2_TARGET,
                new int[]{mReaderParams.target});

        if(mReaderParams.filenable==1)
        {
            Reader.TagFilter_ST tfst=mReader.new TagFilter_ST();
            tfst.bank= mReaderParams.filbank;
            tfst.fdata=new byte[mReaderParams.fildata.length()/2];
            mReader.Str2Hex(mReaderParams.fildata,
                    mReaderParams.fildata.length(), tfst.fdata);
            tfst.flen=tfst.fdata.length*8;
            tfst.startaddr=mReaderParams.filadr;
            tfst.isInvert=mReaderParams.filisinver;

            mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_FILTER, tfst);
        }

        if(mReaderParams.emdenable==1)
        {
            Reader.EmbededData_ST edst = mReader.new EmbededData_ST();

            edst.accesspwd=null;
            edst.bank=mReaderParams.emdbank;
            edst.startaddr=mReaderParams.emdadr;
            edst.bytecnt=mReaderParams.emdbytec;
            edst.accesspwd=null;

            er=mReader.ParamSet(Reader.Mtr_Param.MTR_PARAM_TAG_EMBEDEDDATA,
                    edst);
        }

        er=mReader.ParamSet
                (Reader.Mtr_Param.MTR_PARAM_TAGDATA_UNIQUEBYEMDDATA,
                        new int[]{mReaderParams.adataq});
        er=mReader.ParamSet
                (Reader.Mtr_Param.MTR_PARAM_TAGDATA_RECORDHIGHESTRSSI,
                        new int[]{mReaderParams.rhssi});
        er=mReader.ParamSet
                (Reader.Mtr_Param.MTR_PARAM_TAG_SEARCH_MODE,
                        new int[]{mReaderParams.invw});

        Reader.HardwareDetails val=mReader.new HardwareDetails();
        er=mReader.GetHardwareDetails(val);
        return er;
    }

    private boolean reconnect() {
        Log.i(TAG, "reconenct");
        boolean isPowerUp = mRpower.PowerUp();
        if(!isPowerUp) {
            Log.e(TAG,"RFID Power up failed!");
            return false;
        }

        Reader.READER_ERR er = mReader.InitReader_Notype(mDevPath, mAntPorts);
        if(er == Reader.READER_ERR.MT_OK_ERR) {
            mNeedreconnect = false;
        }

        er = configProtocol();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }

        er = configAntPower();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }

        er = configRegion();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }

        er = configOther();
        if(er != Reader.READER_ERR.MT_OK_ERR) {
            return false;
        }

        if(er == Reader.READER_ERR.MT_OK_ERR) {
            return true;
        } else {
            Toast.makeText(this,
                    ConstantUtil.Constr_sub1recfailed,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void TagsBufferResh(String key,Reader.TAGINFO tfs)
    {
        if (!mTagsMap.containsKey(key)) {
            mTagsMap.put(key, tfs);
        } else {
            Reader.TAGINFO tf = mTagsMap.get(key);
            tf.ReadCnt += tfs.ReadCnt;
            tf.RSSI = tfs.RSSI;
            tf.Frequency = tfs.Frequency;
            tf.AntennaID = tfs.AntennaID;
        }
    }
}
