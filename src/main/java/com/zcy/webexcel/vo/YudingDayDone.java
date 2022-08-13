package com.zcy.webexcel.vo;

import lombok.Data;

//预定组工作内容
@Data
public class YudingDayDone {
    String no;
    String name;
    Double inCall;
    Double outCall;
    Double allCall;
    Double satisfy;
}
