package com.zcy.webexcel.DaoSys.vo;

import lombok.Data;

@Data
public class ItFillExcel {
    private String title;
    private Double incallAnswerRate;
    private String incallAnswerRateIsDone;
    private String incallAnswerDiff;
    private Double incallAnswerMonthRate;
    private int triggerNumber;
    private Double triggerRate;
    private String triggerRateIsDone;
    private String triggerRateDiff;
    private Double triggerMonthRate;
    private String verySatisfiedWithThePercentage;
    private String PercentageIsDone;
    private String PercentageDiff;
    private Double PercentageMonthDone;
    private Integer dayDone;
    private Integer people;
    private Integer hour;
    private String isDone;
    private Double dayAvgDone;
    private String signaturePlan;
    private String signature;
    private String signatureIsDone;
    private String signatureDiff;
    private Integer signatureMonthDone;
}
