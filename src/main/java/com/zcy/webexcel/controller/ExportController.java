package com.zcy.webexcel.controller;

import com.alibaba.fastjson.JSON;
import com.zcy.webexcel.DaoSys.pojo.DataParams;
import com.zcy.webexcel.service.ExcelService;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("export")
@CrossOrigin
public class ExportController {
    private final ExcelService excelService;
    public ExportController(ExcelService excelService){
        this.excelService=excelService;
    }
    @GetMapping
    public void testRespExcel(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            excelService.exportExcel(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("variance")
    public void varianceExcel(HttpServletResponse response, @RequestParam("data")String data){
        DataParams dataParams = JSON.parseObject(data,DataParams.class);
        try {
//          从HttpServletResponse中获取OutputStream输出流
            excelService.getVariance(response,dataParams);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
