package com.zcy.webexcel.DaoSys.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayData {
//    String abandonCallNum_5s;
//  String abandonCallNum_10s;
//  String abandonCallNum_15s;
//  String abandonCallNum_20s;
//  String abandonCallNum_25s;
//  String abandonCallNum_30s;
//  String abandonCallNum_35s;
//  String abandonCallNum_40s;
//  String abandonCallNum_45s;
//  String abandonCallNum_50s;
//  String abandonCallNum_55s;
//  String abandonCallNum_60s;
//  String abandonRate;
  private Integer abandoncallNum;
  private Integer abnormalCallLoss;
  private Integer acdIncallNum;
//  String actualCallLossRate;
//  String avgAbandoncallTime;
//  String avgInCallAnswerTime;
//  String avgIncallTime;
//  String avgOutcallAnswerTime;
//  String avgOutcallTime;
//  String callLoss_10S;
//  String callServiceLevel_10s;
//  String callServiceLevel_20s;
//  String createTime;
//  String dateTime;
//  String domain;
//  String id;
//  String inCallAnswerTime;
  private Integer actualCallLoss;
  private Integer incallAnswerNum;
//  String incallAnswerNum_5s;
//  String incallAnswerNum_10s;
//  String incallAnswerNum_15s;
//  String incallAnswerNum_20s;
//  String incallAnswerNum_25s;
//  String incallAnswerNum_30s;
//  String incallAnswerNum_35s;
//  String incallAnswerNum_40s;
//  String incallAnswerNum_45s;
//  String incallAnswerNum_50s;
//  String incallAnswerNum_55s;
//    String incallAnswerNum_60s;
  private Double incallAnswerRate;
//  private Float incallNum;
//  String internalCallNum;
//  String internalCallTime;
//  String maxIncallTime;
//  String maxLoginAgent;
//  String maxOutcallTime;
//  String outcallAnswerRate;
//  String outcallAnswerTime;
//  String totalAbandoncallTime;
  private Double outcallAnswerNum;
  private Double outcallNum;
  private Double totalIncallTime;
  private Double totalOutcallTime;
  private Double maxLoginAgent;
}
