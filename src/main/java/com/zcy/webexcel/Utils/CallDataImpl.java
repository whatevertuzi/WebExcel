package com.zcy.webexcel.Utils;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CallDataImpl implements CallData {
    private static final Logger LOGGER = LogManager.getLogger(CallDataImpl.class);

    private static void setHeader(HashMap<String, String> header, String domain) {
        header.put("Accept-Encoding","gzip, deflate");
        header.put("Accept-Language","zh-CN,zh;q=0.9");
        header.put("Connection","keep-alive");
        header.put("Content-Length","275");
        header.put("Content-Type","application/json; charset=UTF-8");
        header.put("Cookie","usrName=admi; tenantId="+domain+"; remeber=true");
        header.put("Host","10.3.75.63:8085");
        header.put("Origin","http://10.3.75.63:8085");
        header.put("Referer","http://10.3.75.63:8085/");
        header.put("User-Agent","Mozilla/5.0 (Windows_s3 NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36");
        header.put("X-Requested-With","XMLHttpRequest");
    }

    @Override
    public Integer getPage(String beginTime, String endTime, String domain, String action) throws Exception {
        String Url ="http://10.3.75.63:8085/action";
        JSONObject pageParam = new JSONObject();
        JSONObject pageParaminner = new JSONObject();
        HashMap<String,String> header =new HashMap<>();
        setHeader(header,domain);
        pageParaminner.put("domain",domain);
        if (action=="/report/rest/allrecord/querynum"){
            pageParaminner.put("startTime",beginTime);
//            pageParaminner.put("callId","");
//            pageParaminner.put("caller","");
//            pageParaminner.put("callee","");
//            pageParaminner.put("callType","");
//            pageParaminner.put("isAnswer","");
//            pageParaminner.put("agentId","");
//            pageParaminner.put("skillId","");
//            pageParaminner.put("skillName","");
            pageParaminner.put("hangupTime",endTime);
        } else if (action=="/report/rest/skillreport_hour/querynum") {
            pageParaminner.put("begin_time",beginTime);
            pageParaminner.put("end_time",endTime);
            pageParaminner.put("skillId","201");
        } else {
            pageParaminner.put("begin_time",beginTime);
            pageParaminner.put("end_time",endTime);
        }
        if (action=="/report/rest/twoSkillreport_hour/querynum") {
            pageParam.put("action","/report/rest/skillreport_hour/querynum");
        }else {
            pageParam.put("action",action);
        }
        pageParam.put("data",pageParaminner.toJSONString());
        pageParam.put("sessionId","s95E2B128A732674E6EDE16648E2862C5-");
        pageParam.put("action_id","0.3437387635967428");
        JSONObject result =JSONObject.parseObject(SimpleHttpUtils.post(Url,header,pageParam.toJSONString().getBytes()));
        Page page = JSON.parseObject(String.valueOf(JSON.parseObject(result.get("data").toString())),Page.class);
        if (page.getNum()==0){
            System.out.println("没有查询到数据");
            return null;
        }
        return page.getNum();
    }

    @Override
    public  List  getReport(String beginTime, String endTime , String domain , String action ) throws Exception {

        String user;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")){
            user = "管理员或未登录人员";
        }else {
            user=((LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUsername();
        }

        CallDataImpl callData = new CallDataImpl();
        String Url ="http://10.3.75.63:8085/action";
        HashMap<String,String> header =new HashMap<>();
        setHeader(header,domain);
        JSONObject dataParam = new JSONObject();
        JSONObject dataParaminner =new JSONObject();
        dataParaminner.put("startPage","0");

        if (Objects.equals(action, "callreport_day")){
            dataParaminner.put("pageNum",callData.getPage(beginTime.substring(0,10),endTime.substring(0,10),domain,"/report/rest/callreport_day/querynum"));
        } else if (Objects.equals(action, "callreport_hour")) {
            dataParaminner.put("pageNum",callData.getPage(beginTime,endTime,domain,"/report/rest/callreport_hour/querynum"));
        } else if (Objects.equals(action, "satisfaction_day")) {
            dataParaminner.put("pageNum",callData.getPage(beginTime,endTime,domain,"/report/rest/satisfaction_day/querynum")/2);
            dataParaminner.put("callType",2);
        } else if (Objects.equals(action, "callrecord")) {
            dataParaminner.put("pageNum",callData.getPage(beginTime,endTime,domain,"/report/rest/callrecord/querynum"));
        } else if (Objects.equals(action, "agentreport")) {
            dataParaminner.put("pageNum",25);
        } else if (Objects.equals(action,"agentreport_day")) {
            dataParaminner.put("pageNum",callData.getPage(beginTime,endTime,domain,"/report/rest/agentreport_day/querynum"));
        }else if (Objects.equals(action, "skillreport_hour")){
            dataParaminner.put("pageNum",callData.getPage(beginTime,endTime,domain,"/report/rest/skillreport_hour/querynum"));
        }

        if (Objects.equals(action, "callreport_day")){ //如果访问呼叫日数据接口则需要截取开始日期和结束日期只保留年月日
            dataParaminner.put("domain",domain);
            dataParaminner.put("begin_time",beginTime.substring(0,10));
            dataParaminner.put("end_time",endTime.substring(0,10));
        } else if (Objects.equals(action, "callrecord")) {
            dataParaminner.put("domain",domain);
            dataParaminner.put("startTime",beginTime);
            dataParaminner.put("hangupTime",endTime);
//            dataParaminner.put("callId","");
//            dataParaminner.put("caller","");
//            dataParaminner.put("callee","");
//            dataParaminner.put("callType","");
//            dataParaminner.put("isAnswer","");
//            dataParaminner.put("agentId","");
//            dataParaminner.put("skillId","");
//            dataParaminner.put("skillName","");
        } else if (Objects.equals(action, "agentreport_day")){
            dataParaminner.put("domain",domain);
            dataParaminner.put("begin_time",beginTime);
            dataParaminner.put("end_time",beginTime);
        } else if(Objects.equals(action, "skillreport_hour")){
            dataParaminner.put("domain",domain);
            dataParaminner.put("begin_time",beginTime);
            dataParaminner.put("end_time",endTime);
            dataParaminner.put("skillId","203");
        } else if (Objects.equals(action, "satisfactionStatistics")){
            if (domain.equals("helpdesk.greentree.com")){
                dataParaminner.put("domain",domain);
                dataParaminner.put("begin_time",beginTime);
                dataParaminner.put("end_time",endTime);
            }else {
                dataParaminner.put("domain",domain);
                dataParaminner.put("begin_time",beginTime);
                dataParaminner.put("end_time",endTime);
                dataParaminner.put("skillId","201");
                dataParaminner.put("callType","2");
            }
        } else {
            dataParaminner.put("domain",domain);
            dataParaminner.put("begin_time",beginTime);
            dataParaminner.put("end_time",endTime);
        }
        if (action=="twoSkillreport_hour"){
            dataParam.put("action","/report/rest/"+"skillreport_hour"+"/list");
        }else {
            dataParam.put("action","/report/rest/"+action+"/list");
        }
        dataParam.put("data",dataParaminner.toJSONString());
        dataParam.put("sessionId","s95E2B128A732674E6EDE16648E2862C5-7");
        dataParam.put("action_id","0.3437387635967428");
        JSONObject resultj =JSONObject.parseObject(SimpleHttpUtils.post(Url,header,dataParam.toJSONString().getBytes(StandardCharsets.UTF_8)));
        JSONArray jsonArray = JSON.parseArray(resultj.get("data").toString());
        int size = jsonArray.size();
        if (action=="callreport_hour"){
            List<HourData> datasList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HourData datas = JSON.parseObject(String.valueOf(jsonObject), HourData.class);
                if (Objects.equals(datas.getActualCallLossRate(), "0.00%")){
                    datas.setFloatActualCallLossRate(null);
                }else {
                    datas.setFloatActualCallLossRate(Double.parseDouble(datas.getActualCallLossRate().substring(0,4))*0.01);
                }
                datasList.add(datas);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的分时数据");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的分时数据");
            }
            return datasList;
        } else if (action=="callreport_day") {
            List<DayData> dayDataList = ListUtils.newArrayList();
            for (int i =size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DayData daydata = JSON.parseObject(String.valueOf(jsonObject), DayData.class);
                dayDataList.add(daydata);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的单日数据");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的单日数据");
            }
            return dayDataList;
        } else if (action=="satisfaction_day") {
            List<Satisfy> satisifiesList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Satisfy satisify = JSON.parseObject(String.valueOf(jsonObject), Satisfy.class);
                satisify.setFloatTriggerRate(Double.parseDouble(satisify.getTriggerRate().substring(0,4)));
                satisify.setFloatPercentage(Double.parseDouble(satisify.getVerySatisfiedWithThePercentage().substring(0,5)));
                satisifiesList.add(satisify);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的满意度数据");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的满意度数据");
            }
            return satisifiesList;
        } else if (action=="callrecord") {
            List<CallRecord> callRecordsList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CallRecord callRecord = JSON.parseObject(String.valueOf(jsonObject), CallRecord.class);
                callRecordsList.add(callRecord);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的通话记录数据");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的通话记录数据");
            }
            return callRecordsList;
        } else if (action == "agentreport") {
            List<AgentReport> agentReportList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AgentReport agentReport = JSON.parseObject(String.valueOf(jsonObject), AgentReport.class);
                agentReportList.add(agentReport);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的坐席统计报表");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的坐席统计报表");
            }
            return agentReportList;
        } else if (action == "agentreport_day") {
            List<AgentReportDay> agentReportDayList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AgentReportDay agentReportDay = JSON.parseObject(String.valueOf(jsonObject), AgentReportDay.class);
                agentReportDayList.add(agentReportDay);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的坐席统计报表");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的坐席统计报表");
            }
            return agentReportDayList;
        } else if (action == "satisfactionStatistics") {
            List<AgentSatisfy> agentSatisfyList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AgentSatisfy agentSatisfy = JSON.parseObject(String.valueOf(jsonObject), AgentSatisfy.class);
                agentSatisfy.setFloatVerySatisfiedWithThePercentage(Double.parseDouble(agentSatisfy.getVerySatisfiedWithThePercentage().substring(0,4)));
                agentSatisfy.setFloatNotSatisfy(Double.parseDouble(agentSatisfy.getNotSatisfiedPercentage().substring(0,4)));
                agentSatisfyList.add(agentSatisfy);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的坐席满意度统计报表");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的坐席满意度统计报表");
            }
            return agentSatisfyList;
        } else if (action == "skillreport_hour") {
            List<SkillHourData> skillHourDataArrayList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SkillHourData skillHourData = JSON.parseObject(String.valueOf(jsonObject), SkillHourData.class);
                skillHourDataArrayList.add(skillHourData);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的技能组分时统计报表");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的技能组分时统计报表");
            }
            return skillHourDataArrayList;
        }else if (action == "twoSkillreport_hour") {
            List<SkillHourData> skillHourDataArrayList = ListUtils.newArrayList();
            for (int i = size-1; i >=0; i--) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SkillHourData skillHourData = JSON.parseObject(String.valueOf(jsonObject), SkillHourData.class);
                skillHourDataArrayList.add(skillHourData);
            }
            if (Objects.equals(domain,"yuding.greentree.com")){
                LOGGER.info(user+"查询了预订部日期为"+beginTime.substring(0,10)+"的技能组分时统计报表");
            } else if (Objects.equals(domain, "helpdesk.greentree.com")) {
                LOGGER.info(user+"查询了实施组日期为"+beginTime.substring(0,10)+"的技能组分时统计报表");
            }
            return skillHourDataArrayList;
        }

        return null;

    }

}
