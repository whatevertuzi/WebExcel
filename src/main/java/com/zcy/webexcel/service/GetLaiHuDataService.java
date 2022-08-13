package com.zcy.webexcel.service;

import com.zcy.webexcel.pojo.LaiHuSys.*;

import java.util.List;


//来虎接口
public interface GetLaiHuDataService {

    Integer getPage(String beginTime, String endTime, String domain, String action) throws Exception;
    List<HourData> getCallReportHour(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<DayData> getCallReportDay(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<Satisfy> getSatisfactionDay(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<CallRecord> getCallRecord(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<AgentReport> getAgentReport(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<AgentReportDay> getAgentReportDay(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<AgentSatisfy> getSatisfactionStatistics(String beginTime, String endTime , String domain , String action ) throws Exception;
    List<SkillHourData> getSkillReportHour(String beginTime, String endTime , String domain , String action ) throws Exception;

}
