package com.zcy.webexcel.service.impl;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.Utils.SimpleHttpUtils;
import com.zcy.webexcel.pojo.LaiHuSys.*;
import com.zcy.webexcel.service.GetLaiHuDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class GetLaiHuDataServiceImpl implements GetLaiHuDataService {
    @Value("${dailyDataSource.url}")
    String Url;

    private HashMap<String,String> setHeader(HashMap<String, String> header) {
        header.put("Accept-Encoding","gzip, deflate");
        header.put("Accept-Language","zh-CN,zh;q=0.9");
        header.put("Connection","keep-alive");
        header.put("Content-Length","275");
        header.put("Content-Type","application/json; charset=UTF-8");
        header.put("Cookie","usrName=admin; tenantId=yuding.greentree.com; remeber=true");
        header.put("Host","10.3.75.63:8085");
        header.put("Origin","http://10.3.75.63:8085");
        header.put("Referer","http://10.3.75.63:8085/");
        header.put("User-Agent","Mozilla/5.0 (Windows_s3 NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        header.put("X-Requested-With","XMLHttpRequest");
        return header;
    }

    HashMap<String,String> HEADER =setHeader(new HashMap<>());

    @Override
    public Integer getPage(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject pageParam = new JSONObject();
        JSONObject pageParaminner = new JSONObject();
        pageParaminner.put("domain",domain);
        if (Objects.equals(action, "/report/rest/allrecord/querynum")){
            pageParaminner.put("startTime",beginTime);
            pageParaminner.put("hangupTime",endTime);
        } else if (Objects.equals(action, "/report/rest/skillreport_hour/querynum")) {
            pageParaminner.put("begin_time",beginTime);
            pageParaminner.put("end_time",endTime);
            pageParaminner.put("skillId","201");
        } else {
            pageParaminner.put("begin_time",beginTime);
            pageParaminner.put("end_time",endTime);
        }
        if (Objects.equals(action, "/report/rest/twoSkillreport_hour/querynum")) {
            pageParam.put("action","/report/rest/skillreport_hour/querynum");
        }else {
            pageParam.put("action",action);
        }
        pageParam.put("data",pageParaminner.toJSONString());
        pageParam.put("sessionId","s95E2B128A732674E6EDE16648E2862C5-");
        pageParam.put("action_id","0.3437387635967428");
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,pageParam.toJSONString().getBytes()));
        Page page = JSON.parseObject(String.valueOf(JSON.parseObject(result.get("data").toString())),Page.class);
        if (page.getNum()==0){
            System.out.println("没有查询到数据");
            return null;
        }
        return page.getNum();
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<HourData> getCallReportHour(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime,endTime,domain,"/report/rest/"+action+"/querynum"));
        dataParamInner.put("domain",domain);
        dataParamInner.put("begin_time",beginTime);
        dataParamInner.put("end_time",endTime);
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<HourData> datasList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            HourData datas = JSON.parseObject(String.valueOf(jsonObject), HourData.class);
            if (Objects.equals(datas.getActualCallLossRate(), "0.00%")){
                datas.setFloatActualCallLossRate((double) 0);
            }else {
                datas.setFloatActualCallLossRate(Double.parseDouble(datas.getActualCallLossRate().substring(0,4))*0.01);
            }
            datasList.add(datas);
        }
        return datasList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<DayData> getCallReportDay(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime.substring(0,10),endTime.substring(0,10),domain,"/report/rest/"+action+"/querynum"));
        dataParamInner.put("domain",domain);
        dataParamInner.put("begin_time",beginTime.substring(0,10));
        dataParamInner.put("end_time",endTime.substring(0,10));
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        List<DayData> dayDataList = ListUtils.newArrayList();
        int size = jsonArray.size();
        for (int i =size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DayData daydata = JSON.parseObject(String.valueOf(jsonObject), DayData.class);
            dayDataList.add(daydata);
        }
        return dayDataList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<Satisfy> getSatisfactionDay(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime,endTime,domain,"/report/rest/"+action+"/querynum")/2);
        dataParamInner.put("callType",2);
        dataParamInner.put("domain",domain);
        dataParamInner.put("begin_time",beginTime);
        dataParamInner.put("end_time",endTime);
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<Satisfy> satisifiesList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Satisfy satisify = JSON.parseObject(String.valueOf(jsonObject), Satisfy.class);
            satisify.setFloatTriggerRate(Double.parseDouble(satisify.getTriggerRate().substring(0,4)));
            satisify.setFloatPercentage(Double.parseDouble(satisify.getVerySatisfiedWithThePercentage().substring(0,5)));
            satisifiesList.add(satisify);
        }
        return satisifiesList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<CallRecord> getCallRecord(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime,endTime,domain,"/report/rest/"+action+"/querynum"));
        dataParamInner.put("domain",domain);
        dataParamInner.put("startTime",beginTime);
        dataParamInner.put("hangupTime",endTime);
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<CallRecord> callRecordsList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CallRecord callRecord = JSON.parseObject(String.valueOf(jsonObject), CallRecord.class);
            callRecordsList.add(callRecord);
        }
        return callRecordsList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<AgentReport> getAgentReport(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParaminner =new JSONObject();
        dataParaminner.put("startPage","0");
        dataParaminner.put("pageNum",25);
        dataParaminner.put("domain",domain);
        dataParaminner.put("begin_time",beginTime);
        dataParaminner.put("end_time",endTime);
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParaminner.toJSONString());
        JSONObject resultj =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(resultj.get("data").toString());
        int size = jsonArray.size();
        List<AgentReport> agentReportList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            AgentReport agentReport = JSON.parseObject(String.valueOf(jsonObject), AgentReport.class);
            agentReportList.add(agentReport);
        }
        return agentReportList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<AgentReportDay> getAgentReportDay(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime,endTime,domain,"/report/rest/"+action+"/querynum"));
        dataParamInner.put("domain",domain);
        dataParamInner.put("begin_time",beginTime);
        dataParamInner.put("end_time",beginTime);
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<AgentReportDay> agentReportDayList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            AgentReportDay agentReportDay = JSON.parseObject(String.valueOf(jsonObject), AgentReportDay.class);
            agentReportDayList.add(agentReportDay);
        }
        return agentReportDayList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<AgentSatisfy> getSatisfactionStatistics(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        if (domain.equals("helpdesk.greentree.com")){
            dataParamInner.put("domain",domain);
            dataParamInner.put("begin_time",beginTime);
            dataParamInner.put("end_time",endTime);
        }else {
            dataParamInner.put("domain",domain);
            dataParamInner.put("begin_time",beginTime);
            dataParamInner.put("end_time",endTime);
            dataParamInner.put("skillId","201");
            dataParamInner.put("callType","2");
        }
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<AgentSatisfy> agentSatisfyList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            AgentSatisfy agentSatisfy = JSON.parseObject(String.valueOf(jsonObject), AgentSatisfy.class);
            agentSatisfy.setFloatVerySatisfiedWithThePercentage(Double.parseDouble(agentSatisfy.getVerySatisfiedWithThePercentage().substring(0,4)));
            agentSatisfy.setFloatNotSatisfy(Double.parseDouble(agentSatisfy.getNotSatisfiedPercentage().substring(0,4)));
            agentSatisfyList.add(agentSatisfy);
        }
        return agentSatisfyList;
    }

    /**
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param domain 域名
     * @param action 接口
     * @return 返回分时数据类 HourData
     * @throws Exception 异常
     */
    @Override
    public List<SkillHourData> getSkillReportHour(String beginTime, String endTime, String domain, String action) throws Exception {
        JSONObject dataParam = new JSONObject();
        JSONObject dataParamInner =new JSONObject();
        dataParamInner.put("startPage","0");
        dataParamInner.put("pageNum",getPage(beginTime,endTime,domain,"/report/rest/skillreport_hour/querynum"));
        dataParamInner.put("domain",domain);
        dataParamInner.put("begin_time",beginTime);
        dataParamInner.put("end_time",endTime);
        dataParamInner.put("skillId","203");
        dataParam.put("action","/report/rest/"+action+"/list");
        dataParam.put("data",dataParamInner.toJSONString());
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,HEADER,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(result.get("data").toString());
        int size = jsonArray.size();
        List<SkillHourData> skillHourDataArrayList = ListUtils.newArrayList();
        for (int i = size-1; i >=0; i--) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            SkillHourData skillHourData = JSON.parseObject(String.valueOf(jsonObject), SkillHourData.class);
            skillHourDataArrayList.add(skillHourData);
        }
        return skillHourDataArrayList;
    }

}
