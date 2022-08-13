package com.zcy.webexcel.pojo.LaiHuSys;

import lombok.Data;

@Data
public class Satisfy {
   private int answerNum;
   private String basicSatisfactionPercentage;
    private int callType;
    private String dateTime;
    private String domain;
    private int id;
    private int notSatisfiedNum;
    private String notSatisfiedPercentage;
    private int satisfactionNum;
    private int totalNumberOfReviews;
    private int triggerNumber;
    private String triggerRate;
    private Double floatTriggerRate;
    private int verySatisfiedNum;
    private String verySatisfiedWithThePercentage;
    private Double FloatPercentage;
}
