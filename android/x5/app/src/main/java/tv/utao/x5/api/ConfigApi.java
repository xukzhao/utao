package tv.utao.x5.api;

import android.content.Context;
import android.os.Build;

import java.text.MessageFormat;
import java.util.HashMap;

import tv.utao.x5.MyApplication;
import tv.utao.x5.call.ConfigCallback;
import tv.utao.x5.domain.ConfigDTO;
import tv.utao.x5.util.AppVersionUtils;
import tv.utao.x5.util.HttpUtil;
import tv.utao.x5.util.JsonUtil;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.util.Util;
import tv.utao.x5.util.ValueUtil;

public class ConfigApi {
    public static final String  apiHost="http://api.vonchange.com";
    private static final  String updateUrl=apiHost+"/utao/config/update.json";
    public static ConfigDTO configDTO=null;
    private static  Long lastTime=System.currentTimeMillis();
    public static void  syncGetConfig(ConfigCallback configCallback){
        new Thread(()->{
            ConfigDTO configDTO1 = getConfig();
            configCallback.getConfig(configDTO1);
        }).start();
    }
    private static String firstUpX5Ok ="";
    public static void  syncIsX5Ok(Context context){
       String isX5Ok= ValueUtil.getString(context,"x5");
       //String firstUpX5Ok= ValueUtil.getString(context,"firstUpX5Ok");
       if(isX5Ok.equals("ok")&&"".equals(firstUpX5Ok)){
            new Thread(()->{
                String androidId= MyApplication.androidId;
                String reqUrl =updateUrl+"?isOk=1&id="+androidId;
                LogUtil.i("isOk getConfig","reqUrl "+reqUrl);
                HttpUtil.getJson(reqUrl,new HashMap<>());
                firstUpX5Ok="1";
               //ValueUtil.putString(context,"firstUpX5Ok","1");
            }).start();
       }
    }


    public static ConfigDTO getConfig(){
        if(null!=configDTO&&System.currentTimeMillis()-lastTime<1000*60*60*24){
            return configDTO;
        }
        lastTime=System.currentTimeMillis();
        String json;
        try {
             String androidId= MyApplication.androidId;
             String num="32";
             if(Util.is64()){
                 num="64";
             }
             int api = Build.VERSION.SDK_INT;
            String remark=  Build.MANUFACTURER+"_"+Build.MODEL+"_"+Build.VERSION.RELEASE+"_"+
                    AppVersionUtils.getVersionCode();
            String paramStr=   MessageFormat.format("?id={0}&num={1}&api={2}&remark={3}",androidId,num,api,remark);
            String reqUrl =updateUrl+paramStr;
            LogUtil.i("getConfig","reqUrl "+reqUrl);
             json = HttpUtil.getJson(reqUrl,new HashMap<>());
        }catch (Exception e){
            return  null;
        }
        LogUtil.i("getConfig","getConfig "+json);
        if(HttpUtil.isErrorResponse(json)){
            return null;
        }
        ConfigDTO result= JsonUtil.fromJson(json,ConfigDTO.class);
        configDTO=result;
        return result;
    }

}
