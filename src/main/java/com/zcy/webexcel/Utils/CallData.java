package com.zcy.webexcel.Utils;

import java.util.List;

public interface CallData {
    Integer getPage(String beginTime, String endTime, String domain, String action) throws Exception;
    List  getReport(String beginTime, String endTime , String domain , String action ) throws Exception;

}
