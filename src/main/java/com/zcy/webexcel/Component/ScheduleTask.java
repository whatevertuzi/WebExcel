package com.zcy.webexcel.Component;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.abel533.echarts.Grid;
import com.github.abel533.echarts.Legend;
import com.github.abel533.echarts.Title;
import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.SplitLine;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.*;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.json.GsonUtil;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.github.abel533.echarts.style.AreaStyle;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.LineStyle;
import com.github.abel533.echarts.style.TextStyle;
import com.github.abel533.echarts.style.itemstyle.Normal;
import com.zcy.webexcel.pojo.DailyData;
import com.zcy.webexcel.pojo.LaiHuSys.HourData;
import com.zcy.webexcel.service.GetLaiHuDataService;
import com.zcy.webexcel.service.ItGroupService;
import com.zcy.webexcel.vo.ItFillExcel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleTask {

    private final GetLaiHuDataService getLaiHuDataService;

    private final ItGroupService itGroupService;

    public ScheduleTask(GetLaiHuDataService getLaiHuDataService, ItGroupService itGroupService) {
        this.getLaiHuDataService = getLaiHuDataService;
        this.itGroupService = itGroupService;
    }

    @Scheduled(cron = "0 0 12 * * ? ")
    public void schedueItYesterday() throws Exception{
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd 07:00");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 01:00");
        String beginTime = format1.format(new Date(new Date().getTime()-24*60*60*1000));
        String endTime = format2.format(new Date());
        String domain = "helpdesk.greentree.com";
        generateImg(domain,beginTime.substring(0,10),beginTime,endTime);
        DailyData daily = itGroupService.getDailyData(beginTime,endTime);
        List<HourData> timeDataList = daily.getHourDataList();
        ItFillExcel itFillExcel =daily.getItFillExcel();

        //excel写在本地
        String itFillPath = "itdemo_fill.xlsx";
        ExcelWriter excellocalWriter = EasyExcel.write(  "FilesIt/"+"IT" + beginTime.substring(0, 10) + ".xlsx").withTemplate(itFillPath).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("分时呼损").build();//本地和前后端公用
        WriteSheet writeSheet1 = EasyExcel.writerSheet("数据汇总").build();//同上
        excellocalWriter.fill(timeDataList, writeSheet);
        excellocalWriter.fill(itFillExcel, writeSheet1);
        excellocalWriter.finish();

        InputStream inputStream = null;
        byte[] buffer = null;
        //读取图片字节数组
        try {
            File imgFile = new File("FilesIt/IT"+beginTime.substring(0,10)+".png");
            inputStream = new FileInputStream(imgFile);
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            buffer = new byte[count];
            inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        byte[] encode = java.util.Base64.getEncoder().encode(buffer);
        itGroupService.sendEmail(beginTime,encode);
    }

    @Scheduled(cron = "0 08 20 * * ? ")
    public void schedueItToday() throws Exception{
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd 07:00");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 18:30");
        String beginTime = format1.format(new Date());
        String endTime = format2.format(new Date());
        String domain = "helpdesk.greentree.com";
        generateImg(domain,beginTime.substring(0,10),beginTime,endTime);
        DailyData daily = itGroupService.getDailyData(beginTime,endTime);
        List<HourData> timeDataList = daily.getHourDataList();
        ItFillExcel itFillExcel =daily.getItFillExcel();

        //excel写在本地
        String itFillPath = "itdemo_fill.xlsx";
        ExcelWriter excellocalWriter = EasyExcel.write(  "FilesIt/"+"IT" + beginTime.substring(0, 10) + ".xlsx").withTemplate(itFillPath).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("分时呼损").build();//本地和前后端公用
        WriteSheet writeSheet1 = EasyExcel.writerSheet("数据汇总").build();//同上
        excellocalWriter.fill(timeDataList, writeSheet);
        excellocalWriter.fill(itFillExcel, writeSheet1);
        excellocalWriter.finish();

        InputStream inputStream = null;
        byte[] buffer = null;
        //读取图片字节数组
        try {
            File imgFile = new File("FilesIt/IT"+beginTime.substring(0,10)+".png");
            inputStream = new FileInputStream(imgFile);
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            buffer = new byte[count];
            inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        byte[] encode = java.util.Base64.getEncoder().encode(buffer);
        itGroupService.sendEmail(beginTime,encode);
    }

    public void generateImg(String domain,String path,String beginTime ,String endTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        List<HourData> callreport_hour = getLaiHuDataService
                .getCallReportHour(beginTime, endTime, domain, "callreport_hour");
        List<Date> xAsisList = callreport_hour.stream().map(HourData::getStartTime).collect(Collectors.toList());
        List<String> stringXasisList = new ArrayList<>();
        List<Double> finalLossRateList = new ArrayList<>();
        List<Integer> inCallList = callreport_hour.stream().map(HourData::getIncallNum).collect(Collectors.toList());
        List<Integer> lossCallList = callreport_hour.stream().map(HourData::getActualCallLoss).collect(Collectors.toList());
        List<Integer> peopleNumList = callreport_hour.stream().map(HourData::getMaxLoginAgent).collect(Collectors.toList());
        List<Double> lossRateList = callreport_hour.stream().map(HourData::getFloatActualCallLossRate).collect(Collectors.toList());
        for (Date date : xAsisList) {
            stringXasisList.add(format.format(date));
        }
        for (Double aDouble : lossRateList) {
            if (aDouble==0){
                finalLossRateList.add(null);
                continue;
            }
            finalLossRateList.add(aDouble*100);
        }
        generateEchartsJson(stringXasisList,inCallList,lossCallList,peopleNumList,finalLossRateList);
        String dataPath = "myoption.json";
        String fileName= "FilesIt/IT"+path+ ".png";
        try {
            String cmd = "C:\\Program Files\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe " + "echarts-convert.js" + " -infile " + dataPath + " -outfile " + fileName;//生成命令行
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateEchartsJson(List<String>xAis, List<Integer> seriesInCallNum, List<Integer> seriesLossCallNum, List<Integer> seriesPeopleNum, List<Double> seriesLossRate){

        TextStyle textStyle = new TextStyle();
        textStyle.setColor("#000000");
        textStyle.setFontStyle(FontStyle.normal);
        textStyle.setFontWeight(FontWeight.lighter);
        textStyle.setFontSize(15);

        Title title = new Title();
        title.setText("分时段 呼入数-放弃数-呼损率-当班人数 情况");
        title.setTextStyle(textStyle);
        title.setX(X.center);
        title.setY(Y.top);

        Legend legend = new Legend();
        legend.setOrient(Orient.vertical);
        legend.setLeft("center");
        legend.setBottom("bottom");
        List<String> legendList = new ArrayList<>();
        legendList.add("呼入数");
        legendList.add("呼入放弃数");
        legendList.add("当班人数");
        legendList.add("呼损率");
        legend.setData(legendList);

        ValueAxis xAxis = new ValueAxis();
        ValueAxis y1Axis = new ValueAxis();
        ValueAxis y2Axis = new ValueAxis();
        y1Axis.setType(AxisType.value);
        y1Axis.setSplitLine(new SplitLine().show(false));
        y1Axis.setMin(0);
        y1Axis.setInterval(100);
        y1Axis.setAxisLabel(new AxisLabel().formatter("{value}"));

        y2Axis.setType(AxisType.value);
        y2Axis.setSplitLine(new SplitLine().show(true));
        y2Axis.setMin(0);
        y2Axis.setInterval(20);
        y2Axis.setAxisLabel(new AxisLabel().formatter("{value}%"));

        xAxis.setType(AxisType.category);
        xAxis.setBoundaryGap(true);
        xAxis.setData(xAis);

        List<Axis> xAxisList = new ArrayList<>();
        xAxisList.add(xAxis);
        List<Axis> yAxisList = new ArrayList<>();
        yAxisList.add(y1Axis);
        yAxisList.add(y2Axis);

        Bar InCallBar = new Bar("呼入数");
        Bar LossCallBar = new Bar("呼入放弃数");
        Line peopleNumLine = new Line("当班人数");
        Line LossRateLine = new Line("呼损率");

        InCallBar.setData(seriesInCallNum);
        InCallBar.setZ(3);
        InCallBar.setYAxisIndex(0);
        InCallBar.setItemStyle(new ItemStyle().color("#424242"));

        LossCallBar.setData(seriesLossCallNum);
        LossCallBar.setZ(3);
        LossCallBar.setYAxisIndex(0);
        LossCallBar.setItemStyle(new ItemStyle().color("#9d9d9d"));

        peopleNumLine.setYAxisIndex(0);
        peopleNumLine.setLineStyle(new LineStyle().width(1).color("#d0d0d0"));
        peopleNumLine.setStack("数据");
        peopleNumLine.setData(seriesPeopleNum);
        peopleNumLine.setAreaStyle(new AreaStyle().color("#d0d0d0"));
        peopleNumLine.setItemStyle(new ItemStyle().normal(new Normal().show(false)));

        LossRateLine.setYAxisIndex(1);
        LossRateLine.setLineStyle(new LineStyle().width(3).color("#d0d0d0").opacity(0.0));
        LossRateLine.setData(seriesLossRate);
        LossRateLine.setSymbol("circle");
        //折线（区域）图、柱状（条形）图、K 线图 : {a}（系列名称），{b}（类目值），{c}（数值）, {d}（无）
        LossRateLine.setLabel(new ItemStyle().normal(new Normal().show(true).formatter("{c}"+"%")));
        LossRateLine.setItemStyle(new ItemStyle().color("#5b5a5a"));

        List<Series> seriesList = new ArrayList<>();
        seriesList.add(InCallBar);
        seriesList.add(LossCallBar);
        seriesList.add(peopleNumLine);
        seriesList.add(LossRateLine);

        GsonOption option = new GsonOption();
        option.setBackgroundColor("#ffffff");
        option.setTitle(title);
        option.setLegend(legend);
        option.setxAxis(xAxisList);
        option.setyAxis(yAxisList);
        option.setGrid(new Grid().x(50).y(50).x2(50).y2(50));
        option.setSeries(seriesList);
        String content = GsonUtil.format(option);
        try {

            // Java 11 , default StandardCharsets.UTF_8
            String path="myoption.json";
            Files.writeString(Paths.get(path), content);

            // encoding
            // Files.writeString(Paths.get(path), content, StandardCharsets.US_ASCII);

            // extra options
            // Files.writeString(Paths.get(path), content,
            //		StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
