package com.zcy.webexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.CsTableYuding;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class WebExcelApplicationTestsService {

    @Test
    public void test(){
        String a="100";
        String b="001";
        System.out.println(a.equals(b));
    }




    @Test
    public void getNoName() throws ParseException {
        String beginTime = "2022-07-18";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        List<CsTableYuding> csTableYudingList = new ArrayList<>();
        EasyExcel.read("C:\\Users\\GT-IT\\Desktop\\班表.xlsx", CsTableYuding.class,new PageReadListener<CsTableYuding>(datList->{
            for (CsTableYuding csTableYuding : datList){
                if (Objects.equals(csTableYuding.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(csTableYuding.getNo())){
                    csTableYuding.setNo("坐席号未知");
                }
                csTableYudingList.add(csTableYuding);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 2) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 3) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 4) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 5) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 6) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 7) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getSunday().contains("房控")){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        }
    }
    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    public void testPasswordEncoder(){
       String pw = passwordEncoder.encode("Abcdef123456");
        System.out.println(pw);
    }



}
