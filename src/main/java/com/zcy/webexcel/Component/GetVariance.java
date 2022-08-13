package com.zcy.webexcel.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.zcy.webexcel.pojo.LaiHuSys.HourData;
import com.zcy.webexcel.vo.avgData;
import com.zcy.webexcel.vo.varData;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetVariance {
    public static void getVariance(HttpServletResponse response, int num, List<HourData> datasList, String variancePath, String itAvgPath) {
        EasyExcel.read(variancePath, HourData.class, new PageReadListener<HourData>(dataList -> {
            ServletOutputStream outputStream;
            try {
                outputStream = response.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<varData> varDataList = new ArrayList<>();
            List<avgData> avgDataList = new ArrayList<>();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            for (HourData demoData : dataList) {
                List<HourData> resultList = new ArrayList<>();
                for (HourData datas : datasList) {
                    String fillStartTime = df.format(datas.getStartTime());
                    String toStartTime = df.format(demoData.getStartTime());
                    if (fillStartTime.equals(toStartTime)) {
                        resultList.add(datas);
                    }
                }
                if (resultList.size() != 0) {
                    double avgIncallNum = resultList.stream().mapToDouble(HourData::getIncallNum).average().getAsDouble();
                    double avgIncallAnswerNum = resultList.stream().mapToDouble(HourData::getIncallAnswerNum).average().getAsDouble();
                    double avgActualCallLoss = resultList.stream().mapToDouble(HourData::getActualCallLoss).average().getAsDouble();
                    double avgFloatActualCallLossRate = resultList.stream().mapToDouble(HourData::getFloatActualCallLossRate).average().getAsDouble();
                    double avgMaxLoginAgent = resultList.stream().mapToDouble(HourData::getMaxLoginAgent).average().getAsDouble();
                    avgData avgdata = new avgData();
                    varData vardata = new varData();
                    vardata.setStartTime(df.format(demoData.getStartTime()));
                    avgdata.setStartTime(df.format(demoData.getStartTime()));
                    for (HourData redata : resultList) {
                        vardata.setIncallNum(vardata.getIncallNum() + ((redata.getIncallNum() - avgIncallNum) * (redata.getIncallNum() - avgIncallNum) / num));
                        vardata.setIncallAnswerNum(vardata.getIncallAnswerNum() + ((redata.getIncallAnswerNum() - avgIncallAnswerNum) * (redata.getIncallAnswerNum() - avgIncallAnswerNum)) / num);
                        vardata.setActualCallLoss(vardata.getActualCallLoss() + ((redata.getActualCallLoss() - avgActualCallLoss) * (redata.getActualCallLoss() - avgActualCallLoss) / num));
                        vardata.setFloatActualCallLossRate(vardata.getFloatActualCallLossRate() + ((redata.getFloatActualCallLossRate() - avgFloatActualCallLossRate) * (redata.getFloatActualCallLossRate() - avgFloatActualCallLossRate) / num));
                        vardata.setMaxLoginAgent(vardata.getMaxLoginAgent() + ((redata.getMaxLoginAgent() - avgMaxLoginAgent) * (redata.getMaxLoginAgent() - avgMaxLoginAgent) / num));
                        avgdata.setIncallNum(avgIncallNum);
                        avgdata.setIncallAnswerNum(avgIncallAnswerNum);
                        avgdata.setActualCallLoss(avgActualCallLoss);
                        avgdata.setFloatActualCallLossRate(avgFloatActualCallLossRate);
                        avgdata.setMaxLoginAgent(avgMaxLoginAgent);
                    }
                    varDataList.add(vardata);
                    avgDataList.add(avgdata);
                }
            }
            try (ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(itAvgPath).build()) {
                WriteSheet writeSheet = EasyExcel.writerSheet(0).build();
                WriteSheet writeSheet1 = EasyExcel.writerSheet(2).build();
                excelWriter.fill(avgDataList, writeSheet);
                excelWriter.fill(varDataList, writeSheet1);
                excelWriter.finish();
            }
        })).sheet().doRead();
    }
}
