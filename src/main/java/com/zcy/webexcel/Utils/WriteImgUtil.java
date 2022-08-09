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
     * @return cellPoint 自定义的对象，返回下一个要插入图片的坐标(x, y)
     * @throws IOException
     */
    public static void whiteImg(XSSFDrawing patriarch, XSSFWorkbook wb, byte[] bytes,String domain) throws IOException {
        XSSFClientAnchor anchor1 = null;
        if (Objects.equals(domain,"yuding.greentree.cn")){
            anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 19, (short) 8, 28);
        } else if (Objects.equals(domain,"helpdesk.greentree.cn")) {
            anchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 11, (short) 7, 27);
        }
        // anchor主要用于设置图片的属性
        // 插入图片
        int index = wb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PICT);
        patriarch.createPicture(anchor1, index);
    }

    /**
     * * 插入图片到指定excel
     *
     * @param file       待插入excel文件
     * @param sheetIndex 待插入excel的Sheet序号(从0开始)

     * @return
     * @Date: 2020/11/28 10:09
     */
    public static void addImageToExcel(File file, Integer sheetIndex, byte[] bytes,String domain) {
        FileOutputStream fileOut = null;
        try {
            FileInputStream inputstream = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(inputstream);
            XSSFSheet sheet1 = wb.getSheetAt(sheetIndex);
            // 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            XSSFDrawing patriarch = sheet1.createDrawingPatriarch();
            whiteImg(patriarch, wb, bytes,domain);
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
