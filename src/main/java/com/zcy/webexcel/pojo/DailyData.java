package com.zcy.webexcel.pojo;


import com.zcy.webexcel.pojo.LaiHuSys.HourData;
import com.zcy.webexcel.vo.ItFillExcel;
import lombok.Data;

import java.util.List;

@Data
public class DailyData {
    ItFillExcel itFillExcel;
    List<HourData> hourDataList;
}
