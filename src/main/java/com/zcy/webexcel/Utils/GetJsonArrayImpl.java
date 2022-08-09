package com.zcy.webexcel.Utils;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.Page;
import com.zcy.webexcel.DaoSys.pojo.varianceData;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GetJsonArrayImpl implements GetJsonArray{
    @Override
    public List<varianceData> getJson(int pageNum, String beginTime, String endTime ,String domain) throws Exception {
        String Url ="http://10.3.75.63:8085/action";
        HashMap<String,String> header = new HashMap<>();
        setHeader(header);
        JSONObject param = new JSONObject();
        JSONObject paraminner =new JSONObject();
        paraminner.put("startPage","0");
        paraminner.put("pageNum",pageNum);
        paraminner.put("domain",domain);
        paraminner.put("begin_time",beginTime);
        paraminner.put("end_time",endTime);
        param.put("action","/report/rest/callreport_hour/list");
        param.put("data",paraminner.toJSONString());
        param.put("sessionId","s95E2B128A732674E6EDE16648E2862C5-7");
        param.put("action_id","0.3437387635967428");
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,header,param.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<varianceData> dataList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            varianceData data = JSON.parseObject(String.valueOf(jsonObject), varianceData.class);
            if (Objects.equals(data.getActualCallLossRate(), "0.00%")){
                data.setFloatActualCallLossRate(Float.valueOf(0));
            }else {
                data.setFloatActualCallLossRate(Float.parseFloat(data.getActualCallLossRate().substring(0,4))*0.01F);
            }
            dataList.add(data);
        }
        return dataList;
    }

    private static void setHeader(HashMap<String, String> header) {
        header.put("Accept-Encoding","gzip, deflate");
        header.put("Accept-Language","zh-CN,zh;q=0.9");
        header.put("Connection","keep-alive");
        header.put("Content-Length","275");
        header.put("Content-Type","application/json; charset=UTF-8");
        header.put("Cookie","usrName=admin; tenantId=helpdesk.greentree.com; remeber=true");
        header.put("Host","10.3.75.63:8085");
        header.put("Origin","http://10.3.75.63:8085");
        header.put("Referer","http://10.3.75.63:8085/");
        header.put("User-Agent","Mozilla/5.0 (Windows_s3 NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        header.put("X-Requested-With","XMLHttpRequest");
    }

    @Override
    public int getPage(String beginTime,String endTime) throws Exception {
        String Url ="http://10.3.75.63:8085/action";
        JSONObject param = new JSONObject();
        JSONObject paraminner = new JSONObject();
        HashMap<String,String> header =new HashMap<>();
        header.put("Accept","*/*");
        setHeader(header);
        paraminner.put("domain","yuding.greentree.com");
        paraminner.put("begin_time",beginTime);
        paraminner.put("end_time",endTime);
        param.put("action","/report/rest/callreport_hour/querynum");
        param.put("data",paraminner.toJSONString());
        param.put("sessionId","s95E2B128A732674E6EDE16648E2862C5-7");
        param.put("action_id","0.3437387635967428");
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,header,param.toJSONString().getBytes()));
        Page page =JSON.parseObject(String.valueOf(JSON.parseObject(result.get("data").toString())),Page.class);
        return page.getNum();
    }
}
