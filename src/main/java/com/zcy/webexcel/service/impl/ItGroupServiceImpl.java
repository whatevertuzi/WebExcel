package com.zcy.webexcel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.sun.mail.util.MailSSLSocketFactory;
import com.zcy.webexcel.Component.GetVariance;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.pojo.LaiHuSys.*;
import com.zcy.webexcel.vo.ItFillExcel;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.vo.ResultTool;
import com.zcy.webexcel.Utils.Base2ImgUtil;
import com.zcy.webexcel.pojo.LocalData.LocalDailyIt;
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
        //利用BigDecimal来实现四舍五入.保留一位小数
        //1代表保留1位小数,保留两位小数就是2,依此累推
        //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
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
        String domain =dataParams.getDoMain();
        String beginTime = dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();

        response.addHeader("Content-Disposition", "attachment;filename=" + "test.xlsx");
        response.setContentType("application/vnd.ms-excel;charset=gb2312");
        ServletOutputStream outputStream = response.getOutputStream();

        List<HourData> timeDataList;
        List<DayData>  dayDataList;
        List<DayData>  monthDayDataList;
        List<Satisfy>  satisifyList;
        List<Satisfy>  monthsatisifyList;
//            List<CallRecord> callRecordsList;

        //从来虎获取数据
        try {
            //IT分时呼损数据
            timeDataList = getLaiHuDataService.getCallReportHour(beginTime, endTime, domain, "callreport_hour");

            //IT当日呼损数据
            dayDataList = getLaiHuDataService.getCallReportDay(beginTime, endTime, domain, "callreport_day");

            //IT当月数据
            monthDayDataList = getLaiHuDataService.getCallReportDay(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "callreport_day");

            //IT当日满意率
            satisifyList = getLaiHuDataService.getSatisfactionDay(beginTime.substring(0, 10), endTime.substring(0, 10), domain, "satisfaction_day");

            //IT当月满意率
            monthsatisifyList = getLaiHuDataService.getSatisfactionDay(beginTime.substring(0, 8) + "01", beginTime.substring(0, 10), domain, "satisfaction_day");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //当日报修记录excel
        LocalDailyIt dayLocalDailyIt = getLocalDataService.getLocalExcel(beginTime);

        //当月报修记录excel
        LocalDailyIt monthLocalDailyIt = getLocalDataService.getLocalExcel(beginTime.substring(0,7));

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
        itFillExcel.setDayDone(dayLocalDailyIt.getDayDone());

        //当日发日报人数
        if (dayLocalDailyIt.getSignature()!=0){
            itFillExcel.setPeople(dayLocalDailyIt.getPeople()-1);
        }else {
            itFillExcel.setPeople(dayLocalDailyIt.getPeople());
        }

        //当日处理报修用时
        itFillExcel.setHour(dayLocalDailyIt.getHour());

        //人日均处理数
        itFillExcel.setDayAvgDone((double)itFillExcel.getDayDone() / itFillExcel.getPeople());

        //当日是否完成
        if (itFillExcel.getDayAvgDone()>=35){
            itFillExcel.setIsDone("已完成");
        }else {
            itFillExcel.setIsDone("未完成");
        }

        //当日会签回访数量
        itFillExcel.setSignature(String.valueOf(dayLocalDailyIt.getSignature()));
        if (dayLocalDailyIt.getSignature() >= 30) {
            itFillExcel.setSignaturePlan("30");
            itFillExcel.setSignatureIsDone("已完成");
            itFillExcel.setSignatureDiff("/");
        } else if (dayLocalDailyIt.getSignature()==0) {
            itFillExcel.setSignaturePlan("/");
            itFillExcel.setSignature("/");
            itFillExcel.setSignatureIsDone("/");
            itFillExcel.setSignatureDiff("/");
        } else {
            itFillExcel.setSignaturePlan("30");
            itFillExcel.setSignatureIsDone("未完成");
            itFillExcel.setSignatureDiff(String.valueOf(30 - dayLocalDailyIt.getSignature()));
        }

        //当月会签回访总数
        itFillExcel.setSignatureMonthDone(monthLocalDailyIt.getSignature());

        //excel写在本地
        ExcelWriter excellocalWriter = EasyExcel.write(  "FilesIt/"+"IT" + beginTime.substring(0, 10) + ".xlsx").withTemplate(itFillPath).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("分时呼损").build();//本地和前后端公用
        WriteSheet writeSheet1 = EasyExcel.writerSheet("数据汇总").build();//同上
        excellocalWriter.fill(timeDataList, writeSheet);
        excellocalWriter.fill(itFillExcel, writeSheet1);
        excellocalWriter.finish();

        //返回给前端excel
        ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(itFillPath).build();
        excelWriter.fill(timeDataList, writeSheet);
        excelWriter.fill(itFillExcel, writeSheet1);
        excelWriter.finish();

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

            //获取所有记录
            callRecordsList = getLaiHuDataService.getCallRecord(beginTime,endTime,"helpdesk.greentree.com","callrecord");

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
            ExcelWriter excellocalWriter = EasyExcel.write("FilesIt/"+"ItReCall"+beginTime.substring(0,10)+".xlsx").withTemplate(itRecordPath).build();
            WriteSheet writeSheet =EasyExcel.writerSheet(0).build();
            excellocalWriter.fill(callRecordList,writeSheet);
            excellocalWriter.fill(callRecordNum,writeSheet);
            excellocalWriter.finish();

            //excel返回给前端
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



    /**
     * @param jsonObject
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

        //4、准备MimeMessage
        //消息的固定信息
        MimeMessage mimeMessage = new MimeMessage(session);
        //发件人
        mimeMessage.setFrom(new InternetAddress("2365118193@qq.com","IT"));
        //收件人
        InternetAddress zhuwentao = new InternetAddress("zhuwentao@998.com","朱文涛");
        InternetAddress duhaiqiao = new InternetAddress("duhaiqiao@998.com","杜海桥");
        InternetAddress zhangqian = new InternetAddress("zhangqiana@998.com","张倩");
        InternetAddress zhangchenyang = new InternetAddress("zhangchenyang@998.com","张晨阳");
        mimeMessage.setRecipient(Message.RecipientType.TO,zhuwentao);
        InternetAddress[] internetAddressCC={duhaiqiao,zhangqian,zhangchenyang};
        mimeMessage.setRecipients(Message.RecipientType.CC,internetAddressCC);
        MimeBodyPart text = new MimeBodyPart();
        MimeBodyPart appendix = new MimeBodyPart();
        MimeBodyPart image = new MimeBodyPart();
        //邮件标题
        mimeMessage.setSubject("日报-实施服务组");
        //准备图片
        DataHandler dh = new DataHandler(new FileDataSource("FilesIt/IT"+beginTime.substring(0,10)+".jpg")); // 读取本地文件
        image.setDataHandler(dh);		            // 将图片数据添加到“节点”
        image.setContentID("image_fairy_tail");	    // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）
        //准备文本
        text.setContent(beginTime.substring(0,10)+"日报-实施服务组<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");
        //附件
        String fileName = "IT"+beginTime.substring(0,10)+".xlsx";
        appendix.setDataHandler(new DataHandler(new FileDataSource("FilesIt/"+fileName)));
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

        return ResultTool.success(ResultCode.SUCCESS);
    }
}
