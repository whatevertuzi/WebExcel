package com.zcy.webexcel.Utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.zcy.webexcel.pojo.LocalData.LocalBookingTable;

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
        List<LocalBookingTable> localBookingTableList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", LocalBookingTable.class,new PageReadListener<LocalBookingTable>(datList->{
            for (LocalBookingTable localBookingTable : datList){
                if (Objects.equals(localBookingTable.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(localBookingTable.getNo()) || Objects.equals(localBookingTable.getNo(),"远程")){
                    localBookingTable.setNo("坐席号未知");
                }
                localBookingTableList.add(localBookingTable);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getSunday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getSunday().contains("休")&&!localBookingTable.getSaturday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 2) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getMonday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getMonday().contains("休")&&!localBookingTable.getTemp().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 3) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                boolean night = localBookingTable.getTuesday().contains("休")&&!localBookingTable.getMonday().contains("晚班");
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getTuesday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || night){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 4) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getWednesday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getWednesday().contains("休")&&!localBookingTable.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 5) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getThursday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getThursday().contains("休")&&!localBookingTable.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 6) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getFriday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getFriday().contains("休")&&!localBookingTable.getThursday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
                }
            }
        } else if (weekday == 7) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getSaturday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getSaturday().contains("休")&&!localBookingTable.getFriday().contains("晚班"))){
                    continue;
                }else {
                    NoList.add(localBookingTable.getNo());
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
        List<LocalBookingTable> localBookingTableList = new ArrayList<>();
        EasyExcel.read("sec" +".xlsx", LocalBookingTable.class,new PageReadListener<LocalBookingTable>(datList->{
            for (LocalBookingTable localBookingTable : datList){
                if (Objects.equals(localBookingTable.getGroup(),"排班人数")){
                    break;
                }
                if (Objects.isNull(localBookingTable.getNo()) || Objects.equals(localBookingTable.getNo(),"远程")){
                    localBookingTable.setNo("坐席号未知");
                }
                localBookingTableList.add(localBookingTable);
            }
        })).sheet(0).headRowNumber(2).doRead();
        List<String> NoList = new ArrayList<>();
        if (weekday == 1) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getSunday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getSunday().contains("休")&&!localBookingTable.getSaturday().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getSunday().contains("休")&& localBookingTable.getSaturday().contains("晚班")) || localBookingTable.getSunday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 2) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getMonday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getMonday().contains("休")&&!localBookingTable.getTemp().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getMonday().contains("休")&& localBookingTable.getTemp().contains("晚班")) || localBookingTable.getMonday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 3) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                boolean night = localBookingTable.getTuesday().contains("休")&&!localBookingTable.getMonday().contains("晚班");
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getTuesday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || night){
                    continue;
                }else {
                    if ((localBookingTable.getTuesday().contains("休")&& localBookingTable.getMonday().contains("晚班")) || localBookingTable.getTuesday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 4) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getWednesday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getWednesday().contains("休")&&!localBookingTable.getTuesday().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getWednesday().contains("休")&& localBookingTable.getTuesday().contains("晚班")) || localBookingTable.getWednesday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 5) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知") || localBookingTable.getThursday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getThursday().contains("休")&&!localBookingTable.getWednesday().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getThursday().contains("休")&& localBookingTable.getWednesday().contains("晚班")) || localBookingTable.getThursday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 6) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getFriday().contains("房控")  || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getFriday().contains("休")&&!localBookingTable.getThursday().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getFriday().contains("休")&& localBookingTable.getThursday().contains("晚班")) || localBookingTable.getFriday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        } else if (weekday == 7) {
            for (LocalBookingTable localBookingTable : localBookingTableList) {
                if (Objects.equals(localBookingTable.getNo(),"坐席号未知")  || localBookingTable.getSaturday().contains("房控") || Objects.equals(localBookingTable.getName(), "沈鸣") || (localBookingTable.getSaturday().contains("休")&&!localBookingTable.getFriday().contains("晚班"))){
                    continue;
                }else {
                    if ((localBookingTable.getSaturday().contains("休")&& localBookingTable.getFriday().contains("晚班")) || localBookingTable.getSaturday().contains("晚班")){
                        NoList.add(localBookingTable.getNo());
                    }
                }
            }
        }
        return NoList;
    }
}
