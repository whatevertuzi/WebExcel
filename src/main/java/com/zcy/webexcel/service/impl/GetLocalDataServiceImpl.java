package com.zcy.webexcel.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.CrsData;
import com.zcy.webexcel.DaoSys.pojo.LocalExcelIt;
import com.zcy.webexcel.Utils.SimpleHttpUtils;
import com.zcy.webexcel.service.GetLocalDataService;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class GetLocalDataServiceImpl implements GetLocalDataService {
    @Override
    public LocalExcelIt getLocalExcel(String beginTime) throws Exception {
        String Url = "http://10.3.28.20:8899/getexcel";
        JSONObject Param = new JSONObject();
        Param.put("beginTime", beginTime);
        HashMap<String,String> header =new HashMap<>();
        setHeader(header);
        JSONObject result = JSONObject.parseObject(SimpleHttpUtils.post(Url, header, Param.toJSONString().getBytes()));
        return JSON.parseObject(String.valueOf(JSON.parseObject(result.get("data").toString())), LocalExcelIt.class);
    }

    @Override
    public CrsData getCrs(String beginTime) throws Exception {
        String Url = "http://10.3.28.20:8899/getcrs";
        JSONObject Param = new JSONObject();
        Param.put("beginTime", beginTime);
        HashMap<String,String> header =new HashMap<>();
        setHeader(header);
        JSONObject result = JSONObject.parseObject(SimpleHttpUtils.post(Url, header, Param.toJSONString().getBytes()));
        return JSON.parseObject(String.valueOf(JSON.parseObject(result.get("data").toString())), CrsData.class);
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
