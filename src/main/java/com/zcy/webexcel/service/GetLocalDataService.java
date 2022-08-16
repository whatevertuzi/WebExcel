package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.pojo.LocalData;

public interface GetLocalDataService {

    LocalData getDay(String beginTime) throws Exception;
    LocalData getMonth(String beginTime) throws Exception;
    LocalData getCond(String beginTime,String endTime) throws Exception;

}
