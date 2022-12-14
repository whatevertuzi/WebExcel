package com.zcy.webexcel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.mail.util.MailSSLSocketFactory;
import com.zcy.webexcel.Component.GetVariance;
import com.zcy.webexcel.DaoSys.pojo.CustomerService;
import com.zcy.webexcel.DaoSys.pojo.SysBusinessStatistics;
import com.zcy.webexcel.DaoSys.pojo.YudingDay;
import com.zcy.webexcel.DaoSys.mapper.SysBusinessStatisticsMapper;
import com.zcy.webexcel.DaoSys.mapper.YudingDayMapper;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.pojo.LaiHuSys.SkillHourData;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.vo.ResultTool;
import com.zcy.webexcel.vo.YudingFillExcel;
import com.zcy.webexcel.Utils.Base2ImgUtil;
import com.zcy.webexcel.pojo.LaiHuSys.AgentReport;
import com.zcy.webexcel.pojo.LaiHuSys.AgentSatisfy;
import com.zcy.webexcel.pojo.LaiHuSys.HourData;
import com.zcy.webexcel.service.GetCrsDataService;
import com.zcy.webexcel.Utils.WriteImgUtil;
import com.zcy.webexcel.service.BookingService;
import com.zcy.webexcel.service.GetLaiHuDataService;
import com.zcy.webexcel.vo.CrsData;
import com.zcy.webexcel.vo.YudingDayDone;
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
    private final GetCrsDataService getCrsDataService;
    //Mapper
    private final SysBusinessStatisticsMapper sysBusinessStatisticsMapper;
    private final YudingDayMapper yudingDayMapper;

    public BookingServiceImpl(GetLaiHuDataService getLaiHuDataService,GetCrsDataService getCrsDataService, SysBusinessStatisticsMapper sysBusinessStatisticsMapper, YudingDayMapper yudingDayMapper) {
        this.getLaiHuDataService = getLaiHuDataService;
        this.getCrsDataService = getCrsDataService;
        this.sysBusinessStatisticsMapper = sysBusinessStatisticsMapper;
        this.yudingDayMapper = yudingDayMapper;
    }

    private Double twoScale(Double number){
        if (number == null)
            return null;
        //??????BigDecimal?????????????????????.??????????????????
        //1????????????1?????????,????????????????????????2,????????????
        //BigDecimal.ROUND_HALF_UP ?????????????????????????????????
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
        //????????????
        String templateFile = "demo_fill.xlsx";
        List<HourData> timeDataList;
        List<AgentSatisfy> satisfyList;//???????????????
        List<AgentReport> agentReportList;//??????????????????
        List<SkillHourData> skillHourDataList;//201?????????????????????

        //?????????????????????
        try {

            //??????????????????
            timeDataList = getLaiHuDataService.getCallReportHour(beginTime, endTime, domain, "callreport_hour");

            //?????????????????????
            satisfyList = getLaiHuDataService.getSatisfactionStatistics(beginTime.substring(0, 10), beginTime.substring(0, 10), domain, "satisfactionStatistics");

            //????????????????????????
            agentReportList = getLaiHuDataService.getAgentReport(beginTime.substring(0, 10), beginTime.substring(0,10), domain,"agentreport");

            //????????????203?????????????????????
            skillHourDataList = getLaiHuDataService.getSkillReportHour(beginTime,endTime,domain,"skillreport_hour");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //???????????????
        double allAcd = timeDataList.stream().mapToInt(HourData::getIncallNum).sum();
        System.out.println("??????????????????????????????"+allAcd);
        double allLoss = timeDataList.stream().mapToInt(HourData::getActualCallLoss).sum();
        System.out.println("??????????????????????????????"+allLoss);
        double hotelAcd = skillHourDataList.stream().mapToDouble(SkillHourData::getIncallNum).sum();
        System.out.println("203?????????????????????????????????"+hotelAcd);
        double hotelLoss = skillHourDataList.stream().mapToDouble(SkillHourData::getAbandoncallNum).sum();
        System.out.println("203?????????????????????????????????"+hotelLoss);
        double abandonRate = (allLoss-hotelLoss)/(allAcd-hotelAcd) ;
        double AnswerRate = 100-(abandonRate*100);
        YudingFillExcel yudingFillExcel = new YudingFillExcel();

        //????????????
        yudingFillExcel.setTitle(beginTime.substring(5, 7) + '/' + beginTime.substring(8, 10) + "??????????????????????????????????????????");

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
        System.out.println("???????????????"+totalStatisfy);
        System.out.println("????????????"+totalReview);
        System.out.println(NoList);
        System.out.println("????????????"+incallAnswer);
        System.out.println("????????????"+outcall);


        YudingDay yudingDay = new YudingDay();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        yudingDay.setDate(date);
        yudingDay.setDayCall(yudingFillExcel.getAvgPhone());
        Date startDate = format.parse(beginTime.substring(0,8)+"01");
        Date endDate = format.parse(beginTime.substring(0,10));
        LambdaQueryWrapper<YudingDay> monthYudingDayLambdaQueryWrapper = new LambdaQueryWrapper<>();
        monthYudingDayLambdaQueryWrapper.ge(YudingDay::getDate,startDate);//??????????????????
        monthYudingDayLambdaQueryWrapper.le(YudingDay::getDate,endDate);//??????????????????
        List<YudingDay> monthYudingDayList = yudingDayMapper.selectList(monthYudingDayLambdaQueryWrapper);
        int countDayCall = monthYudingDayList.size();

        //400????????????
        yudingFillExcel.setAnswerRate(twoScale(AnswerRate) + "%");
        if (AnswerRate > 97.2) {
            yudingFillExcel.setAnswerRateIsDone("?????????");
            yudingFillExcel.setAnswerRateDiff("/");
        } else {
            yudingFillExcel.setAnswerRateIsDone("?????????");
            yudingFillExcel.setAnswerRateDiff(twoScale(97.20 - AnswerRate) + "%");
        }

        //400????????????
        yudingFillExcel.setSatisfy(twoScale(100- ((totalStatisfy/totalReview)*100))+"%");
        if (Float.parseFloat(yudingFillExcel.getSatisfy().substring(0, 4)) >= 99.7) {
            yudingFillExcel.setSatisfyIsDone("?????????");
            yudingFillExcel.setSatisfyDiff("/");
        } else {
            yudingFillExcel.setSatisfyIsDone("?????????");
            yudingFillExcel.setSatisfyDiff(twoScale(99.7 - Double.parseDouble(yudingFillExcel.getSatisfy().substring(0, 4))) + "%");
        }

        yudingDay.setStatisfy(twoScale(100- ((totalStatisfy/totalReview)*100)));

        //?????????????????????????????????
        double avgHour = Double.parseDouble(dataParams.getAvgHour());
        double totalNum = twoScale((incallAnswerNum + outcallNum) / ((avgHour-dataParams.getNotStatic())*3600) * 28800);
        yudingFillExcel.setAvgPhone(totalNum);
        if (totalNum>= 110.0) {
            yudingFillExcel.setAvgPhoneIsDone("?????????");
            yudingFillExcel.setAvgPhoneDiff("/");
        } else {
            yudingFillExcel.setAvgPhoneIsDone("?????????");
            yudingFillExcel.setAvgPhoneDiff(String.valueOf(twoScale(110.0 - (totalNum))));
        }
        yudingDay.setDayCall(totalNum);
        //???????????????

        LambdaQueryWrapper<SysBusinessStatistics> dayqueryWrapper = new LambdaQueryWrapper<>();
        dayqueryWrapper.eq(SysBusinessStatistics::getServiceDate,beginTime.substring(0,10));
        List<SysBusinessStatistics> customerBusinessStatisticsList = sysBusinessStatisticsMapper.selectList(dayqueryWrapper);

        LambdaQueryWrapper<SysBusinessStatistics> MonthqueryWrapper = new LambdaQueryWrapper<>();
        MonthqueryWrapper.ge(SysBusinessStatistics::getServiceDate,startDate);//??????????????????
        MonthqueryWrapper.le(SysBusinessStatistics::getServiceDate,endDate);//??????????????????
        List<SysBusinessStatistics> monthCustomerBusinessStatisticsList = sysBusinessStatisticsMapper.selectList(MonthqueryWrapper);


        //??????????????????
        yudingFillExcel.setComplaintTime((double) (customerBusinessStatisticsList.get(0).getAvgTime() / 60));
        if (yudingFillExcel.getComplaintTime()<=6.7){
            yudingFillExcel.setComplaintTimeIsDone("?????????");
            yudingFillExcel.setComplaintTimeDiff("/");
        }else {
            yudingFillExcel.setComplaintTimeIsDone("?????????");
            yudingFillExcel.setComplaintTimeDiff(String.valueOf(twoScale(yudingFillExcel.getComplaintTime()-6.7)));
        }

        yudingFillExcel.setComplaintTimeMonthDone(twoScale(monthCustomerBusinessStatisticsList.stream()
                .mapToDouble(SysBusinessStatistics::getAvgTime).average().getAsDouble() / 60));


        //??????????????????
        double avgBusiness = Double.parseDouble(dataParams.getAvgBusiness());
        yudingFillExcel.setAvgBusiness(avgBusiness);
        if (yudingFillExcel.getAvgBusiness()>=34){
            yudingFillExcel.setAvgBusinessIsDone("?????????");
            yudingFillExcel.setAvgBusinessDiff("/");
        }else {
            yudingFillExcel.setAvgBusinessIsDone("?????????");
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

        //?????????????????????
        yudingFillExcel.setCallNumDay((int) (incallAnswer + outcall));
        yudingFillExcel.setCallNumUnDone(0);
        yudingFillExcel.setCallNumHistory(0);
        yudingFillExcel.setCallNumSum(yudingFillExcel.getCallNumDay());
        double chatMin = dataParams.getChatNum()*5;//?????????????????????????????? = ???????????????????????????*5
        double callPeopleNum = avgHour/8;//???????????? = ?????????????????????????????????/8
        double complaintNumDay = crs.getTotal();//???????????????????????? = ?????????????????????
        yudingFillExcel.setCallNumMin((callPeopleNum*480)-chatMin-(complaintNumDay*3));
        yudingFillExcel.setCallNumAvgMin(twoScale(yudingFillExcel.getCallNumMin()/yudingFillExcel.getCallNumDay()));

        //???????????????
        yudingFillExcel.setBusinessNumDay((int) (customerBusinessStatisticsList.get(0).getMisCount().doubleValue()+customerBusinessStatisticsList.get(0).getProblemsCount()));
        // crs ??????+???????????????????????? ????????????????????? ?????????+?????????+?????????
        yudingFillExcel.setBusinessUnDone(crs.getBusinessUndone());
        // crs ??????+???????????????????????? ???2??????????????? ?????????+?????????
        yudingFillExcel.setBusinessHistory(crs.getBusinessHistory());
        yudingFillExcel.setBusinessNumSum((double) (yudingFillExcel.getBusinessNumDay()+yudingFillExcel.getBusinessUnDone())+yudingFillExcel.getBusinessHistory());
        yudingFillExcel.setBusinessMin(1440);
        yudingFillExcel.setBusinessAvgMin(twoScale(1440 / yudingFillExcel.getBusinessNumSum()));

        //?????????????????? ????????????
        Integer chatNum = dataParams.getChatNum();
        yudingFillExcel.setChatSum(chatNum);
        yudingFillExcel.setCrossSectorNumDay(crs.getCrossSector());
        yudingFillExcel.setCrossSectorNumUnDone(crs.getCrossSectorUndone());
        yudingFillExcel.setCrossSectorNumHistory(crs.getCrossSectorHistory());
        yudingFillExcel.setCrossSectorNumSum(yudingFillExcel.getCrossSectorNumDay()+ yudingFillExcel.getCrossSectorNumUnDone()+ yudingFillExcel.getCrossSectorNumHistory());

        //??????
        yudingFillExcel.setSumDay(String.valueOf((int)(yudingFillExcel.getCallNumSum()+yudingFillExcel.getBusinessNumSum()+yudingFillExcel.getChatSum())));
        yudingFillExcel.setSumDayDone(String.valueOf(yudingFillExcel.getCallNumDay()+yudingFillExcel.getBusinessNumDay()+yudingFillExcel.getChatSum()));
        yudingFillExcel.setSumDayUnDone(String.valueOf(yudingFillExcel.getCallNumUnDone()+yudingFillExcel.getBusinessUnDone()));
        yudingFillExcel.setSumDayHis(String.valueOf(yudingFillExcel.getCallNumHistory()+yudingFillExcel.getBusinessHistory()));
        yudingFillExcel.setSumDayMin(String.valueOf((int)(yudingFillExcel.getCallNumMin()+yudingFillExcel.getBusinessMin())));
        yudingFillExcel.setSumDayAvgMin(String.valueOf((int)(yudingFillExcel.getCallNumAvgMin()+yudingFillExcel.getBusinessAvgMin())));

        //??????????????????????????????
        CustomerService customerServiceDay = new CustomerService();
        //??????????????????????????????
        customerServiceDay.setComplaintPlan(100);
        customerServiceDay.setComplaintDone(customerBusinessStatisticsList.get(0).getProblemsCount());
        customerServiceDay.setComplaintRate((double) (customerServiceDay.getComplaintDone()/customerServiceDay.getComplaintPlan()));
        //????????????????????????????????????
        customerServiceDay.setTimePlan(6.7);
        customerServiceDay.setTimeDone(yudingFillExcel.getComplaintTime());
        customerServiceDay.setTimeStatus(yudingFillExcel.getComplaintTimeIsDone());
        //??????????????????????????????
        customerServiceDay.setOnHotelPlan(10);
        customerServiceDay.setOnHotelDone(customerBusinessStatisticsList.get(0).getRectifyCount());
        customerServiceDay.setOnHotelRate((double) (customerBusinessStatisticsList.get(0).getRectifyCount()/10));
        //????????????????????????????????????
        customerServiceDay.setOnHotelResPlan(0.6);
        customerServiceDay.setOnHotelResDone(customerBusinessStatisticsList.get(0).getRectifyVisitRate().doubleValue());
        customerServiceDay.setOnHotelResRate(customerServiceDay.getOnHotelResDone()/0.6);
        //??????MIS?????????
        customerServiceDay.setHotelMisPlan(70);
        customerServiceDay.setHotelMisDone(customerBusinessStatisticsList.get(0).getMisCount());
        customerServiceDay.setHotelMisRate((double) (customerServiceDay.getHotelMisDone()/70));

        //excel????????????
        WriteSheet writeSheet;
        WriteSheet writeSheet1;
        WriteSheet writeSheet2;
        try (ExcelWriter excellocalWriter = EasyExcel.write("FilesYuding/" + "Yuding" + beginTime.substring(0, 10) + ".xlsx").withTemplate(templateFile).build()) {
            writeSheet = EasyExcel.writerSheet("????????????").build();
            writeSheet1 = EasyExcel.writerSheet("????????????").build();
            writeSheet2 = EasyExcel.writerSheet("?????????????????????").build();
            WriteSheet writeSheet3 = EasyExcel.writerSheet("??????????????????????????????-?????????").build();
            WriteSheet writeSheet4 = EasyExcel.writerSheet("??????????????????????????????-?????????").build();
            excellocalWriter.fill(timeDataList, writeSheet);
            excellocalWriter.fill(yudingFillExcel, writeSheet1);
            excellocalWriter.fill(yudingDayDones, writeSheet2);
            excellocalWriter.finish();
        }

        //excel???????????????
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
        itJpgPath = "FilesIt/Yuding" + beginTime.substring(0, 10) + ".jpg";
        try{
            Base2ImgUtil.generateImg(bytes,itJpgPath);
            WriteImgUtil.addImageToExcel(new File("FilesIt/Yuding" + beginTime.substring(0, 10) + ".xlsx"), 1, bytes,(short) 0, 11, (short) 7, 27);
        }catch(Exception e){
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }

        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");
        prop.setProperty("mail.port", "465");
        prop.setProperty("mail.transport.protocol", "smtp"); // ??????????????????
        prop.setProperty("mail.smtp.auth", "true"); // ???????????????????????????

        // QQ????????????SSL??????
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //1????????????????????????????????????????????????????????? Session ??????
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //????????????????????????????????????
                return new PasswordAuthentication("2365118193@QQ.com","hzmokzjqypbbeadg");
            }
        });

        //2?????????session??????transport??????
        Transport transport = session.getTransport();

        //3?????????transport??????????????????????????????????????????????????????
        transport.connect("smtp.qq.com","2365118193@QQ.com","hzmokzjqypbbeadg");

        //?????????????????????
        MimeMessage mimeMessage = new MimeMessage(session);

        //?????????
        mimeMessage.setFrom(new InternetAddress("2365118193@qq.com","IT"));

        //?????????
        InternetAddress zhuwentao = new InternetAddress("zhuwentao@998.com","?????????");
        InternetAddress shenming = new InternetAddress("shenming@998.com","??????");
        mimeMessage.setRecipient(Message.RecipientType.TO,zhuwentao);
        mimeMessage.setRecipient(Message.RecipientType.CC,shenming);

        MimeBodyPart text = new MimeBodyPart();
        MimeBodyPart appendix = new MimeBodyPart();
        MimeBodyPart image = new MimeBodyPart();
        //????????????
        mimeMessage.setSubject(beginTime.substring(0,10)+"??????-?????????");

        //????????????
        DataHandler dh = new DataHandler(new FileDataSource("FilesYuding/Yuding"+beginTime.substring(0,10)+".jpg")); // ??????????????????
        image.setDataHandler(dh);		            // ????????????????????????????????????
        image.setContentID("image_fairy_tail");	    // ???????????????????????????????????????????????????????????????????????????ID???

        //????????????
        text.setContent(beginTime.substring(0,10)+"??????-?????????<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");

        //??????
        String fileName = "Yuding"+beginTime.substring(0,10)+".xlsx";
        appendix.setDataHandler(new DataHandler(new FileDataSource("FilesYuding/"+fileName)));
        appendix.setFileName(fileName);

        //??????????????????
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(text);
        mimeMultipart.addBodyPart(image);
        mimeMultipart.setSubType("related");//??????????????????

        //??????????????????????????????????????????
        MimeBodyPart contentText = new MimeBodyPart();
        contentText.setContent(mimeMultipart);

        //????????????
        MimeMultipart allFile = new MimeMultipart();
        allFile.addBodyPart(appendix);//??????
        allFile.addBodyPart(contentText);//??????
        allFile.setSubType("mixed"); //?????????????????????????????????????????????????????????mixed


        //??????Message?????????
        mimeMessage.setContent(allFile);
        mimeMessage.saveChanges();//????????????

        //5???????????????
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

        //6???????????????
        transport.close();

        return null;
    }
}
