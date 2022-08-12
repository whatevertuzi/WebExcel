package com.zcy.webexcel.Utils;

import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.Objects;

public class WriteImgUtil {

    /**
     * 写入图片到Excel指定的位置
     *
     * @param patriarch 画图的顶级管理器，一个sheet只能获取一次，多次插入图片请使用同一个patriarch对象
     * @param wb        HSSFWorkbook对象
     */
    public static void whiteImg(XSSFDrawing patriarch, XSSFWorkbook wb, byte[] bytes, int col1, int row1, int col2, int row2) {
        XSSFClientAnchor anchor1;
        anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) col1, row1, (short) col2, row2);
        // anchor主要用于设置图片的属性
        // anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 19, (short) 8, 28);预订
        // anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 11, (short) 7, 27);IT
        // 插入图片
        int index = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PICT);
        patriarch.createPicture(anchor1, index);
    }

    /**
     * * 插入图片到指定excel
     *
     * @param file       待插入excel文件
     * @param sheetIndex 待插入excel的Sheet序号(从0开始)
     * @Date: 2020/11/28 10:09
     */
    public static void addImageToExcel(File file, Integer sheetIndex, byte[] bytes, int col1, int row1, int col2, int row2) {
        FileOutputStream fileOut = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            XSSFSheet sheet1 = wb.getSheetAt(sheetIndex);
            // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            XSSFDrawing patriarch = sheet1.createDrawingPatriarch();
            whiteImg(patriarch, wb, bytes,(short) col1, row1, (short) col2, row2);
            fileOut = new FileOutputStream(file);
            // 写入excel文件
            wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
