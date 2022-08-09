package com.zcy.webexcel.DaoSys.pojo;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@ContentRowHeight(1)
@ColumnWidth(1)
public class ImageData {
    public byte[] byteArray;
}
