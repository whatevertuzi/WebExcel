package com.zcy.webexcel.service;

import com.zcy.webexcel.Utils.CallData;
import com.zcy.webexcel.DaoSys.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataServiceImpl implements DataService {
    @Autowired
    CallData callData;
    @Override
    public Result getData(String beginTime, String endTime, String domain) throws Exception {
        return Result.success(callData.getReport(beginTime, endTime, domain ,"callreport_hour"));
    }

}
