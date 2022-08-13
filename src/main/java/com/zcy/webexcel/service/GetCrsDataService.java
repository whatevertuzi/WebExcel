package com.zcy.webexcel.service;

import com.zcy.webexcel.vo.CrsData;

public interface GetCrsDataService {
    CrsData getComplaint(String beginTime) throws Exception;
}
