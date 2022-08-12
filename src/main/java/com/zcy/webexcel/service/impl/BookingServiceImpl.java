package com.zcy.webexcel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.mail.util.MailSSLSocketFactory;
import com.zcy.webexcel.Component.GetVariance;
import com.zcy.webexcel.DaoSys.CustomerService;
import com.zcy.webexcel.DaoSys.SysBusinessStatistics;
import com.zcy.webexcel.DaoSys.YudingDay;
import com.zcy.webexcel.DaoSys.mapper.SysBusinessStatisticsMapper;
import com.zcy.webexcel.DaoSys.mapper.YudingDayMapper;
import com.zcy.webexcel.DaoSys.pojo.*;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;
import com.zcy.webexcel.DaoSys.vo.ResultTool;
import com.zcy.webexcel.DaoSys.vo.YudingFillExcel;
import com.zcy.webexcel.Utils.Base2ImgUtil;
import com.zcy.webexcel.service.GetCrsDataService;
import com.zcy.webexcel.service.GetLocalDataService;
import com.zcy.webexcel.Utils.WriteImgUtil;
import com.zcy.webexcel.service.BookingService;
import com.zcy.webexcel.service.GetLaiHuDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zcy.webexcel.Utils.GetListUtil.getNight;
import static com.zcy.webexcel.Utils.GetListUtil.getNoName;

@Service
public class BookingServiceImpl implements BookingService {

    private final GetLaiHuDataService getLaiHuDataService;
    private final GetLocalDataService getLocalDataService;
    private final GetCrsDataService getCrsDataService;

    //Mapper
    private final SysBusinessStatisticsMapper sysBusinessStatisticsMapper;
    private final YudingDayMapper yudingDayMapper;

    public BookingServiceImpl(GetLaiHuDataService getLaiHuDataService, GetLocalDataService getLocalDataService, GetCrsDataService getCrsDataService, SysBusinessStatisticsMapper sysBusinessStatisticsMapper, YudingDayMapper yudingDayMapper) {
        this.getLaiHuDataService = getLaiHuDataService;
        this.getLocalDataService = getLocalDataService;
        this.getCrsDataService = getCrsDataService;
        this.sysBusinessStatisticsMapper = sysBusinessStatisticsMapper;
        this.yudingDayMapper = yudingDayMapper;
    }

    private Double twoScale(Double number){
        if (number == null)
            return null;
        //利用BigDecimal来实现四舍五入.保留一位小数
        //1代表保留1位小数,保留两位小数就是2,依此累推
        //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
        return new BigDecimal(number).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * @param response
     * @param dataParams
     */
    @Override
    public void getDaily(HttpServletResponse response, DataParams dataParams) throws Exception {
        String domain =dataParams.getDoMain();
        String beginTime = dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();

        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();
        //模板路径
        String templateFile = "demo_fill.xlsx";
        List<HourData> timeDataList;
        List<AgentSatisfy> satisfyList;//当日满意率
        List<AgentReport> agentReportList;//坐席统计报表
        List<SkillHourData> skillHourDataList;//201技能组分时报表

        //从来虎获取数据
        try {

            //预订分时数据
            timeDataList = getLaiHuDataService.getCallReportHour(beginTime, endTime, domain, "callreport_hour");

            //预订当日满意率
            satisfyList = getLaiHuDataService.getSatisfactionStatistics(beginTime.substring(0, 10), beginTime.substring(0, 10), domain, "satisfactionStatistics");

            //预订坐席统计报表
            agentReportList = getLaiHuDataService.getAgentReport(beginTime.substring(0, 10), beginTime.substring(0,10), domain,"agentreport");

            //预订当日203技能组分时报表
            skillHourDataList = getLaiHuDataService.getSkillReportHour(beginTime,endTime,domain,"skillreport_hour");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //计算接通率
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
        System.out.println(LastList);
        Double incallAnswerNum = 0.00;
        Double outcallNum = 0.00;
        Double incallAnswer = 0.00;
        Double outcall = 0.00;
        double statisfy =0.00;
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
            for (AgentSatisfy agentSatisfy : satisfyList) {
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

        CrsData crs = getCrsDataService.getComplaint(beginTime.substring(0, 10));

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

        //excel写在本地
        WriteSheet writeSheet;
        WriteSheet writeSheet1;
        WriteSheet writeSheet2;
        try (ExcelWriter excellocalWriter = EasyExcel.write("FilesYuding/" + "Yuding" + beginTime.substring(0, 10) + ".xlsx").withTemplate(templateFile).build()) {
            writeSheet = EasyExcel.writerSheet("分时呼损").build();
            writeSheet1 = EasyExcel.writerSheet("数据汇总").build();
            writeSheet2 = EasyExcel.writerSheet("预订组工作内容").build();
            WriteSheet writeSheet3 = EasyExcel.writerSheet("周工作计划及完成情况-客服组").build();
            WriteSheet writeSheet4 = EasyExcel.writerSheet("周工作计划及完成情况-预订组").build();
            excellocalWriter.fill(timeDataList, writeSheet);
            excellocalWriter.fill(yudingFillExcel, writeSheet1);
            excellocalWriter.fill(yudingDayDones, writeSheet2);
            excellocalWriter.finish();
        }

        //excel返回给前端
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(templateFile).build()) {
            excelWriter.fill(timeDataList, writeSheet);
            excelWriter.fill(yudingFillExcel, writeSheet1);
            excelWriter.fill(yudingDayDones, writeSheet2);
            excelWriter.finish();
        }
    }

    @Value("${templateFile.varianceDemo.path.dev}")
    String variancePath;
    @Value("${templateFile.itAvg.path.dev}")
    String itAvgPath;
    /**
     * @param response
     * @param dataParams
     */
    @Override
    public void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception {
        String beginTime= dataParams.getBeginTime();
        String endTime= dataParams.getEndTime();
        int num = getLaiHuDataService.getPage(beginTime,endTime,"yuding.greentree.com","/report/rest/callreport_hour/querynum");
        List<HourData> datasList = getLaiHuDataService.getCallReportHour(beginTime,endTime,"yuding.greentree.com","callreport_hour");
        GetVariance.getVariance(response, num, datasList, variancePath, itAvgPath);
    }

    /**
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public JsonResult<List<HourData>> getDatas(String beginTime, String endTime) throws Exception {
        return ResultTool.success(getLaiHuDataService.getCallReportHour(beginTime, endTime, "yuding.greentree.com" ,"callreport_hour"));
    }

    /**
     * @param
     * @param
     * @return
     */
    @Override
    public JsonResult<ResultCode> sendEmail(JSONObject jsonObject) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {
        String imageBase = String.valueOf(jsonObject.get("imageBase64"));
        String beginTime = (String) jsonObject.get("beginTime");
        byte[] bytes= Base64.getDecoder().decode(imageBase.substring(23));
        String itJpgPath;
        itJpgPath = "FilesIt/IT" + beginTime.substring(0, 10) + ".jpg";
        try{
            Base2ImgUtil.generateImg(bytes,itJpgPath);
            WriteImgUtil.addImageToExcel(new File("FilesIt/IT" + beginTime.substring(0, 10) + ".xlsx"), 1, bytes,(short) 0, 11, (short) 7, 27);
        }catch(Exception e){
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }

        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");
        prop.setProperty("mail.port", "465");
        prop.setProperty("mail.transport.protocol", "smtp"); // 邮件发送协议
        prop.setProperty("mail.smtp.auth", "true"); // 需要验证用户名密码

        // QQ邮箱设置SSL加密
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //1、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //传入发件人的姓名和授权码
                return new PasswordAuthentication("2365118193@QQ.com","hzmokzjqypbbeadg");
            }
        });

        //2、通过session获取transport对象
        Transport transport = session.getTransport();

        //3、通过transport对象邮箱用户名和授权码连接邮箱服务器
        transport.connect("smtp.qq.com","2365118193@QQ.com","hzmokzjqypbbeadg");

        //消息的固定信息
        MimeMessage mimeMessage = new MimeMessage(session);

        //发件人
        mimeMessage.setFrom(new InternetAddress("2365118193@qq.com","IT"));

        //收件人
        InternetAddress zhuwentao = new InternetAddress("zhuwentao@998.com","朱文涛");
        InternetAddress shenming = new InternetAddress("shenming@998.com","沈鸣");
        mimeMessage.setRecipient(Message.RecipientType.TO,zhuwentao);
        mimeMessage.setRecipient(Message.RecipientType.CC,shenming);

        MimeBodyPart text = new MimeBodyPart();
        MimeBodyPart appendix = new MimeBodyPart();
        MimeBodyPart image = new MimeBodyPart();
        //邮件标题
        mimeMessage.setSubject(beginTime.substring(0,10)+"日报-预订部");

        //准备图片
        DataHandler dh = new DataHandler(new FileDataSource("FilesYuding/Yuding"+beginTime.substring(0,10)+".jpg")); // 读取本地文件
        image.setDataHandler(dh);		            // 将图片数据添加到“节点”
        image.setContentID("image_fairy_tail");	    // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）

        //准备文本
        text.setContent(beginTime.substring(0,10)+"日报-预订部<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");

        //附件
        String fileName = "Yuding"+beginTime.substring(0,10)+".xlsx";
        appendix.setDataHandler(new DataHandler(new FileDataSource("FilesYuding/"+fileName)));
        appendix.setFileName(fileName);

        //拼装邮件正文
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(text);
        mimeMultipart.addBodyPart(image);
        mimeMultipart.setSubType("related");//文本内嵌成功

        //将拼装好的正文内容设置为主体
        MimeBodyPart contentText = new MimeBodyPart();
        contentText.setContent(mimeMultipart);

        //拼接附件
        MimeMultipart allFile = new MimeMultipart();
        allFile.addBodyPart(appendix);//附件
        allFile.addBodyPart(contentText);//正文
        allFile.setSubType("mixed"); //正文和附件都存在邮件中，所有类型设置为mixed


        //放到Message消息中
        mimeMessage.setContent(allFile);
        mimeMessage.saveChanges();//保存修改

        //5、发送邮件
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

        //6、关闭连接
        transport.close();

        return null;
    }
}
