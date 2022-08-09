package com.zcy.webexcel.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.vo.JsonResult;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    JsonResult exportImage(JSONObject jsonObject) throws UnsupportedEncodingException;

}
