package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.pojo.DataParams;

import javax.servlet.http.HttpServletResponse;


public interface ExcelService {
    void exportExcel(HttpServletResponse response, DataParams dataParams) throws Exception;
    void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception;
}
