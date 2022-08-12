package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.pojo.CrsData;
import com.zcy.webexcel.DaoSys.pojo.LocalExcelIt;

public interface GetLocalDataService {

    LocalExcelIt getLocalExcel(String beginTime) throws Exception;
    CrsData getCrs(String beginTime) throws Exception;

}
