package com.zcy.webexcel.Utils;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class JsoupUtil {
    public static Document getDoc(String beginTime,String formatTime,String status,String typeName1,String typeName2) throws Exception {
        String Url = "http://52.131.229.157/Home/logIn?ReturnUrl=/";
        JSONObject Param = new JSONObject();
        Param.put("UserName","1002014080500050");
        Param.put("Password","zlQ024624");
        HashMap<String,String> header =new HashMap<>();
        setHeader(header);
        String Cookie= SimpleHttpUtils.sendRequest(Url,"POST",header,new ByteArrayInputStream(Param.toJSONString().getBytes()),null);
        return Jsoup.connect("http://52.131.229.157/Complaint/Index")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                .referrer("http://52.131.229.157/Complaint/Index")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7")
                .header("Connection", "keep-alive")
                .header("Content-Length", "1042")
                .data("status",status)
                .data("hotelgroup", "GL")
                .data("createtimeStart", beginTime)
                .data("createtimeEnd",formatTime)
                .data("requesttype1", typeName1)
                .data("requesttype1", typeName2)
                .data("requesttype", "'"+typeName1+"'"+","+"'"+typeName2+"'")
                .data("pageform_pager_select", "0")
                .data("pageform_pager_radio_10", "10")
                .data("PageIndex", "0")
                .data("PageSize", "10")
                .post();
    }
    public static Document getDoc(String beginTime,String formatTime,String status,String typeName1,String typeName2,String timeType) throws Exception {
        String Url = "http://52.131.229.157/Home/logIn?ReturnUrl=/";
        JSONObject Param = new JSONObject();
        Param.put("UserName","1002014080500050");
        Param.put("Password","zlQ024624");
        HashMap<String,String> header =new HashMap<>();
        setHeader(header);
        String Cookie= SimpleHttpUtils.sendRequest(Url,"POST",header,new ByteArrayInputStream(Param.toJSONString().getBytes()),null);
        return Jsoup.connect("http://52.131.229.157/Complaint/Index")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
                .referrer("http://52.131.229.157/Complaint/Index")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7")
                .header("Connection", "keep-alive")
                .header("Content-Length", "1042")
                .data("status",status)
                .data("hotelgroup", "GL")
                .data(timeType+"Start", beginTime)
                .data(timeType+"End", formatTime)
                .data("requesttype1", typeName1)
                .data("requesttype1", typeName2)
                .data("requesttype", "'"+typeName1+"'"+","+"'"+typeName2+"'")
                .data("pageform_pager_select", "0")
                .data("pageform_pager_radio_10", "10")
                .data("PageIndex", "0")
                .data("PageSize", "10")
                .post();
    }
    private static void setHeader(HashMap<String, String> header) {
        header.put("Accept-Encoding","gzip, deflate");
        header.put("Accept-Language","zh-CN,zh;q=0.9");
        header.put("Connection","keep-alive");
        header.put("Content-Length","275");
        header.put("Content-Type","application/json; charset=UTF-8");
        header.put("Cookie","JSESSIONID=EF80FDDA60AB59C6DA05B28BA4107CE8");
        header.put("Host","10.3.28.20:8899");
        header.put("User-Agent","Mozilla/5.0 (Windows_s3 NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        header.put("X-Requested-With","XMLHttpRequest");
    }
}
