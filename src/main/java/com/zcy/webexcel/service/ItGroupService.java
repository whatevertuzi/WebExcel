package com.zcy.webexcel.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.pojo.DailyData;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.pojo.LaiHuSys.HourData;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ItGroupService {
    void getDaily(HttpServletResponse response, DataParams dataParams) throws Exception;

    void getDaily(String beginTime, String endTime) throws Exception;

    DailyData getDailyData(String beginTime, String endTime) throws Exception;

    void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception;
    void getReCallRecord(HttpServletResponse response, DataParams dataParams) throws Exception;
    JsonResult<List<HourData>> getDatas(String beginTime, String endTime) throws Exception;
    JsonResult<ResultCode> sendEmail(String beginTime,byte[] bytes) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException;
    void getRecordData(HttpServletResponse response, DataParams dataParams) throws Exception;
}
