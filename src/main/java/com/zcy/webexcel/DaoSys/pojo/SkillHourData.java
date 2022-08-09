package com.zcy.webexcel.DaoSys.pojo;

import lombok.Data;

@Data
public class SkillHourData {
    Double incallNum;
    Double inCallAnswerNum;//呼入接通数
    Double abandonRate;//呼入放弃率
    Double abandoncallNum;//呼入放弃数
}
