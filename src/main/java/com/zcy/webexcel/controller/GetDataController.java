package com.zcy.webexcel.controller;

import com.zcy.webexcel.DaoSys.pojo.DataParams;
import com.zcy.webexcel.service.DataService;
import com.zcy.webexcel.DaoSys.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("getdata")
@CrossOrigin
public class GetDataController {

    @Autowired
    DataService dataService;
    @PostMapping
    public Result getDatas(@RequestBody DataParams dataParams){
        String beginTime =dataParams.getBeginTime();
        String endTime = dataParams.getEndTime();
        String domain = dataParams.getDoMain();
        try {
//          从HttpServletResponse中获取OutputStream输出流
         return  dataService.getData(beginTime,endTime,domain);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(401,"获取失败");
        }
    }
}
