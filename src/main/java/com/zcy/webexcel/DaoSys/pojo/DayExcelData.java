package com.zcy.webexcel.DaoSys.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DayExcelData {
    @ExcelProperty(value = "酒店编号")
    private String hotelNum;
    @ExcelProperty(value = "申请来源")
    private String originApply;
    @ExcelProperty(value = "报修类型")
    private String sysType;
    @ExcelProperty(value = "故障描述")
    private String  problemDes;
    @ExcelProperty(value = "处理结果")
    private String dealResult;
    @ExcelProperty(value = "报修开始时间")
    private String startTime;
    @ExcelProperty(value = "报修结束时间")
    private String endTime;
    @ExcelProperty(value = "处理完成状态")
    private String statusDone;
    @ExcelProperty(value = "外部对接人")
    private String peopleOther;
    @ExcelProperty(value = "类型")
    private String supportType;
    @ExcelProperty(value = "问题性质")
    private String problemType;
    @ExcelProperty(value = "申请人姓名")
    private String name;
}
