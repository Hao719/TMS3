package com.hao.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Map;

@Component
public class BaiduMapUtils {

    private static final String AK="3gkzOMwNeGwod8DSf2aWG08Dhe1XHvxc";

    /**
     * 调用百度地图驾车路线规划服务接口，根据寄件人地址和收件人地址坐标计算订单距离
     * @param origin
     * @param destination
     * @return
     */
    public static Double getDistance(String origin,String destination){
        String httpUrl = "http://api.map.baidu.com/directionlite/v1/driving?origin="
                +origin+"&destination="
                +destination+"&ak=" + AK;

        Map map = JSON.parseObject(loadJSON(httpUrl), Map.class);
        if ("0".equals(map.get("status").toString())) {
            Map result = (Map) map.get("result");
            JSONArray jsonArray = (JSONArray) result.get("routes");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            double distance = Double.parseDouble(jsonObject.get("distance") == null ? "0" : jsonObject.get("distance").toString());
            return distance;
        }
        return null;
    }

    /**
     * 调用百度地图驾车路线规划服务接口，根据寄件人地址和收件人地址坐标计算订单时间
     * @param origin
     * @param destination
     * @return
     */
    public static Double getTime(String origin,String destination){
        String httpUrl = "http://api.map.baidu.com/directionlite/v1/driving?origin="
                +origin+"&destination="
                +destination+"&ak=" + AK;

        Map map = JSON.parseObject(loadJSON(httpUrl), Map.class);
        if ("0".equals(map.get("status").toString())) {
            Map childMap = (Map) map.get("result");
            JSONArray jsonArray = (JSONArray) childMap.get("routes");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            double time = Double.parseDouble(jsonObject.get("duration") == null ? "0" : jsonObject.get("duration").toString());
            return time;
        }

        return null;
    }

    /**
     * 调用百度地图地理编码服务接口，根据地址获取坐标（经度、纬度）
     * @param address
     * @return
     */
    public static String getCoordinate(String address){
        String httpUrl = "http://api.map.baidu.com/geocoding/v3/?address="+address+"&output=json&ak=" + AK;
        Map map = JSON.parseObject(loadJSON(httpUrl), Map.class);
        if("0".equals(map.get("status").toString())){
            Map result = (Map) map.get("result");
            Map location = (Map) result.get("location");
            DecimalFormat df = new DecimalFormat("#.######");
            //经度
            String lng = df.format(Double.parseDouble(location.get("lng").toString()));
            //纬度
            String lat = df.format(Double.parseDouble(location.get("lat").toString()));
            return lat + "," + lng;
        }
        return null;
    }


    /**
     * 调用服务接口，返回百度地图服务端的结果
     * @param httpUrl
     * @return
     */
    public static String loadJSON(String httpUrl){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(httpUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
