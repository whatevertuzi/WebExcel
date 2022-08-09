package com.zcy.webexcel.controller;

import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.service.EmailService;
import com.zcy.webexcel.DaoSys.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("sendemail")
@CrossOrigin
public class SendEmailController {

    @Autowired
    EmailService emailService;
    @PostMapping
    public Result sendEmail(@RequestBody JSONObject jsonObject){
        try {
            emailService.exportImage(jsonObject);
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail(401,"获取失败");
        }
        return Result.success(null);
    }

}
