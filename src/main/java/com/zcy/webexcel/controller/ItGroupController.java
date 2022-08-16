package com.zcy.webexcel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.vo.ResultTool;
import com.zcy.webexcel.service.ItGroupService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

@RestController
@CrossOrigin
@RequestMapping("itgroup")
public class ItGroupController {

    private final ItGroupService itGroupService;

    public ItGroupController(ItGroupService itGroupService) {
        this.itGroupService = itGroupService;
    }
    //日报数据
    @GetMapping("daily")
    public void getItDaily(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            itGroupService.getDaily(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //方差和平均数据
    @GetMapping("variance")
    public void getItVariance(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            itGroupService.getVariance(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //分时数据
    @PostMapping("datas")
    public JsonResult getItDatas(@RequestBody DataParams dataParams){
        String beginTime =dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();
        try {
            return  itGroupService.getDatas(beginTime,endTime);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }
    //呼损回访
    @GetMapping("recallrecord")
    public void getRecallRecord(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
            itGroupService.getReCallRecord(response,dataParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送邮件
//    @PostMapping("email")
//    public JsonResult<ResultCode> sendEmail(@RequestBody JSONObject jsonObject) throws MessagingException, GeneralSecurityException, UnsupportedEncodingException {
//        return itGroupService.sendEmail();
//    }
    @GetMapping("record")
    public void getRecordData(HttpServletResponse response, @RequestParam("data")String data) throws Exception {
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        itGroupService.getRecordData(response,dataParams);
    }

}
