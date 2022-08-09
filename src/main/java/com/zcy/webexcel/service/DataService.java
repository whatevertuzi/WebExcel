package com.zcy.webexcel.service;


import com.zcy.webexcel.DaoSys.vo.Result;

public interface DataService {
    Result getData(String beginTime, String endTime, String domain) throws Exception;
}
