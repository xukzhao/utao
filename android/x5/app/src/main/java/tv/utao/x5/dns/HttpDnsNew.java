package tv.utao.x5.dns;

import android.util.Log;

import tv.utao.x5.util.HttpRequest;
import tv.utao.x5.util.JsonUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Dns;

public class HttpDnsNew implements Dns {
    private static final String TAG="HttpDnsNew";
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        String body = HttpRequest.get("http://dns.alidns.com/resolve?name="+hostname).body();
        Log.i(TAG,body);
        Map<String,Object> map= JsonUtil.fromJson(body, Map.class);
        List<Map<String,Object>> list= (List<Map<String, Object>>) map.get("Answer");
        if(null==list||list.isEmpty()){
            return Dns.SYSTEM.lookup(hostname);
        }
        List<InetAddress> result = new ArrayList<>();
        for (Map<String, Object> item : list) {
            Object dataObj = item.get("data");
            if(null!=dataObj){
                Log.i(TAG,dataObj.toString());
                result.add(InetAddress.getByName(dataObj.toString()));
            }
        }
        if(!result.isEmpty()){
            return result;
        }
        return Dns.SYSTEM.lookup(hostname);
    }
}
