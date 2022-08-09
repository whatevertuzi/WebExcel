package com.zcy.webexcel.DaoSys.pojo;

import lombok.Data;

@Data
public class AgentReportDay {
//    abandonCallNum_5s: 0
//    abandonCallNum_10s: 0
//    abandonCallNum_15s: 0
//    abandonCallNum_20s: 0
//    abandonCallNum_25s: 1
//    abandonCallNum_30s: 1
//    abandonCallNum_35s: 1
//    abandonCallNum_40s: 1
//    abandonCallNum_45s: 1
//    abandonCallNum_50s: 1
//    abandonCallNum_55s: 1
//    abandonCallNum_60s: 1
//    auxcode1Time: 0
//    auxcode1Times: 0
//    auxcode2Time: 0
//    auxcode2Times: 0
//    auxcode3Time: 0
//    auxcode3Times: 0
//    auxcode4Time: 0
//    auxcode4Times: 0
//    auxcode5Time: 0
//    auxcode5Times: 0
//    auxcode6Time: 0
//    auxcode6Times: 0
//    auxcode7Time: 0
//    auxcode7Times: 0
//    auxcode8Time: 0
//    auxcode8Times: 0
//    auxcode9Time: 0
//    auxcode9Times: 0
//    auxcode10Time: 0
//    auxcode10Times: 0
//    avgAcwTime: 0
//    avgAuxTime: 301
//    private Double avgInCallAnswerTime;//平均呼入时长
//    private Double avgIncallAbandonTime;//平均放弃时长
//    private Double avgOutcallAnswerTime;//平均呼出接通时长
//    private Double avgOutcallTime;//平均外呼时长
//    private Double avgReadyTime;//平均就绪时长
//    createTime: "2022-07-02 00:11:10.769"
//    dateTime: "2022-07-01"
//    domain: "helpdesk.greentree.com"
//    holdTime: 18
//    id: 504369
//    private Double inCallAnswerTime;
//    private Double incallAbandonTime;
//    incallAnswerNum_5s: 1
//    incallAnswerNum_10s: 1
//    incallAnswerNum_15s: 2
//    incallAnswerNum_20s: 2
//    incallAnswerNum_25s: 2
//    incallAnswerNum_30s: 3
//    incallAnswerNum_35s: 3
//    incallAnswerNum_40s: 5
//    incallAnswerNum_45s: 5
//    incallAnswerNum_50s: 5
//    incallAnswerNum_55s: 5
//    incallAnswerNum_60s: 5
//    incallAvgTime: 146
//    incallMaxTime: 384
//    maxAcwTime: 0
//    maxAuxTime: 366
//    maxHoldcallTime: 14
//    maxIncallAbandonTime: 20
//    maxOutcallTime: 33
//    maxReadyTime: 4
//    private Double outcallAnswerNum;
//    outcallAnswerRate: 100
//    private Double outcallAnswerTime;
//    serviceLevel_5s: 14
//    serviceLevel_10s: 14
//    serviceLevel_15s: 28
//    serviceLevel_20s: 28
//    serviceLevel_25s: 28
//    serviceLevel_30s: 42
//    serviceLevel_35s: 42
//    serviceLevel_40s: 71
//    serviceLevel_45s: 71
//    serviceLevel_50s: 71
//    serviceLevel_55s: 71
//    serviceLevel_60s: 71
//    totalAcwTime: 0
//    totalAcwTimes: 0
//    totalAuxTime: 603
//    totalAuxTimes: 2
//    totalLoginTime: 1956
//    totalReadyTime: 4
//    totalReadyTimes: 8

    private Double incallAnswerRate;//接通率
    private String agentId;//坐席号
    private String agentName;//坐席名称
    private Double incallAnswerNum;//呼入接通数
    private Double incallNum;//呼入数
    private Double incallAbandonNum;//呼入放弃数
    private Double incallTime;//呼入总时长
    private Double outcallNum;//呼出总数
    private Double totalOutcallTime;//呼出总时长
}
