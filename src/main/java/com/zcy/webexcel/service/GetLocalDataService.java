package com.zcy.webexcel.service;

import com.zcy.webexcel.pojo.LocalData.LocalDailyIt;

public interface GetLocalDataService {

    LocalDailyIt getLocalExcel(String beginTime) throws Exception;

}
