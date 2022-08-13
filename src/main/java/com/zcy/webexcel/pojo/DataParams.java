package com.zcy.webexcel.pojo;

import lombok.Data;

@Data
public class DataParams {
     String beginTime;
     String endTime;
     String doMain;
     String action;
     Integer chatNum;//企微沟通数量
     String avgHour;//微盘工作内容表格的总工时
     String avgBusiness;
     Double notStatic;
     String text;//附加内容
}

