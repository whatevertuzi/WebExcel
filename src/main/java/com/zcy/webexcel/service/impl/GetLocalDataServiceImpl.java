package com.zcy.webexcel.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcy.webexcel.DaoSys.mapper.LocalDataMapper;
import com.zcy.webexcel.DaoSys.pojo.LocalData;
import com.zcy.webexcel.DaoSys.pojo.SysBusinessStatistics;
import com.zcy.webexcel.pojo.LocalData.DemoData;
import com.zcy.webexcel.service.GetLocalDataService;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GetLocalDataServiceImpl implements GetLocalDataService {
    private final LocalDataMapper localDataMapper;

    public GetLocalDataServiceImpl(LocalDataMapper localDataMapper) {
        this.localDataMapper = localDataMapper;
    }

    @Override
    public LocalData getDay(String beginTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginTimeDate = format.parse(beginTime);
        LambdaQueryWrapper<LocalData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(LocalData::getDate,beginTimeDate);
        return localDataMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * @param beginTime
     * @return
     * @throws Exception
     */
    @Override
    public LocalData getMonth(String beginTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse(beginTime.substring(0,8)+"01");
        return getLocalData(beginTime, format, startDate);
    }
    @Override
    public LocalData getCond(String beginTime,String endTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = format.parse(beginTime.substring(0,10));
        return getLocalData(endTime, format, startDate);
    }

    private LocalData getLocalData(String endTime, SimpleDateFormat format, Date startDate) throws ParseException {
        Date endDate = format.parse(endTime.substring(0,10));
        LambdaQueryWrapper<LocalData> MonthQueryWrapper = new LambdaQueryWrapper<>();
        MonthQueryWrapper.ge(LocalData::getDate,startDate);//大于开始日期
        MonthQueryWrapper.le(LocalData::getDate,endDate);//小于结束日期
        List<LocalData> localDataList = localDataMapper.selectList(MonthQueryWrapper);
        LocalData localData = new LocalData();
        List<DemoData> finalDemoDataList = new ArrayList<>();
        for (LocalData localDatali : localDataList) {
            Object datalist = localDatali.getDatalist();
            JSONArray demoDataList = JSONObject.parseArray((String) datalist);
            List<DemoData> demoData = demoDataList.toJavaList(DemoData.class);
            finalDemoDataList.addAll(demoData);
        }
        String s = JSONObject.toJSONString(finalDemoDataList);
        localData.setDatalist(s);
        localData.setSignature(localDataList.stream().mapToDouble(LocalData::getSignature).sum());
        return localData;
    }


}
