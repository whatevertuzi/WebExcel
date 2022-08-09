package com.zcy.webexcel.DaoSys.vo;


import lombok.Data;

@Data
public class YudingFillExcel {
    private String title;
    private String answerRate;
    private String answerRateIsDone;
    private String answerRateDiff;
    private String answerRateMonthDone;
    private String satisfy;
    private String satisfyIsDone;
    private String satisfyDiff;
    private String satisfyMonthDone;
    private Double avgPhone;
    private String avgPhoneIsDone;
    private String avgPhoneDiff;
    private Double avgPhoneMonthDone;
    private Double complaintTime;
    private String complaintTimeIsDone;
    private String complaintTimeDiff;
    private Double complaintTimeMonthDone;
    private Double avgBusiness;
    private String avgBusinessIsDone;
    private String avgBusinessDiff;
    private Double avgBusinessMonthDone;
    private Integer callNumSum;
    private Integer callNumDay;
    private Integer callNumUnDone;
    private Integer callNumHistory;
    private Double callNumMin;
    private Double callNumAvgMin;
    private Double businessNumSum;
    private Integer businessNumDay;
    private Integer businessUnDone;
    private Integer businessHistory;
    private Integer businessMin;
    private Double businessAvgMin;
    private Integer chatSum;
    private Integer chatUnDone;
    private Integer chatHistory;
//    private Integer chatMin;
//    private Integer chatAvgMin;
    private Integer crossSectorNumSum;
    private Integer crossSectorNumDay;
    private Integer crossSectorNumUnDone;
    private Integer crossSectorNumHistory;
//    private Integer crossSectorNumMin;
//    private Integer crossSectorNumAvgMin;
    private String SumDay;
    private String SumDayDone;
    private String SumDayUnDone;
    private String SumDayHis;
    private String SumDayMin;
    private String SumDayAvgMin;

}
