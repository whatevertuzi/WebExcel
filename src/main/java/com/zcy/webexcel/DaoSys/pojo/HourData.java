package com.zcy.webexcel.DaoSys.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HourData {
    //   private int abandonCallNum_5s;
//   private int abandonCallNum_10s;
//   private int abandonCallNum_15s;
//   private int abandonCallNum_20s;
//   private int abandonCallNum_25s;
//   private int abandonCallNum_30s;
//   private int abandonCallNum_35s;
//   private int abandonCallNum_40s;
//   private int abandonCallNum_45s;
//   private int abandonCallNum_50s;
//   private int abandonCallNum_55s;
//   private int abandonCallNum_60s;
//   private int abandonRate;
   private int abandoncallNum;
   private int abnormalCallLoss;
    @ExcelProperty(value = "开始时间")
    @DateTimeFormat("HH:mm")
    private Date startTime;
//   @ExcelProperty(value = "结束时间")
//   @DateTimeFormat("HH:mm:ss")
//   private Date endTime;

   private int acdIncallNum;
    @ExcelProperty(value = "呼入数")
    private int  incallNum;
    @ExcelProperty(value = "呼入接通数")
    private int incallAnswerNum;
//    @ExcelProperty(value = "呼出数",index = 3)
//    private int  outcallNum;
//    @ExcelProperty(value = "手机呼出数",index = 4)
//    private int phoneCall;
    @ExcelProperty(value = "最大坐席登陆数")
    private int  maxLoginAgent;

    @ExcelProperty(value = "呼入放弃数")
    private int actualCallLoss;

    @ExcelProperty(value = "呼损率")
    private Double floatActualCallLossRate;

    private String actualCallLossRate;

//    private String actualCallLossRate;
    //   @ExcelProperty(value = "平均放弃所有时间",index = 8)
//   private int avgAbandoncallTime;
    //   @ExcelProperty(value = "平均来电应答时间",index =9)
//   private int avgInCallAnswerTime;
    //   @ExcelProperty(value = "平均通话时间",index = 10)
//   private int  avgIncallTime;
    //   @ExcelProperty(value = "平均外呼应答时间",index = 11)
//   private int  avgOutcallAnswerTime;
    //   @ExcelProperty(value = "平均外呼时间",index = 12)
//   private int  avgOutcallTime;
    //   @ExcelProperty(value = "20秒服务",index = 13)
//   private String callServiceLevel_20s;
    //   @ExcelProperty(value = "创建时间",index = 14)
//   private String  createTime;
    //   @ExcelProperty(value = "日期",index = 15)
//   private String  dateTime;
    //   @ExcelProperty(value = "域名",index = 16)
//   private String  domain;
    //   @ExcelProperty(value = "小时",index = 17)
//   private String  hourTime;
    //   @ExcelProperty(value = "ID",index = 18)
//   private int id;
    //   @ExcelProperty(value = "来电应答时间",index = 19)
//   private int inCallAnswerTime;
    //   @ExcelProperty(value = "来电5s内应答数",index = 21)
//   private int incallAnswerNum_5s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_10s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_15s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_20s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_25s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_30s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_35s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_40s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_45s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_50s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_55s;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int incallAnswerNum_60s;
//    @ExcelProperty(value = "来电应答率",index = 22)
//    private int incallAnswerRate;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int  internalCallNum;
    //   @ExcelProperty(value = "最大坐席登陆数",index = 7)
//   private int  internalCallTime;
    //   @ExcelProperty(value = "最大接听时间",index = 23)
//   private int  maxIncallTime;
    //   @ExcelProperty(value = "最大外呼时间",index = 24)
//   private int  maxOutcallTime;
    //   @ExcelProperty(value = "外呼应答数",index = 25)
//   private int  outcallAnswerNum;
    //   @ExcelProperty(value = "外呼总应答率",index = 26)
//   private int  outcallAnswerRate;
    //   @ExcelProperty(value = "外呼总应答时间",index = 27)
//   private int  outcallAnswerTime;
    //   @ExcelProperty(value = "总放弃时间",index = 28)
//   private int  totalAbandoncallTime;
    //   @ExcelProperty(value = "总进线时间",index = 29)
//   private int  totalIncallTime;
    //   @ExcelProperty(value = "总外呼时间",index = 30)
//   private int  totalOutcallTime;
}
