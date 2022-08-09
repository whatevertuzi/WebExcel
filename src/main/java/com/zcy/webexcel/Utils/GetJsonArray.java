package com.zcy.webexcel.Utils;

import com.zcy.webexcel.DaoSys.pojo.varianceData;

import java.util.List;


public interface GetJsonArray {
    List<varianceData> getJson(int pageNum , String beginTime , String endTime, String doMain) throws Exception;
    int getPage(String beginTime,String endTime) throws Exception;
}
