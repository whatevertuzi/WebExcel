package com.zcy.webexcel.Utils;

import com.zcy.webexcel.DaoSys.pojo.CrsData;
import com.zcy.webexcel.DaoSys.pojo.LocalExcelIt;

public interface LocalExcel {

    LocalExcelIt getLocalExcel(String beginTime) throws Exception;
    CrsData getCrs(String beginTime) throws Exception;

}
