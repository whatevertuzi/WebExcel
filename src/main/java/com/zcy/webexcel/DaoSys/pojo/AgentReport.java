package com.zcy.webexcel.DaoSys.pojo;

import lombok.Data;

@Data
public class AgentReport {
  private String agentId;
  private String agentName;
  private String averageProcessingTime;
  private Double avgInCallAnswerTime;
  private String callRate;
  private String dateTime;
  private Double holdTime;
  private Double inCallAnswerTime;
  private String inCallRate;
  private Double incallAnswerNum;
  private Double maxHoldCallTime;
  private String non_machineRatio;
  private Double outCallNum;
  private Double totalAcwNum;
  private Double totalAcwTime;
  private Double totalLoginTime;
  private Double totalOutcallTime;
  private Double totalReadyTime;
  private String waitingRatio;
  private Double waitingTime;
}
