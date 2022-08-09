package com.zcy.webexcel.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcy.webexcel.DaoSys.CustomerService;
import com.zcy.webexcel.DaoSys.SysBusinessStatistics;
import com.zcy.webexcel.DaoSys.YudingDay;
import com.zcy.webexcel.DaoSys.mapper.CustomerServiceMapper;
import com.zcy.webexcel.DaoSys.mapper.SysBusinessStatisticsMapper;
import com.zcy.webexcel.DaoSys.mapper.YudingDayMapper;
import com.zcy.webexcel.DaoSys.pojo.*;
import com.zcy.webexcel.DaoSys.vo.ItFillExcel;
import com.zcy.webexcel.DaoSys.vo.YudingFillExcel;
import com.zcy.webexcel.Utils.CallData;
import com.zcy.webexcel.Utils.GetJsonArrayImpl;
import com.zcy.webexcel.Utils.LocalExcel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

    //日志
    private static final Logger LOGGER = LogManager.getLogger(ExcelServiceImpl.class);



    //保留两位小数
    private static Double twoScale(Double number){
        if (number == null)
            return null;
        //利用BigDecimal来实现四舍五入.保留一位小数
        //1代表保留1位小数,保留两位小数就是2,依此累推
        //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
        return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    //来虎接口
    private final CallData callData;

    //本地excel
    private final LocalExcel localExcel;

    //Mapper
    private final SysBusinessStatisticsMapper sysBusinessStatisticsMapper;
    private final YudingDayMapper yudingDayMapper;
    private final CustomerServiceMapper customerServiceMapper;

    @Autowired
    public ExcelServiceImpl(CallData callData,
                            LocalExcel localExcel,
                            SysBusinessStatisticsMapper sysBusinessStatisticsMapper,
                            YudingDayMapper yudingDayMapper, CustomerServiceMapper customerServiceMapper){
        this.callData = callData;
        this.localExcel = localExcel;
        this.sysBusinessStatisticsMapper = sysBusinessStatisticsMapper;
        this.yudingDayMapper = yudingDayMapper;

        this.customerServiceMapper = customerServiceMapper;
    }


    @Override
    public void exportExcel(HttpServletResponse response, DataParams dataParams) throws Exception {
        /*
         * EasyExcel 有多个不同的read方法，适用于多种需求
         * 这里调用EasyExcel中通过OutputStream流方式输出Excel的write方法
         * 它会返回一个ExcelWriterBuilder类型的返回值
         * ExcelWriterBuilde中有一个doWrite方法，会输出数据到设置的Sheet中
         */
        String action = dataParams.getAction();
        String domain =dataParams.getDoMain();
        String beginTime = dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();

        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();
        String templateFile;
        if (Objects.equals(action,"dayData")) {
            if (Objects.equals(domain, "helpdesk.greentree.com")) {
                //模板路径
                templateFile = "itdemo_fill.xlsx";

                List<HourData> timeDataList;
                List<DayData> dayDataList;
                List<DayData> monthDayDataList;
                List<Satisfy> satisifyList;
                List<Satisfy> monthsatisifyList;
//            List<CallRecord> callRecordsList;

                //从来虎获取数据
                try {

                    //IT分时呼损数据
                    timeDataList = callData.getReport(beginTime, endTime, domain, "callreport_hour");

                    //IT当日呼损数据
                    dayDataList = callData.getReport(beginTime, endTime, domain, "callreport_day");

                    //IT当月数据
                    monthDayDataList = callData.getReport(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "callreport_day");

                    //IT当日满意率
                    satisifyList = callData.getReport(beginTime.substring(0, 10), endTime.substring(0, 10), domain, "satisfaction_day");

                    //IT当月满意率
                    monthsatisifyList = callData.getReport(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "satisfaction_day");

                    //IT当月满意率
//                callRecordsList = callData.getReport(beginTime.substring(0,10),endTime.substring(0,10),domain,"callrecord");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                //当日报修记录excel
                LocalExcelIt dayLocalExcelIt = localExcel.getLocalExcel(beginTime);

                //当月报修记录excel
                LocalExcelIt monthLocalExcelIt = localExcel.getLocalExcel(beginTime.substring(0,7));

                ItFillExcel itFillExcel = new ItFillExcel();

                //title
                itFillExcel.setTitle(beginTime.substring(5, 7) + '/' + beginTime.substring(8, 10) + "指标完成情况汇报—实施服务组");

                //当月呼入接通率
                itFillExcel.setIncallAnswerMonthRate(monthDayDataList.stream().mapToDouble(DayData::getIncallAnswerRate).average().getAsDouble() * 0.01);

                //当日呼入接通率
                itFillExcel.setIncallAnswerRate(dayDataList.get(0).getIncallAnswerRate() * 0.01F);
                if (itFillExcel.getIncallAnswerRate() >= 0.7832) {
                    itFillExcel.setIncallAnswerRateIsDone("已完成");
                    itFillExcel.setIncallAnswerDiff("/");
                } else {
                    itFillExcel.setIncallAnswerRateIsDone("未完成");
                    itFillExcel.setIncallAnswerDiff(twoScale((0.7832 - itFillExcel.getIncallAnswerRate()) * 100) + "%");
                }

                //当日触发数
                itFillExcel.setTriggerNumber(satisifyList.get(0).getTriggerNumber());

                //当日触发率
                itFillExcel.setTriggerRate(satisifyList.get(0).getFloatTriggerRate() * 0.01);
                if (itFillExcel.getTriggerRate() >= 0.50) {
                    itFillExcel.setTriggerRateIsDone("已完成");
                    itFillExcel.setTriggerRateDiff("/");
                } else {
                    itFillExcel.setTriggerRateIsDone("未完成");
                    itFillExcel.setTriggerRateDiff(twoScale((0.50 - itFillExcel.getTriggerRate()) * 100) + "%");
                }

                //当月触发率
                itFillExcel.setTriggerMonthRate(monthsatisifyList.stream().mapToDouble(Satisfy::getFloatTriggerRate).average().getAsDouble() * 0.01);

                //当日满意度
                itFillExcel.setVerySatisfiedWithThePercentage(satisifyList.get(0).getVerySatisfiedWithThePercentage());
                if (Float.parseFloat(itFillExcel.getVerySatisfiedWithThePercentage().substring(0, 4)) >= 99.3) {
                    itFillExcel.setPercentageIsDone("已完成");
                    itFillExcel.setPercentageDiff("/");
                } else {
                    itFillExcel.setPercentageIsDone("未完成");
                    itFillExcel.setPercentageDiff(twoScale(99.3-Double.parseDouble(itFillExcel.getVerySatisfiedWithThePercentage().substring(0, 4))) + "%");
                }

                //当月满意度
                itFillExcel.setPercentageMonthDone(twoScale(monthsatisifyList.stream().mapToDouble(Satisfy::getFloatPercentage).average().getAsDouble())*0.01);

                //当日报修处理数
                itFillExcel.setDayDone(dayLocalExcelIt.getDayDone());

                //当日发日报人数
                if (dayLocalExcelIt.getSignature()!=0){
                    itFillExcel.setPeople(dayLocalExcelIt.getPeople()-1);
                }else {
                    itFillExcel.setPeople(dayLocalExcelIt.getPeople());
                }

                //当日处理报修用时
                itFillExcel.setHour(dayLocalExcelIt.getHour());

                //人日均处理数
                itFillExcel.setDayAvgDone((double)itFillExcel.getDayDone() / itFillExcel.getPeople());

                //当日会签回访数量
                itFillExcel.setSignature(String.valueOf(dayLocalExcelIt.getSignature()));
                if (dayLocalExcelIt.getSignature() >= 30) {
                    itFillExcel.setSignaturePlan("30");
                    itFillExcel.setSignatureIsDone("已完成");
                    itFillExcel.setSignatureDiff("/");
                } else if (dayLocalExcelIt.getSignature()==0) {
                    itFillExcel.setSignaturePlan("/");
                    itFillExcel.setSignature("/");
                    itFillExcel.setSignatureIsDone("/");
                    itFillExcel.setSignatureDiff("/");
                } else {
                    itFillExcel.setSignaturePlan("30");
                    itFillExcel.setSignatureIsDone("未完成");
                    itFillExcel.setSignatureDiff(String.valueOf(30 - dayLocalExcelIt.getSignature()));
                }

                //当月会签回访总数
                itFillExcel.setSignatureMonthDone(monthLocalExcelIt.getSignature());

                //excel写在本地
                ExcelWriter excellocalWriter = EasyExcel.write(  "FilesIt/"+"IT" + beginTime.substring(0, 10) + ".xlsx").withTemplate(templateFile).build();
                WriteSheet writeSheet = EasyExcel.writerSheet("分时呼损").build();//本地和前后端公用
                WriteSheet writeSheet1 = EasyExcel.writerSheet("数据汇总").build();//同上
                excellocalWriter.fill(timeDataList, writeSheet);
                excellocalWriter.fill(itFillExcel, writeSheet1);
                excellocalWriter.finish();

                //返回给前端excel
                ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(templateFile).build();
                excelWriter.fill(timeDataList, writeSheet);
                excelWriter.fill(itFillExcel, writeSheet1);
                excelWriter.finish();
                LOGGER.info("下载了实施组日期为" + beginTime.substring(0, 10) + "的数据");
            } else if (Objects.equals(domain, "yuding.greentree.com")) {

                //模板路径
                templateFile = "demo_fill.xlsx";
                List<HourData> timeDataList;//预订分时数据
//                List<DayData> dayDataList;//当日呼损数据
//                List<DayData> monthDayDataList;//预订当月数据
                List<AgentSatisfy> satisifyList;//当日满意率
//                List<AgentSatisfy> monthsatisifyList;//当月满意率
                List<AgentReport> agentReportList;//坐席统计报表
                List<SkillHourData> skillHourDataList;//201技能组分时报表
                List<CustomerService> customerServiceList;//周工作计划及完成情况-客服组
//                List<SkillHourData> monthSkillHourDataList;//201技能组分时报表
//            List<CallRecord> callRecordsList;

                //从来虎获取数据
                try {

                    //预订分时数据
                    timeDataList = callData.getReport(beginTime, endTime, domain, "callreport_hour");

                    //预订当日呼损数据
//                    dayDataList = callData.getReport(beginTime, endTime, domain, "callreport_day");

                    //预订当月数据
//                    monthDayDataList = callData.getReport(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "callreport_day");

                    //预订当日满意率
                    satisifyList = callData.getReport(beginTime.substring(0, 10), beginTime.substring(0, 10), domain, "satisfactionStatistics");

                    //预订当月满意率
//                    monthsatisifyList = callData.getReport(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "satisfactionStatistics");

                    //预订坐席统计报表
                    agentReportList = callData.getReport(beginTime.substring(0, 10), beginTime.substring(0,10), domain,"agentreport");

                    //预订当日203技能组分时报表
                    skillHourDataList = callData.getReport(beginTime,endTime,domain,"skillreport_hour");

                    //预订当月201技能组分时报表
//                    monthSkillHourDataList = callData.getReport(beginTime.substring(0, 8) + "01",endTime,domain,"skillreport_hour");
                    //预订通话记录
//                callRecordsList = callData.getReport(beginTime.substring(0,10),endTime.substring(0,10),domain,"callrecord");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                //计算接通率
//                double abandonRate = skillHourDataList.stream().mapToDouble(SkillHourData::getAbandonRate).average().getAsDouble();
                double allAcd = timeDataList.stream().mapToInt(HourData::getIncallNum).sum();
                System.out.println("呼叫分时报表进线量："+allAcd);
                double allLoss = timeDataList.stream().mapToInt(HourData::getActualCallLoss).sum();
                System.out.println("呼叫分时报表放弃数："+allLoss);
                double hotelAcd = skillHourDataList.stream().mapToDouble(SkillHourData::getIncallNum).sum();
                System.out.println("203技能组分时报表进线量："+hotelAcd);
                double hotelLoss = skillHourDataList.stream().mapToDouble(SkillHourData::getAbandoncallNum).sum();
                System.out.println("203技能组分时报表放弃数："+hotelLoss);
                double abandonRate = (allLoss-hotelLoss)/(allAcd-hotelAcd) ;
                double AnswerRate = 100-(abandonRate*100);
                YudingFillExcel yudingFillExcel = new YudingFillExcel();

                //预订标题
                yudingFillExcel.setTitle(beginTime.substring(5, 7) + '/' + beginTime.substring(8, 10) + "指标完成情况汇报—预订客服部");

                List<String> NoList = getNoName(beginTime);
                List<String> LastList = getNight(beginTime);
//                String s = dataParams.getNotStatic();
//                StringTokenizer st = new StringTokenizer(s,",");
//                while (st.hasMoreElements()) {
//                    LastList.add((String) st.nextElement());
//                }
                System.out.println(LastList);
                Double incallAnswerNum = 0.00;
                Double outcallNum = 0.00;
                Double incallAnswer = 0.00;
                Double outcall = 0.00;
                Double statisfy =0.00;
                Double totalStatisfy = 0.00;
                Double totalReview = 0.00;
                List<YudingDayDone> yudingDayDones = new ArrayList<>();
                for (String no : NoList) {
                    YudingDayDone yudingDayDone = new YudingDayDone();
                    for (AgentReport agentReport : agentReportList) {
                        if (Objects.equals(agentReport.getAgentId(), no)) {
                            yudingDayDone.setNo(no);
                            yudingDayDone.setName(agentReport.getAgentName());
                            yudingDayDone.setInCall(agentReport.getIncallAnswerNum());
                            yudingDayDone.setOutCall(agentReport.getOutCallNum());
                            yudingDayDone.setAllCall(yudingDayDone.getInCall()+yudingDayDone.getOutCall());
                            incallAnswer += agentReport.getIncallAnswerNum();
                            outcall += agentReport.getOutCallNum();
                        }
                    }
                    if (!LastList.contains(no)) {
                        for (AgentReport agentReport : agentReportList) {
                            if (Objects.equals(agentReport.getAgentId(), no)) {
                                incallAnswerNum += agentReport.getIncallAnswerNum();
                                outcallNum += agentReport.getOutCallNum();
                            }
                        }
                    }
                    for (AgentSatisfy agentSatisfy : satisifyList) {
                        if (Objects.equals(agentSatisfy.getAgentId(), no)) {
                            yudingDayDone.setSatisfy(twoScale(100-(agentSatisfy.getNotSatisfiedNum()/agentSatisfy.getTotalNumberOfReviews())*100));
                            System.out.println(no+":"+agentSatisfy.getFloatNotSatisfy());
                            statisfy += agentSatisfy.getNotSatisfiedNum()/agentSatisfy.getTotalNumberOfReviews();
                            totalStatisfy+=agentSatisfy.getNotSatisfiedNum();
                            totalReview+=agentSatisfy.getTotalNumberOfReviews();
                        }
                    }
                    yudingDayDones.add(yudingDayDone);
                }
                System.out.println("总不满意数"+totalStatisfy);
                System.out.println("总评价数"+totalReview);
                System.out.println(NoList);
                System.out.println("呼入数："+incallAnswer);
                System.out.println("呼出数："+outcall);


                YudingDay yudingDay = new YudingDay();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(beginTime.substring(0,10));
                yudingDay.setDate(date);
                yudingDay.setDayCall(yudingFillExcel.getAvgPhone());
                Date startDate = format.parse(beginTime.substring(0,8)+"01");
                Date endDate = format.parse(beginTime.substring(0,10));
                LambdaQueryWrapper<YudingDay> monthYudingDayLambdaQueryWrapper = new LambdaQueryWrapper<>();
                monthYudingDayLambdaQueryWrapper.ge(YudingDay::getDate,startDate);//大于开始日期
                monthYudingDayLambdaQueryWrapper.le(YudingDay::getDate,endDate);//小于结束日期
                List<YudingDay> monthYudingDayList = yudingDayMapper.selectList(monthYudingDayLambdaQueryWrapper);
                int countDayCall = monthYudingDayList.size();

                //400的接通率
                yudingFillExcel.setAnswerRate(twoScale(AnswerRate) + "%");
                if (AnswerRate > 97.2) {
                    yudingFillExcel.setAnswerRateIsDone("已完成");
                    yudingFillExcel.setAnswerRateDiff("/");
                } else {
                    yudingFillExcel.setAnswerRateIsDone("未完成");
                    yudingFillExcel.setAnswerRateDiff(twoScale(97.20 - AnswerRate) + "%");
                }

                //400的满意率
                yudingFillExcel.setSatisfy(twoScale(100- ((totalStatisfy/totalReview)*100))+"%");
                if (Float.parseFloat(yudingFillExcel.getSatisfy().substring(0, 4)) >= 99.7) {
                    yudingFillExcel.setSatisfyIsDone("已完成");
                    yudingFillExcel.setSatisfyDiff("/");
                } else {
                    yudingFillExcel.setSatisfyIsDone("未完成");
                    yudingFillExcel.setSatisfyDiff(twoScale(99.7 - Double.parseDouble(yudingFillExcel.getSatisfy().substring(0, 4))) + "%");
                }

                yudingDay.setStatisfy(twoScale(100- ((totalStatisfy/totalReview)*100)));

                //预订当日数据的一些字段
                double avgHour = Double.parseDouble(dataParams.getAvgHour());
                double totalNum = twoScale((incallAnswerNum + outcallNum) / ((avgHour-dataParams.getNotStatic())*3600) * 28800);
                yudingFillExcel.setAvgPhone(totalNum);
                if (totalNum>= 110.0) {
                    yudingFillExcel.setAvgPhoneIsDone("已完成");
                    yudingFillExcel.setAvgPhoneDiff("/");
                } else {
                    yudingFillExcel.setAvgPhoneIsDone("未完成");
                    yudingFillExcel.setAvgPhoneDiff(String.valueOf(twoScale(110.0 - (totalNum))));
                }
                yudingDay.setDayCall(totalNum);
                //月均电话量

                LambdaQueryWrapper<SysBusinessStatistics> dayqueryWrapper = new LambdaQueryWrapper<>();
                dayqueryWrapper.eq(SysBusinessStatistics::getServiceDate,beginTime.substring(0,10));
                List<SysBusinessStatistics> customerBusinessStatisticsList = sysBusinessStatisticsMapper.selectList(dayqueryWrapper);

                LambdaQueryWrapper<SysBusinessStatistics> MonthqueryWrapper = new LambdaQueryWrapper<>();
                MonthqueryWrapper.ge(SysBusinessStatistics::getServiceDate,startDate);//大于开始日期
                MonthqueryWrapper.le(SysBusinessStatistics::getServiceDate,endDate);//小于结束日期
                List<SysBusinessStatistics> monthCustomerBusinessStatisticsList = sysBusinessStatisticsMapper.selectList(MonthqueryWrapper);


                //客诉处理时长
                yudingFillExcel.setComplaintTime((double) (customerBusinessStatisticsList.get(0).getAvgTime() / 60));
                if (yudingFillExcel.getComplaintTime()<=6.7){
                    yudingFillExcel.setComplaintTimeIsDone("已完成");
                    yudingFillExcel.setComplaintTimeDiff("/");
                }else {
                    yudingFillExcel.setComplaintTimeIsDone("未完成");
                    yudingFillExcel.setComplaintTimeDiff(String.valueOf(twoScale(yudingFillExcel.getComplaintTime()-6.7)));
                }

                yudingFillExcel.setComplaintTimeMonthDone(twoScale(monthCustomerBusinessStatisticsList.stream()
                        .mapToDouble(SysBusinessStatistics::getAvgTime).average().getAsDouble() / 60));


                //人日均业务量
                double avgBusiness = Double.parseDouble(dataParams.getAvgBusiness());
                yudingFillExcel.setAvgBusiness(avgBusiness);
                if (yudingFillExcel.getAvgBusiness()>=34){
                    yudingFillExcel.setAvgBusinessIsDone("已完成");
                    yudingFillExcel.setAvgBusinessDiff("/");
                }else {
                    yudingFillExcel.setAvgBusinessIsDone("未完成");
                    yudingFillExcel.setAvgBusinessDiff(String.valueOf(34-yudingFillExcel.getAvgBusiness()));
                }
                yudingFillExcel.setAvgBusinessMonthDone(twoScale((monthYudingDayList.stream().mapToDouble(YudingDay::getDayBusiness).sum()+yudingFillExcel.getAvgBusiness())/(countDayCall+1)));

                yudingDay.setDayBusiness(avgBusiness);
                yudingDay.setDayAnswer(twoScale(AnswerRate));
                LambdaQueryWrapper<YudingDay> yudingDayLambdaQueryWrapper = new LambdaQueryWrapper<>();
                yudingDayLambdaQueryWrapper.eq(YudingDay::getDate,date);
                if (yudingDayMapper.selectOne(yudingDayLambdaQueryWrapper)==null){
                    yudingDayMapper.insert(yudingDay);
                }

                if (monthYudingDayList.size()==0){
                    yudingFillExcel.setAnswerRateMonthDone(twoScale((AnswerRate)/(countDayCall+1)) + "%");
                    yudingFillExcel.setSatisfyMonthDone(twoScale(((100-(statisfy/NoList.size())))/(countDayCall+1)) + "%");
                    yudingFillExcel.setAvgPhoneMonthDone(twoScale((yudingFillExcel.getAvgPhone())/(countDayCall+1)));
                }else {
                    yudingFillExcel.setAnswerRateMonthDone(twoScale((monthYudingDayList.stream().mapToDouble(YudingDay::getDayAnswer).sum()+AnswerRate)/(countDayCall+1)) + "%");
                    yudingFillExcel.setSatisfyMonthDone(twoScale((monthYudingDayList.stream().mapToDouble(YudingDay::getStatisfy).sum()+(100-(statisfy/NoList.size())))/(countDayCall+1)) + "%");
                    yudingFillExcel.setAvgPhoneMonthDone(twoScale((monthYudingDayList.stream().mapToDouble(YudingDay::getDayCall).sum()+yudingFillExcel.getAvgPhone())/(countDayCall+1)));
                }

                CrsData crs = localExcel.getCrs(beginTime.substring(0, 10));


                //预订当日电话量
                yudingFillExcel.setCallNumDay((int) (incallAnswer + outcall));
                yudingFillExcel.setCallNumUnDone(0);
                yudingFillExcel.setCallNumHistory(0);
                yudingFillExcel.setCallNumSum(yudingFillExcel.getCallNumDay());
                double chatMin = dataParams.getChatNum()*5;//企微登陆时长（分钟） = 企业微信总沟通数量*5
                double callPeopleNum = avgHour/8;//接线人数 = 微盘工作内容表里总工时/8
                double complaintNumDay = crs.getTotal();//当日客情录入数量 = 从数据里库里取
                yudingFillExcel.setCallNumMin((callPeopleNum*480)-chatMin-(complaintNumDay*3));
                yudingFillExcel.setCallNumAvgMin(twoScale(yudingFillExcel.getCallNumMin()/yudingFillExcel.getCallNumDay()));

                //客服业务量
                yudingFillExcel.setBusinessNumDay((int) (customerBusinessStatisticsList.get(0).getMisCount().doubleValue()+customerBusinessStatisticsList.get(0).getProblemsCount()));
                // crs 来电+客诉，录入时间为 前一天，状态为 未处理+处理中+待处理
                yudingFillExcel.setBusinessUnDone(crs.getBusinessUndone());
                // crs 来电+客诉，结束时间为 前2天，状态为 未处理+处理中
                yudingFillExcel.setBusinessHistory(crs.getBusinessHistory());
                yudingFillExcel.setBusinessNumSum((double) (yudingFillExcel.getBusinessNumDay()+yudingFillExcel.getBusinessUnDone())+yudingFillExcel.getBusinessHistory());
                yudingFillExcel.setBusinessMin(1440);
                yudingFillExcel.setBusinessAvgMin(twoScale(1440 / yudingFillExcel.getBusinessNumSum()));

                //企业微信沟通 从微盘读
                Integer chatNum = dataParams.getChatNum();
                yudingFillExcel.setChatSum(chatNum);
                yudingFillExcel.setCrossSectorNumDay(crs.getCrossSector());
                yudingFillExcel.setCrossSectorNumUnDone(crs.getCrossSectorUndone());
                yudingFillExcel.setCrossSectorNumHistory(crs.getCrossSectorHistory());
                yudingFillExcel.setCrossSectorNumSum(yudingFillExcel.getCrossSectorNumDay()+ yudingFillExcel.getCrossSectorNumUnDone()+ yudingFillExcel.getCrossSectorNumHistory());

                //合计
                yudingFillExcel.setSumDay(String.valueOf((int)(yudingFillExcel.getCallNumSum()+yudingFillExcel.getBusinessNumSum()+yudingFillExcel.getChatSum())));
                yudingFillExcel.setSumDayDone(String.valueOf(yudingFillExcel.getCallNumDay()+yudingFillExcel.getBusinessNumDay()+yudingFillExcel.getChatSum()));
                yudingFillExcel.setSumDayUnDone(String.valueOf(yudingFillExcel.getCallNumUnDone()+yudingFillExcel.getBusinessUnDone()));
                yudingFillExcel.setSumDayHis(String.valueOf(yudingFillExcel.getCallNumHistory()+yudingFillExcel.getBusinessHistory()));
                yudingFillExcel.setSumDayMin(String.valueOf((int)(yudingFillExcel.getCallNumMin()+yudingFillExcel.getBusinessMin())));
                yudingFillExcel.setSumDayAvgMin(String.valueOf((int)(yudingFillExcel.getCallNumAvgMin()+yudingFillExcel.getBusinessAvgMin())));

                //周工作计划及完成情况
                CustomerService customerServiceDay = new CustomerService();
                //客户投诉问题处理条数
                customerServiceDay.setComplaintPlan(100);
                customerServiceDay.setComplaintDone(customerBusinessStatisticsList.get(0).getProblemsCount());
                customerServiceDay.setComplaintRate((double) (customerServiceDay.getComplaintDone()/customerServiceDay.getComplaintPlan()));
                //客户投诉问题平均处理时长
                customerServiceDay.setTimePlan(6.7);
                customerServiceDay.setTimeDone(yudingFillExcel.getComplaintTime());
                customerServiceDay.setTimeStatus(yudingFillExcel.getComplaintTimeIsDone());
                //酒店责任客诉处理条数
                customerServiceDay.setOnHotelPlan(10);
                customerServiceDay.setOnHotelDone(customerBusinessStatisticsList.get(0).getRectifyCount());
                customerServiceDay.setOnHotelRate((double) (customerBusinessStatisticsList.get(0).getRectifyCount()/10));
                //酒店责任客诉整改单回复率
                customerServiceDay.setOnHotelResPlan(0.6);
                customerServiceDay.setOnHotelResDone(customerBusinessStatisticsList.get(0).getRectifyVisitRate().doubleValue());
                customerServiceDay.setOnHotelResRate(customerServiceDay.getOnHotelResDone()/0.6);
                //酒店MIS单处理
                customerServiceDay.setHotelMisPlan(70);
                customerServiceDay.setHotelMisDone(customerBusinessStatisticsList.get(0).getMisCount());
                customerServiceDay.setHotelMisRate((double) (customerServiceDay.getHotelMisDone()/70));

                LambdaQueryWrapper<CustomerService> customerServiceLambdaQueryWrapper = new LambdaQueryWrapper<>();



                //excel写在本地
                ExcelWriter excellocalWriter = EasyExcel.write("FilesYuding/" + "Yuding" + beginTime.substring(0, 10) + ".xlsx").withTemplate(templateFile).build();
                WriteSheet writeSheet = EasyExcel.writerSheet("分时呼损").build();
                WriteSheet writeSheet1 = EasyExcel.writerSheet("数据汇总").build();
                WriteSheet writeSheet2 = EasyExcel.writerSheet("预订组工作内容").build();
                WriteSheet writeSheet3 = EasyExcel.writerSheet("周工作计划及完成情况-客服组").build();
                WriteSheet writeSheet4 = EasyExcel.writerSheet("周工作计划及完成情况-预订组").build();
                excellocalWriter.fill(timeDataList, writeSheet);
                excellocalWriter.fill(yudingFillExcel, writeSheet1);
                excellocalWriter.fill(yudingDayDones, writeSheet2);
                excellocalWriter.finish();

                //excel返回给前端
                ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(templateFile).build();
                excelWriter.fill(timeDataList, writeSheet);
                excelWriter.fill(yudingFillExcel, writeSheet1);
                excelWriter.fill(yudingDayDones, writeSheet2);
                excelWriter.finish();
                LOGGER.info("下载了预订部日期为" + beginTime.substring(0, 10) + "的数据");
            }
        } else if (Objects.equals(action, "recall")) {
            if (Objects.equals(domain, "helpdesk.greentree.com")) {
                templateFile = "itRecord.xlsx";
                List<CallRecord> callRecordsList;
                try {

                    //获取所有记录
                    callRecordsList = callData.getReport(beginTime,endTime,domain,"callrecord");

                    //接通记录(含呼出，呼出取值为被叫去90的值)
                    HashMap<Object, Object> answerRecordMap = new HashMap<>();

                    for (CallRecord callRecord : callRecordsList) {
                        if (Objects.equals(callRecord.getHangupCause(),"4")) {
                            if (Objects.equals(callRecord.getCallType(),"1")){
                                answerRecordMap.put(callRecord.getCallee().substring(2), callRecord);
                            }else {
                                answerRecordMap.put(callRecord.getCaller(), callRecord);
                            }
                        }
                    }

                    //最后填充的列表
                    List<CallRecord> callRecordList = new ArrayList<>();

                    //未接通记录
                    HashMap<Object, Object> unAnswerRecordMap = new HashMap<>();

                    //呼损统计数量的类
                    CallRecordNum callRecordNum = new CallRecordNum();

                    for (CallRecord callRecord : callRecordsList) {
                        //所有的未接通的记录
                        if(!Objects.equals(callRecord.getHangupCause(),"4") && Objects.equals(callRecord.getCallType(),"2")){
                            //1、呼入放弃数
                            callRecordNum.setTotalNum(callRecordNum.getTotalNum()+1);
                            //未接通去重
                            if (!unAnswerRecordMap.containsKey(callRecord.getCaller())){
                                unAnswerRecordMap.put(callRecord.getCaller(),callRecord);
                                //未接通的电话在接通的记录里出现过的个数 即 2、当日已解决数
                                if (answerRecordMap.containsKey(callRecord.getCaller())){
                                    callRecordNum.setSolvedNum(callRecordNum.getSolvedNum()+1);
                                }
                                //所有的不在已接通记录里的未接通记录 即 4、需要回拨数
                                if(!answerRecordMap.containsKey(callRecord.getCaller()) ){
                                    callRecordNum.setToReCallNum(callRecordNum.getToReCallNum()+1);
                                    callRecordList.add(callRecord);
                                }
                            }else {
                                //3、重复未接数
                                callRecordNum.setRepeatNum(callRecordNum.getRepeatNum()+1);
                            }
                        }
                    }

                    //excel写在本地
                    ExcelWriter excellocalWriter = EasyExcel.write("FilesIt/"+"ItReCall"+beginTime.substring(0,10)+".xlsx").withTemplate(templateFile).build();
                    WriteSheet writeSheet =EasyExcel.writerSheet(0).build();
                    excellocalWriter.fill(callRecordList,writeSheet);
                    excellocalWriter.fill(callRecordNum,writeSheet);
                    excellocalWriter.finish();

                    //excel返回给前端
                    ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(templateFile).build();
                    excelWriter.fill(callRecordList,writeSheet);
                    excelWriter.fill(callRecordNum,writeSheet);
                    excelWriter.finish();
                    LOGGER.info("下载了实施组日期为"+beginTime.substring(0,10)+"的呼损回访数据");

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    @Override
    public void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception {
        System.out.println("进入了方法");
        String beginTime= dataParams.getBeginTime();
        String endTime= dataParams.getEndTime();
        String doMain= dataParams.getDoMain();
        String fileName="varianceDemo.xls";
        GetJsonArrayImpl getJsonArray =new GetJsonArrayImpl();
        int num = getJsonArray.getPage(beginTime,endTime);
        List<varianceData> datasList = getJsonArray.getJson(num,beginTime,endTime,doMain);
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, varianceData.class, new PageReadListener<varianceData>(dataList -> {
            ServletOutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<varData> varDataList = new ArrayList<>();
            List<avgData> avgDataList = new ArrayList<>();
            for (varianceData demoData : dataList) {
                List<varianceData> resultList = new ArrayList<>();

                for (varianceData datas : datasList) {
                    if (datas.getStartTime().substring(11).equals(demoData.getStartTime())) {
                        resultList.add(datas);
                    }
                }
                if (resultList.size() != 0) {
                    double avgIncallNum = resultList.stream().mapToDouble(varianceData::getIncallNum).average().getAsDouble();
                    double avgIncallAnswerNum = resultList.stream().mapToDouble(varianceData::getIncallAnswerNum).average().getAsDouble();
                    double avgOutcallNum = resultList.stream().mapToDouble(varianceData::getOutcallNum).average().getAsDouble();
                    double avgActualCallLoss = resultList.stream().mapToDouble(varianceData::getActualCallLoss).average().getAsDouble();
                    double avgFloatActualCallLossRate = resultList.stream().mapToDouble(varianceData::getFloatActualCallLossRate).average().getAsDouble();
                    double avgMaxLoginAgent = resultList.stream().mapToDouble(varianceData::getMaxLoginAgent).average().getAsDouble();
                    avgData avgdata = new avgData();
                    varData vardata = new varData();
                    vardata.setStartTime(demoData.getStartTime());
                    avgdata.setStartTime(demoData.getStartTime());
                    for (varianceData redata : resultList) {
                        vardata.setIncallNum(vardata.getIncallNum() + ((redata.getIncallNum() - avgIncallNum) * (redata.getIncallNum() - avgIncallNum) / num));
                        vardata.setIncallAnswerNum(vardata.getIncallAnswerNum() + ((redata.getIncallAnswerNum() - avgIncallAnswerNum) * (redata.getIncallAnswerNum() - avgIncallAnswerNum)) / num);
                        vardata.setOutcallNum(vardata.getOutcallNum() + ((redata.getOutcallNum() - avgOutcallNum) * (redata.getOutcallNum() - avgOutcallNum) / num));
                        vardata.setActualCallLoss(vardata.getActualCallLoss() + ((redata.getActualCallLoss() - avgActualCallLoss) * (redata.getActualCallLoss() - avgActualCallLoss) / num));
                        vardata.setFloatActualCallLossRate(vardata.getFloatActualCallLossRate() + ((redata.getFloatActualCallLossRate() - avgFloatActualCallLossRate) * (redata.getFloatActualCallLossRate() - avgFloatActualCallLossRate) / num));
                        vardata.setMaxLoginAgent(vardata.getMaxLoginAgent() + ((redata.getMaxLoginAgent() - avgMaxLoginAgent) * (redata.getMaxLoginAgent() - avgMaxLoginAgent) / num));
                        avgdata.setIncallNum(avgIncallNum);
                        avgdata.setIncallAnswerNum(avgIncallAnswerNum);
                        avgdata.setOutcallNum(avgOutcallNum);
                        avgdata.setActualCallLoss(avgActualCallLoss);
                        avgdata.setFloatActualCallLossRate(avgFloatActualCallLossRate);
                        avgdata.setMaxLoginAgent(avgMaxLoginAgent);
                    }
                    varDataList.add(vardata);
                    avgDataList.add(avgdata);

                }
            }
            String templateFile = "itAvg.xls";
            ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(templateFile).build();
            WriteSheet writeSheet =EasyExcel.writerSheet(0).build();
            WriteSheet writeSheet1 =EasyExcel.writerSheet(2).build();
            excelWriter.fill(avgDataList,writeSheet);
            excelWriter.fill(varDataList,writeSheet1);
            excelWriter.finish();
        })).sheet().doRead();
    }

    public List<String> getNoName(String beginTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        List<CsTableYuding> csTableYudingList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", CsTableYuding.class,new PageReadListener<CsTableYuding>(datList->{
            for (CsTableYuding csTableYuding : datList){
                if (Objects.equals(csTableYuding.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(csTableYuding.getNo()) || Objects.equals(csTableYuding.getNo(),"远程")){
                    csTableYuding.setNo("坐席号未知");
                }
                csTableYudingList.add(csTableYuding);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                    if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSunday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSunday().contains("休")&&!csTableYuding.getSaturday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 2) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getMonday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getMonday().contains("休")&&!csTableYuding.getTemp().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 3) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                boolean night = csTableYuding.getTuesday().contains("休")&&!csTableYuding.getMonday().contains("晚班");
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getTuesday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || night){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 4) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getWednesday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getWednesday().contains("休")&&!csTableYuding.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 5) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getThursday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getThursday().contains("休")&&!csTableYuding.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 6) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getFriday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getFriday().contains("休")&&!csTableYuding.getThursday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 7) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSaturday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSaturday().contains("休")&&!csTableYuding.getFriday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        }
        return NoList;
    }
    public List<String> getNight(String beginTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        List<CsTableYuding> csTableYudingList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", CsTableYuding.class,new PageReadListener<CsTableYuding>(datList->{
            for (CsTableYuding csTableYuding : datList){
                if (Objects.equals(csTableYuding.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(csTableYuding.getNo()) || Objects.equals(csTableYuding.getNo(),"远程")){
                    csTableYuding.setNo("坐席号未知");
                }
                csTableYudingList.add(csTableYuding);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                    if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSunday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSunday().contains("休")&&!csTableYuding.getSaturday().contains("晚班"))){
                    continue;
                }else {
                        if ((csTableYuding.getSunday().contains("休")&&csTableYuding.getSaturday().contains("晚班")) || csTableYuding.getSunday().contains("晚班")){
                            NoList.add(csTableYuding.getNo());
                        }
                }
            }
        } else if (weekday == 2) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getMonday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getMonday().contains("休")&&!csTableYuding.getTemp().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getMonday().contains("休")&&csTableYuding.getTemp().contains("晚班")) || csTableYuding.getMonday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 3) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                boolean night = csTableYuding.getTuesday().contains("休")&&!csTableYuding.getMonday().contains("晚班");
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getTuesday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || night){
                    continue;
                }else {
                    if ((csTableYuding.getTuesday().contains("休")&&csTableYuding.getMonday().contains("晚班")) || csTableYuding.getTuesday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 4) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getWednesday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getWednesday().contains("休")&&!csTableYuding.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getWednesday().contains("休")&&csTableYuding.getTuesday().contains("晚班")) || csTableYuding.getWednesday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 5) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getThursday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getThursday().contains("休")&&!csTableYuding.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getThursday().contains("休")&&csTableYuding.getWednesday().contains("晚班")) || csTableYuding.getThursday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 6) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getFriday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getFriday().contains("休")&&!csTableYuding.getThursday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getFriday().contains("休")&&csTableYuding.getThursday().contains("晚班")) || csTableYuding.getFriday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 7) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSaturday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSaturday().contains("休")&&!csTableYuding.getFriday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getSaturday().contains("休")&&csTableYuding.getFriday().contains("晚班")) || csTableYuding.getSaturday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        }
        return NoList;
    }

}
