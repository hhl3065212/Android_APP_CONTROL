package com.haier.tft.wifimodule.moduletool;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import common.StringConv;

/**
 * Created by Administrator on 2015/10/10.
 */
public class ControlPrefence {

    private  volatile  boolean IsSocketLive;
    private volatile  boolean IsOnline;
    private byte[] CmdFromServer;
    private long HeartTime;
    private   String num="0";
    private volatile boolean isAnswer;
    private volatile byte[] SN= new byte[4];
    private  volatile byte[] GoodFoodModel=new byte[2];
    private  volatile ConcurrentHashMap<String,byte[]> MessHashMap  =new ConcurrentHashMap<String,byte[]>();
    private static ControlPrefence mInstance;
    private volatile boolean continueFlag;

     protected  ControlPrefence(){

         IsSocketLive=false;
         SharedPreferences mySharedPreferences = getSharedPreferences();
         if(mySharedPreferences!=null){
             SharedPreferences.Editor myEditor = mySharedPreferences.edit();
             myEditor.putBoolean("IsSocketLive",false);
             myEditor.commit();

         }
         IsOnline=false;
         continueFlag=true;
         isAnswer =false;
         HeartTime=0;
     }

    private static synchronized void syncInit() {
              if (mInstance == null) {
                  mInstance = new ControlPrefence();
                  }
            }



    public  static  ControlPrefence  getInstance(){

             if(mInstance==null){
                       syncInit();
                 }

    return mInstance;


}

    public synchronized void setContinueFlag(boolean flag){
        continueFlag =flag;
    }

    public  synchronized boolean getContinueFlag(){
        return continueFlag;
    }

    public synchronized SharedPreferences getSharedPreferences(){
        if(StaticValueAndConnectUrl.mySharedPreferences!=null){
            return   StaticValueAndConnectUrl.mySharedPreferences;
        }
        return null;
    }

    public synchronized void setIsSocketLive(boolean flag){
        SharedPreferences mySharedPreferences = getSharedPreferences();
        if(mySharedPreferences!=null){
            SharedPreferences.Editor myEditor = mySharedPreferences.edit();
            myEditor.putBoolean("IsSocketLive",flag);
            myEditor.commit();

        }



    }

    public synchronized boolean getIsSocketLive() {
        SharedPreferences mySharedPreferences = getSharedPreferences();
        if (mySharedPreferences != null) {

            boolean flag = mySharedPreferences.getBoolean("IsSocketLive", false);

            return flag;
        }

          return false;
    }

    public void setHeartTime(long time){
        HeartTime=time;
    }
    public long getHeartTime(){

        return HeartTime;

    }

    public synchronized void setIsOnline(boolean flag){

        this.IsOnline = flag;


    }

    public synchronized boolean getIsOnline(){

        return IsOnline;
    }

    public void setTypeid(String typeid) {

        SharedPreferences mySharedPreferences = getSharedPreferences();
        if (mySharedPreferences != null) {
            SharedPreferences.Editor myEditor = mySharedPreferences.edit();
            myEditor.putString("typeid", typeid);
            myEditor.commit();

        }
    }
    public String getTypeid(){
            String typeid="0";
            SharedPreferences mySharedPreferences = getSharedPreferences();
            if (mySharedPreferences != null) {
                typeid = mySharedPreferences.getString("typeid", "0");
            }
             return typeid;



    }



    /**
     * 用于确定是否应答，应答的话是02，不是应答的话是06，但是除了查询，如果查询的话属于06
     */
public synchronized void  setIsAnswer(boolean flag ){
                  this.isAnswer=flag;
}

public synchronized boolean getIsAnswer(){

return isAnswer;


}

//    public void setCmdFromServer(byte[] com){
//
//        CmdFromServer=com;
//
//    }
//
//    public byte[] getCmdFromServer(){
//
//        return CmdFromServer;
//    }

    public void setGoodFoodModel(byte[] modle){

        if(modle==null){
            GoodFoodModel[0]= (byte) 0xFE;
            GoodFoodModel[1]= (byte) 0xFE;
        }else{

            GoodFoodModel[0]= modle[0];
            GoodFoodModel[1]= modle[1];

        }

    }
public byte[] getGoodFoodModel(){

    return GoodFoodModel;

}
    public void setSN(byte[] sn){
       if(sn==null){
           SN[0]=0;
           SN[1]=0;
           SN[2]=0;
           SN[3]=0;
       }else {
           SN[0]=sn[0];
           SN[1]=sn[1];
           SN[2]=sn[2];
           SN[3]=sn[3];
       }

    }

   public byte[] getSN(){


        return SN;
    }


    public   int getMessageSize(){

       int size = MessHashMap.size();


             return size;
    }

   public synchronized List<byte[]> getMessageSNList(){

       List<byte[]> mylist = new ArrayList<byte[]>();
       List<String> mylistForKey = new ArrayList<String>();

     Set<String> mapSet = MessHashMap.keySet();

       Iterator<String> itor =  mapSet.iterator();//获取key的Iterator便利
        	while(itor.hasNext()) {//存在下一个值
                String key = itor.next();//当前key值
                mylist.add(MessHashMap.get(key))   ;
                mylistForKey.add(key);
            }
       if(mylistForKey!=null){
           for(int i=0;i<mylistForKey.size();i++){
               MessHashMap.remove(mylistForKey.get(i));
           }
       }


       return mylist;
   }


    public  void  deleteMessage(String mSN){

        MessHashMap.remove(mSN);

    }
    public synchronized void  addMessageForSN(byte[] mSN){

       int IntNum= StringConv.parseInt(num);
        IntNum++;
        if(IntNum>15){
            IntNum=0;

        }
        num =""+IntNum;
        BytesToInt mByteToInt = new BytesToInt();
        Log.i("answer","num = "+num +"and SN = "+mByteToInt.bytesToHexString(mSN));
        MessHashMap.put(num,mSN);


    }








}
