package com.zcy.webexcel.service.impl;

import com.zcy.webexcel.DaoSys.pojo.CrsData;
import com.zcy.webexcel.Utils.JsoupUtil;
import com.zcy.webexcel.service.GetCrsDataService;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GetCrsDataServiceImpl implements GetCrsDataService {
    @Override
    public CrsData getComplaint(String beginTime) throws Exception {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = df.parse(beginTime.substring(0,10));
        String formatTime = df.format(new Date(date.getTime()-24*60*60*1000));

       Document businessHisStatus0Doc = JsoupUtil.getDoc("",formatTime,"0","来电在线","客户投诉");
       Document businessHisStatus1Doc = JsoupUtil.getDoc("",formatTime,"1","来电在线","客户投诉");
       Elements businessHisStatus0Span = businessHisStatus0Doc.getElementsByClass("total_count");
       Elements businessHisStatus1Span = businessHisStatus1Doc.getElementsByClass("total_count");
       Document businessUnDoneStatus0Doc = JsoupUtil.getDoc(beginTime,beginTime,"0","来电在线","客户投诉");
       Document businessUnDoneStatus1Doc = JsoupUtil.getDoc(beginTime,beginTime,"1","来电在线","客户投诉");
       Elements businessUnDoneStatus0Span = businessUnDoneStatus0Doc.getElementsByClass("total_count");
       Elements businessUnDoneStatus1Span = businessUnDoneStatus1Doc.getElementsByClass("total_count");

       Document crossHisStatus0Doc = JsoupUtil.getDoc("",formatTime,"0","财务相关","agoda");
       Document crossHisStatus1Doc = JsoupUtil.getDoc("",formatTime,"1","财务相关","agoda");
       Elements crossHisStatus0Span = crossHisStatus0Doc.getElementsByClass("total_count");
       Elements crossHisStatus1Span = crossHisStatus1Doc.getElementsByClass("total_count");
       Document crossUnDoneStatus0Doc = JsoupUtil.getDoc(beginTime,beginTime,"0","财务相关","agoda");
       Document crossUnDoneStatus1Doc = JsoupUtil.getDoc(beginTime,beginTime,"1","财务相关","agoda");
       Elements crossUnDoneStatus0Span = crossUnDoneStatus0Doc.getElementsByClass("total_count");
       Elements crossUnDoneStatus1Span = crossUnDoneStatus1Doc.getElementsByClass("total_count");

       Document crossStatus3Doc = JsoupUtil.getDoc(beginTime,beginTime,"3","财务相关","agoda","HandlerTime");
       Elements crossStatus3Span = crossStatus3Doc.getElementsByClass("total_count");

       Document totalDoc = JsoupUtil.getDoc("",formatTime,"-1","","");
       Elements totalSpan = totalDoc.getElementsByClass("total_count");

       int businessHistory = Integer.parseInt(businessHisStatus0Span.text()+businessHisStatus1Span.text());
       int businessUnDone = Integer.parseInt(businessUnDoneStatus0Span.text()+businessUnDoneStatus1Span.text());
       int crossHistory = Integer.parseInt(crossHisStatus0Span.text()+crossHisStatus1Span.text());
       int crossUnDone = Integer.parseInt(crossUnDoneStatus0Span.text()+crossUnDoneStatus1Span.text());
       int cross = Integer.parseInt(crossStatus3Span.text());
       int total = Integer.parseInt(totalSpan.text());

       CrsData crsData = new CrsData();
       crsData.setTotal(total);
       crsData.setBusinessHistory(businessHistory);
       crsData.setBusinessUndone(businessUnDone);
       crsData.setCrossSector(cross);
       crsData.setCrossSectorHistory(crossHistory);
       crsData.setCrossSectorUndone(crossUnDone);

       return crsData;
    }
}
