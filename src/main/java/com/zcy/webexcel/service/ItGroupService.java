package com.zcy.webexcel.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.DataParams;
import com.zcy.webexcel.DaoSys.pojo.HourData;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface ItGroupService {
    void getDaily(HttpServletResponse response, DataParams dataParams) throws Exception;
    void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception;
    void getReCallRecord(HttpServletResponse response, DataParams dataParams) throws Exception;
    JsonResult<List<HourData>> getDatas(String beginTime, String endTime) throws Exception;
    JsonResult<ResultCode> sendEmail(JSONObject jsonObject) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException;
}
