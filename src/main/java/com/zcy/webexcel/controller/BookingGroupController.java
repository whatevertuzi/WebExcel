package com.zcy.webexcel.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.pojo.DataParams;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.vo.ResultTool;
import com.zcy.webexcel.service.BookingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("booking")
public class BookingGroupController {
    private final BookingService bookingService;

    public BookingGroupController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("daily")
    public void getBookingDaily(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            bookingService.getDaily(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("variance")
    public void getBookingVariance(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            bookingService.getVariance(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //分时数据
    @PostMapping("datas")
    public JsonResult getBookingDatas(@RequestBody DataParams dataParams){
        String beginTime =dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();
        try {
            return  bookingService.getDatas(beginTime,endTime);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
    }
    @PostMapping("email")
    public JsonResult<ResultCode> sendEmail(@RequestBody JSONObject jsonObject){
        try {
            bookingService.sendEmail(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
        return ResultTool.success(ResultCode.SUCCESS);
    }
}
