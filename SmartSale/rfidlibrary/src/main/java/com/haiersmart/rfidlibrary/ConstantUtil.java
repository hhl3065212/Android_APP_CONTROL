package com.haiersmart.rfidlibrary;

/**
 * Created by tingting on 2017/12/22.
 */

public class ConstantUtil {
    public static final String DOOR_STATE_BROADCAST = "com.haiersmart.smartsale.doorservice";
    public static final String DOOR_STATE = "state";

    public final static String Constr_READ = "读";
    public final static String Constr_CONNECT = "连接";
    public final static String Constr_INVENTORY = "盘点";
    public final static String Constr_RWLOP = "读写锁";
    public final static String Constr_set = "设置";
    public final static String Constr_SetFaill = "设置失败：";
    public final static String Constr_GetFaill = "获取失败：";
    public final static String Constr_SetOk="设置成功";
    public final static String Constr_unsupport="不支持";
    public final static String Constr_Putandexit = "再按一次退出程序";
    public final static String[] Coname = new String[] { "序号", "EPC ID", "次数", "天线",
            "协议", "RSSI", "频率", "附加数据 " };
    public final static String Constr_stopscan = "请先停止扫描";
    public final static String Constr_hadconnected = "已经连接";
    public final static String Constr_plsetuuid = "请设置好UUID:";
    public final static String Constr_pwderror = "密码错误";
    public final static String Constr_search = "搜索";
    public final static String Constr_stop = "停止";

    public final static String Constr_createreaderok = "读写器创建失败";
    public final static String[] pdaatpot = { "一天线", "双天线", "三天线", "四天线","16天线" };

    public final static String[] spibank={"保留区","EPC区","TID区","用户区"};
    public final static String[] spifbank={"EPC区","TID区","用户区"};
    public final static String[] spilockbank={"访问密码","销毁密码","EPCbank","TIDbank","USERbank"};
    public final static String[] spilocktype={"解锁定","暂时锁定","永久锁定"};
    public final static String Constr_sub3readmem = "读标签";
    public final static String Constr_sub3writemem = "写标签";
    public final static String Constr_sub3lockkill = "锁与销毁";
    public final static String Constr_sub3readfail = "读失败:";
    public final static String Constr_sub3nodata = "无数据";
    public final static String Constr_sub3wrtieok = "写成功";
    public final static String Constr_sub3writefail = "写失败:";
    public final static String Constr_sub3lockok = "锁成功";
    public final static String Constr_sub3lockfail = "锁失败:";
    public final static String Constr_sub3killok = "销毁成功";
    public final static String Constr_sub3killfial = "销毁失败:";

    //String[] spireg={"中国","北美","日本","韩国","欧洲","印度","加拿大","全频段"
    //		,"中国2"};
    public static	String[] spireg = { "中国", "北美", "日本", "韩国", "欧洲", "欧洲2", "欧洲3", "印度",
            "加拿大", "全频段", "中国2" };
    public static String[] spinvmo={"普通模式","高速模式"};
    public static String[] spitari={"25微秒","12.5微秒","6.25微秒"};
    public static  String[] spiwmod={"字写","块写"};
    public static String Auto="自动";
    public static String No="无";
    public static String Constr_sub4invenpra="盘点参数";
    public static String Constr_sub4antpow = "天线功率";
    public static String Constr_sub4regionfre = "区域频率";
    public static String Constr_sub4gen2opt = "Gen2项";
    public static String Constr_sub4invenfil = "盘点过滤";
    public static String Constr_sub4addidata = "附加数据";
    public static String Constr_sub4others = "其他参数";
    public static String Constr_sub4quickly = "快速模式";
    public static String Constr_sub4setmodefail = "配置模式失败";
    public static String Constr_sub4setokresettoab = "设置成功，重启读写器生效";
    public static String Constr_sub4ndsapow = "该设备需要功率一致";
    public static String Constr_sub4unspreg = "不支持的区域";

    public static String[] spiregbs = { "北美", "中国", "欧频", "中国2" };
    public static String Constr_subblmode = "模式";
    public static String Constr_subblinven = "盘点";
    public static String Constr_subblfil = "过滤";
    public static String Constr_subblfre = "频率";
    public static String Constr_subblnofre = "没有选择频点";

    public static String[] cusreadwrite={"读操作","写操作"};
    public static String[] cuslockunlock={"锁","解锁"};

    public static String Constr_subcsalterpwd="改密码";
    public static String Constr_subcslockwpwd="带密码锁";
    public static String Constr_subcslockwoutpwd="不带密码锁";
    public static String Constr_subcsplsetimeou="请设置超时时间";
    public static String Constr_subcsputcnpwd="填入当前密码与新密码";
    public static String Constr_subcsplselreg="请选择区域";
    public static String Constr_subcsopfail="操作失败:";
    public static String Constr_subcsputcurpwd="填入当前密码";

    public static String Constr_subdbdisconnreconn = "已经断开,正在重新连接";
    public static String Constr_subdbhadconnected = "已经连接";
    public static String Constr_subdbconnecting = "正在连接......";
    public static String Constr_subdbrev = "接收";
    public static String Constr_subdbstop = "停止";
    public static String Constr_subdbdalennot = "数据长度不对";
    public static String Constr_subdbplpuhexchar = "请输入16进制字符";

    public static String Constr_subsysaveok = "保存成功";
    public static String Constr_subsysout = "输入txt或者csv";
    public static String Constr_subsysreavaid = "重新连接生效";
    public static String Constr_sub1recfailed = "重新连接失败";
    public static String Constr_subsysavefailed = "保存失败";
    public static String Constr_subsysexefin ="执行完毕";
    public static String Constr_sub1adrno="地址没有输入";
    public static String Constr_sub1pdtsl="请选择平台";
    public static String Constr_mainpu="上电：";
    public static String Constr_nostopstreadfailed="不停顿盘点启动失败";
    public static String Constr_nostopspreadfailed="不停顿盘点停止失败";
    public static String Constr_nostopreadfailed="开始盘点失败：";
    public static String Constr_connectok="连接成功";
    public static String Constr_connectfialed="连接失败";
    public static String Constr_disconpowdown="断开读写器，下电：";
    public static String Constr_ok="成功:";
    public static String Constr_failed="失败:";
    public static String Constr_excep="异常:";
    public static String Constr_setcep="设置异常:";
    public static String Constr_getcep="获取异常:";
    public static String Constr_killok="KILL成功";
    public static String Constr_killfailed="KILL失败";
    public static String Constr_psiant="请选择盘点天线";
    public static String Constr_selpro="请选择协议";
    public static String Constr_setpwd="设置功率:";
}
