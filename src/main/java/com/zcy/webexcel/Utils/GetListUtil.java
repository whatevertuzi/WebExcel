package com.zcy.webexcel.Utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.zcy.webexcel.DaoSys.pojo.CsTableYuding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//获取当日上班人数信息
public class GetListUtil {

    public static List<String> getNoName(String beginTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        List<CsTableYuding> csTableYudingList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", CsTableYuding.class,new PageReadListener<CsTableYuding>(datList->{
            for (CsTableYuding csTableYuding : datList){
                if (Objects.equals(csTableYuding.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(csTableYuding.getNo()) || Objects.equals(csTableYuding.getNo(),"远程")){
                    csTableYuding.setNo("坐席号未知");
                }
                csTableYudingList.add(csTableYuding);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSunday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSunday().contains("休")&&!csTableYuding.getSaturday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 2) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getMonday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getMonday().contains("休")&&!csTableYuding.getTemp().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 3) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                boolean night = csTableYuding.getTuesday().contains("休")&&!csTableYuding.getMonday().contains("晚班");
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getTuesday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || night){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 4) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getWednesday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getWednesday().contains("休")&&!csTableYuding.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 5) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getThursday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getThursday().contains("休")&&!csTableYuding.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 6) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getFriday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getFriday().contains("休")&&!csTableYuding.getThursday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        } else if (weekday == 7) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSaturday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSaturday().contains("休")&&!csTableYuding.getFriday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(csTableYuding.getNo());
                }
            }
        }
        return NoList;
    }
    public static List<String> getNight(String beginTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(beginTime.substring(0,10));
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        List<CsTableYuding> csTableYudingList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", CsTableYuding.class,new PageReadListener<CsTableYuding>(datList->{
            for (CsTableYuding csTableYuding : datList){
                if (Objects.equals(csTableYuding.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(csTableYuding.getNo()) || Objects.equals(csTableYuding.getNo(),"远程")){
                    csTableYuding.setNo("坐席号未知");
                }
                csTableYudingList.add(csTableYuding);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSunday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSunday().contains("休")&&!csTableYuding.getSaturday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getSunday().contains("休")&&csTableYuding.getSaturday().contains("晚班")) || csTableYuding.getSunday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 2) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getMonday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getMonday().contains("休")&&!csTableYuding.getTemp().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getMonday().contains("休")&&csTableYuding.getTemp().contains("晚班")) || csTableYuding.getMonday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 3) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                boolean night = csTableYuding.getTuesday().contains("休")&&!csTableYuding.getMonday().contains("晚班");
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getTuesday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || night){
                    continue;
                }else {
                    if ((csTableYuding.getTuesday().contains("休")&&csTableYuding.getMonday().contains("晚班")) || csTableYuding.getTuesday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 4) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getWednesday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getWednesday().contains("休")&&!csTableYuding.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getWednesday().contains("休")&&csTableYuding.getTuesday().contains("晚班")) || csTableYuding.getWednesday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 5) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知") || csTableYuding.getThursday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getThursday().contains("休")&&!csTableYuding.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getThursday().contains("休")&&csTableYuding.getWednesday().contains("晚班")) || csTableYuding.getThursday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 6) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getFriday().contains("房控")  || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getFriday().contains("休")&&!csTableYuding.getThursday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getFriday().contains("休")&&csTableYuding.getThursday().contains("晚班")) || csTableYuding.getFriday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        } else if (weekday == 7) {
            for (CsTableYuding csTableYuding : csTableYudingList) {
                if (Objects.equals(csTableYuding.getNo(),"坐席号未知")  || csTableYuding.getSaturday().contains("房控") || Objects.equals(csTableYuding.getName(), "沈鸣") || (csTableYuding.getSaturday().contains("休")&&!csTableYuding.getFriday().contains("晚班"))){
                    continue;
                }else {
                    if ((csTableYuding.getSaturday().contains("休")&&csTableYuding.getFriday().contains("晚班")) || csTableYuding.getSaturday().contains("晚班")){
                        NoList.add(csTableYuding.getNo());
                    }
                }
            }
        }
        return NoList;
    }
}
