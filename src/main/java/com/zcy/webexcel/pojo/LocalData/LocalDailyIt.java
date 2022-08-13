package com.zcy.webexcel.pojo.LocalData;

import lombok.Data;

@Data
public class LocalDailyIt {
    Integer dayDone;//当日报修数
    Integer people;//当日处理报修人数
    Integer hour;//处理报修用时
    Integer signature;//会签数量
}
