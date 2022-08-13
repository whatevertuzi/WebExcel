package com.zcy.webexcel.pojo.LaiHuSys;

import lombok.Data;

@Data
public class AgentSatisfy {
   private String agentId;//坐席号
   private String agentName;
   private Double triggerNumber;
   private String triggerRate;
   private String verySatisfiedWithThePercentage;//非常满意率
   private Double floatVerySatisfiedWithThePercentage;//非常满意率
   private String notSatisfiedPercentage;//不满意率
   private Double totalNumberOfReviews;//总评价次数
   private Double notSatisfiedNum;//不满意评价次数
   private Double floatNotSatisfy;

}
