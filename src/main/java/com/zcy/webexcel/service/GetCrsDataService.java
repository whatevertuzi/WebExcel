package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.pojo.CrsData;

public interface GetCrsDataService {
    CrsData getComplaint(String beginTime) throws Exception;
}
