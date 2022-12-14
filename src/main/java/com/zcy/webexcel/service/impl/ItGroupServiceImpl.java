package com.zcy.webexcel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.mail.util.MailSSLSocketFactory;
import com.zcy.webexcel.Component.GetVariance;
import com.zcy.webexcel.DaoSys.pojo.LocalData;
import com.zcy.webexcel.pojo.DailyData;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.pojo.LaiHuSys.*;
import com.zcy.webexcel.pojo.LocalData.DemoData;
import com.zcy.webexcel.vo.ItFillExcel;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.vo.ResultTool;
import com.zcy.webexcel.service.GetLocalDataService;
import com.zcy.webexcel.Utils.WriteImgUtil;
import com.zcy.webexcel.service.GetLaiHuDataService;
import com.zcy.webexcel.service.ItGroupService;
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
import java.util.*;

@Service
public class ItGroupServiceImpl implements ItGroupService {

    private final GetLaiHuDataService getLaiHuDataService;
    private final GetLocalDataService getLocalDataService;

    public ItGroupServiceImpl(GetLaiHuDataService getLaiHuDataService, GetLocalDataService getLocalDataService) {
        this.getLaiHuDataService = getLaiHuDataService;
        this.getLocalDataService = getLocalDataService;
    }

    private Double twoScale(Double number){
        if (number == null)
            return null;
        //??????BigDecimal?????????????????????.??????????????????
        //1????????????1?????????,????????????????????????2,????????????
        //BigDecimal.ROUND_HALF_UP ?????????????????????????????????
        return new BigDecimal(number).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Value("${templateFile.itFill.path.dev}")
    String itFillPath;
    /**
     * @param response
     * @param dataParams
     * @throws Exception
     */
    @Override
    public void getDaily(HttpServletResponse response, DataParams dataParams) throws Exception {
        String beginTime = dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();

        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();
        DailyData daily = getDailyData(beginTime, endTime);
        List<HourData> timeDataList = daily.getHourDataList();
        ItFillExcel itFillExcel =daily.getItFillExcel();
        //???????????????excel
        ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(itFillPath).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("????????????").build();//????????????????????????
        WriteSheet writeSheet1 = EasyExcel.writerSheet("????????????").build();//??????
        excelWriter.fill(timeDataList, writeSheet);
        excelWriter.fill(itFillExcel, writeSheet1);
        excelWriter.finish();
    }
    @Override
    public void getDaily(String beginTime, String endTime) throws Exception {
        DailyData daily = getDailyData(beginTime, endTime);
        List<HourData> timeDataList = daily.getHourDataList();
        ItFillExcel itFillExcel =daily.getItFillExcel();

        //excel????????????
        ExcelWriter excellocalWriter = EasyExcel.write(  "FilesIt/"+"IT" + beginTime.substring(0, 10) + ".xlsx").withTemplate(itFillPath).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("????????????").build();//????????????????????????
        WriteSheet writeSheet1 = EasyExcel.writerSheet("????????????").build();//??????
        excellocalWriter.fill(timeDataList, writeSheet);
        excellocalWriter.fill(itFillExcel, writeSheet1);
        excellocalWriter.finish();
    }

    @Override
    public DailyData getDailyData(String beginTime, String endTime) throws Exception {
        String domain = "helpdesk.greentree.com";
        List<HourData> timeDataList;
        List<DayData>  dayDataList;
        List<DayData>  monthDayDataList;
        List<Satisfy>  satisifyList;
        List<Satisfy>  monthsatisifyList;
//            List<CallRecord> callRecordsList;

        //?????????????????????
        try {
            //IT??????????????????
            timeDataList = getLaiHuDataService.getCallReportHour(beginTime, endTime, domain, "callreport_hour");

            //IT??????????????????
            dayDataList = getLaiHuDataService.getCallReportDay(beginTime, endTime, domain, "callreport_day");

            //IT????????????
            monthDayDataList = getLaiHuDataService.getCallReportDay(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "callreport_day");

            //IT???????????????
            satisifyList = getLaiHuDataService.getSatisfactionDay(beginTime.substring(0, 10), endTime.substring(0, 10), domain, "satisfaction_day");

            //IT???????????????
            monthsatisifyList = getLaiHuDataService.getSatisfactionDay(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "satisfaction_day");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //??????????????????excel
        LocalData dayLocalDailyIt = getLocalDataService.getDay(beginTime.substring(0,10));

        //??????????????????excel
        LocalData monthLocalDailyIt = getLocalDataService.getMonth(beginTime.substring(0,10));

        ItFillExcel itFillExcel = new ItFillExcel();

        //title
        itFillExcel.setTitle(beginTime.substring(5, 7) + '/' + beginTime.substring(8, 10) + "??????????????????????????????????????????");

        //?????????????????????
        itFillExcel.setIncallAnswerMonthRate(monthDayDataList.stream().mapToDouble(DayData::getIncallAnswerRate).average().getAsDouble() * 0.01);

        //?????????????????????
        itFillExcel.setIncallAnswerRate(dayDataList.get(0).getIncallAnswerRate() * 0.01F);
        if (itFillExcel.getIncallAnswerRate() >= 0.7832) {
            itFillExcel.setIncallAnswerRateIsDone("?????????");
            itFillExcel.setIncallAnswerDiff("/");
        } else {
            itFillExcel.setIncallAnswerRateIsDone("?????????");
            itFillExcel.setIncallAnswerDiff(twoScale((0.7832 - itFillExcel.getIncallAnswerRate()) * 100) + "%");
        }

        //???????????????
        itFillExcel.setTriggerNumber(satisifyList.get(0).getTriggerNumber());

        //???????????????
        itFillExcel.setTriggerRate(satisifyList.get(0).getFloatTriggerRate() * 0.01);
        if (itFillExcel.getTriggerRate() >= 0.50) {
            itFillExcel.setTriggerRateIsDone("?????????");
            itFillExcel.setTriggerRateDiff("/");
        } else {
            itFillExcel.setTriggerRateIsDone("?????????");
            itFillExcel.setTriggerRateDiff(twoScale((0.50 - itFillExcel.getTriggerRate()) * 100) + "%");
        }

        //???????????????
        itFillExcel.setTriggerMonthRate(monthsatisifyList.stream().mapToDouble(Satisfy::getFloatTriggerRate).average().getAsDouble() * 0.01);

        //???????????????
        itFillExcel.setVerySatisfiedWithThePercentage(satisifyList.get(0).getVerySatisfiedWithThePercentage());
        if (Float.parseFloat(itFillExcel.getVerySatisfiedWithThePercentage().substring(0, 4)) >= 99.3) {
            itFillExcel.setPercentageIsDone("?????????");
            itFillExcel.setPercentageDiff("/");
        } else {
            itFillExcel.setPercentageIsDone("?????????");
            itFillExcel.setPercentageDiff(twoScale(99.3-Double.parseDouble(itFillExcel.getVerySatisfiedWithThePercentage().substring(0, 4))) + "%");
        }

        //???????????????
        itFillExcel.setPercentageMonthDone(twoScale(monthsatisifyList.stream().mapToDouble(Satisfy::getFloatPercentage).average().getAsDouble())*0.01);

        //?????????????????????
        itFillExcel.setDayDone(dayLocalDailyIt.getDaydone());

        //?????????????????????
        if (dayLocalDailyIt.getSignature()!=0){
            itFillExcel.setPeople(dayLocalDailyIt.getPeople()-1);
        }else {
            itFillExcel.setPeople(dayLocalDailyIt.getPeople());
        }

        //????????????????????????
        itFillExcel.setHour(dayLocalDailyIt.getHour());

        //??????????????????
        itFillExcel.setDayAvgDone((double)itFillExcel.getDayDone() / itFillExcel.getPeople());

        //??????????????????
        if (itFillExcel.getDayAvgDone()>=35){
            itFillExcel.setIsDone("?????????");
        }else {
            itFillExcel.setIsDone("?????????");
        }

        //????????????????????????
        itFillExcel.setSignature(String.valueOf(dayLocalDailyIt.getSignature()));
        if (dayLocalDailyIt.getSignature() >= 30) {
            itFillExcel.setSignaturePlan("30");
            itFillExcel.setSignatureIsDone("?????????");
            itFillExcel.setSignatureDiff("/");
        } else if (dayLocalDailyIt.getSignature()==0) {
            itFillExcel.setSignaturePlan("/");
            itFillExcel.setSignature("/");
            itFillExcel.setSignatureIsDone("/");
            itFillExcel.setSignatureDiff("/");
        } else {
            itFillExcel.setSignaturePlan("30");
            itFillExcel.setSignatureIsDone("?????????");
            itFillExcel.setSignatureDiff(String.valueOf(30 - dayLocalDailyIt.getSignature()));
        }

        //????????????????????????
        itFillExcel.setSignatureMonthDone(monthLocalDailyIt.getSignature());
        DailyData dailyData = new DailyData();
        dailyData.setHourDataList(timeDataList);
        dailyData.setItFillExcel(itFillExcel);

        return dailyData;
    }

    @Value("${templateFile.varianceDemo.path.dev}")
    String variancePath;
    @Value("${templateFile.itAvg.path.dev}")
    String itAvgPath;
    /**
     * @param response
     * @param dataParams
     * @throws Exception
     */
    @Override
    public void getVariance(HttpServletResponse response, DataParams dataParams) throws Exception {
        String beginTime= dataParams.getBeginTime();
        String endTime= dataParams.getEndTime();
        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        int num = getLaiHuDataService.getPage(beginTime,endTime,"helpdesk.greentree.com","/report/rest/callreport_hour/querynum");
        List<HourData> datasList = getLaiHuDataService.getCallReportHour(beginTime,endTime,"helpdesk.greentree.com","callreport_hour");
        GetVariance.getVariance(response, num, datasList, variancePath, itAvgPath);
    }

    @Value("${templateFile.itRecord.path.dev}")
    String itRecordPath;
    /**
     * @param response
     * @param dataParams
     * @throws Exception
     */
    @Override
    public void getReCallRecord(HttpServletResponse response, DataParams dataParams) throws Exception {
        String beginTime= dataParams.getBeginTime();
        String endTime= dataParams.getEndTime();
        String itRecordPath = "itRecord.xlsx";
        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();
        List<CallRecord> callRecordsList;
        try {

            //??????????????????
            callRecordsList = getLaiHuDataService.getCallRecord(beginTime,endTime,"helpdesk.greentree.com","callrecord");

            //????????????(????????????????????????????????????90??????)
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

            //?????????????????????
            List<CallRecord> callRecordList = new ArrayList<>();

            //???????????????
            HashMap<Object, Object> unAnswerRecordMap = new HashMap<>();

            //????????????????????????
            CallRecordNum callRecordNum = new CallRecordNum();

            for (CallRecord callRecord : callRecordsList) {
                //???????????????????????????
                if(!Objects.equals(callRecord.getHangupCause(),"4") && Objects.equals(callRecord.getCallType(),"2")){
                    //1??????????????????
                    callRecordNum.setTotalNum(callRecordNum.getTotalNum()+1);
                    //???????????????
                    if (!unAnswerRecordMap.containsKey(callRecord.getCaller())){
                        unAnswerRecordMap.put(callRecord.getCaller(),callRecord);
                        //????????????????????????????????????????????????????????? ??? 2?????????????????????
                        if (answerRecordMap.containsKey(callRecord.getCaller())){
                            callRecordNum.setSolvedNum(callRecordNum.getSolvedNum()+1);
                        }
                        //??????????????????????????????????????????????????? ??? 4??????????????????
                        if(!answerRecordMap.containsKey(callRecord.getCaller()) ){
                            callRecordNum.setToReCallNum(callRecordNum.getToReCallNum()+1);
                            callRecordList.add(callRecord);
                        }
                    }else {
                        //3??????????????????
                        callRecordNum.setRepeatNum(callRecordNum.getRepeatNum()+1);
                    }
                }
            }

            //excel????????????
            ExcelWriter excellocalWriter = EasyExcel.write("FilesIt/"+"ItReCall"+beginTime.substring(0,10)+".xlsx").withTemplate(itRecordPath).build();
            WriteSheet writeSheet =EasyExcel.writerSheet(0).build();
            excellocalWriter.fill(callRecordList,writeSheet);
            excellocalWriter.fill(callRecordNum,writeSheet);
            excellocalWriter.finish();

            //excel???????????????
            ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(itRecordPath).build();
            excelWriter.fill(callRecordList,writeSheet);
            excelWriter.fill(callRecordNum,writeSheet);
            excelWriter.finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    @Override
    public JsonResult<List<HourData>> getDatas(String beginTime, String endTime) throws Exception {
        return ResultTool.success(getLaiHuDataService.getCallReportHour(beginTime, endTime, "helpdesk.greentree.com" ,"callreport_hour"));
    }




    @Override
    public JsonResult<ResultCode> sendEmail(String beginTime,byte[] bytes) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {
        try{
            WriteImgUtil.addImageToExcel(new File("FilesIt/IT" + beginTime.substring(0, 10) + ".xlsx"), 1,Base64.getDecoder().decode(bytes),(short) 0, 11, (short) 7, 27);
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

        //4?????????MimeMessage
        //?????????????????????
        MimeMessage mimeMessage = new MimeMessage(session);
        //?????????
        mimeMessage.setFrom(new InternetAddress("2365118193@qq.com","IT"));
        //?????????
        InternetAddress zhuwentao = new InternetAddress("zhuwentao@998.com","?????????");
        InternetAddress duhaiqiao = new InternetAddress("duhaiqiao@998.com","?????????");
        InternetAddress zhangqian = new InternetAddress("zhangqiana@998.com","??????");
        InternetAddress zhangchenyang = new InternetAddress("zhangchenyang@998.com","?????????");
        mimeMessage.setRecipient(Message.RecipientType.TO,zhangchenyang);
//        InternetAddress[] internetAddressCC={duhaiqiao,zhangqian,zhangchenyang};
//        mimeMessage.setRecipients(Message.RecipientType.CC,internetAddressCC);
        MimeBodyPart text = new MimeBodyPart();
        MimeBodyPart appendix = new MimeBodyPart();
        MimeBodyPart image = new MimeBodyPart();
        //????????????
        mimeMessage.setSubject("??????-???????????????");
        //????????????
        DataHandler dh = new DataHandler(new FileDataSource("FilesIt/IT"+beginTime.substring(0,10)+".png")); // ??????????????????
        image.setDataHandler(dh);		            // ????????????????????????????????????
        image.setContentID("image_fairy_tail");	    // ???????????????????????????????????????????????????????????????????????????ID???
        //????????????
        text.setContent(beginTime.substring(0,10)+"??????-???????????????<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");
        //??????
        String fileName = "IT"+beginTime.substring(0,10)+".xlsx";
        appendix.setDataHandler(new DataHandler(new FileDataSource("FilesIt/"+fileName)));
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

        return ResultTool.success(ResultCode.SUCCESS);
    }


    /**
     * @param response
     * @param dataParams
     * @throws Exception
     */
    @Override
    public void getRecordData(HttpServletResponse response, DataParams dataParams) throws Exception {
        String beginTime= dataParams.getBeginTime();
        String endTime= dataParams.getEndTime();
        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();
        List<DemoData> demoDataList;
        LocalData condData = getLocalDataService.getCond(beginTime,endTime);
        JSONArray demoDataListJson = JSONObject.parseArray((String) condData.getDatalist());
        List<DemoData> demoData = demoDataListJson.toJavaList(DemoData.class);
        EasyExcel.write(outputStream, DemoData.class).sheet("??????").doWrite(demoData);
    }
}
