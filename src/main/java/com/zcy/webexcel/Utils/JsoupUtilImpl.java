package com.zcy.webexcel.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JsoupUtilImpl implements JsoupUtil {
    @Override
    public int getCrsData(String beginTime,String status) throws IOException {
        Document doc = Jsoup.connect("http://52.131.229.157/Complaint/Index")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36") // User-Agent of Chrome 55
                .referrer("http://52.131.229.157/Complaint/Index")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "zh-CN,zh;q=0.9,ja;q=0.8,en;q=0.7")
                .header("Connection", "keep-alive")
                .header("Content-Length", "1042")
                .header("Cookie", "ASP.NET_SessionId=ty4cvumn1tk1hqn4bqaon4mj; .ASPXAUTH=9A9AF703932C99DB107FD7DDBD40AF50C0175C6169A96B13CBE51E8878920A35D083BD75099B004F734554C65816B09CA1247EC2D376A7D3231618854CCE7C01CD65F59A9E5C5325FBC1CCC605D524B68A90F64C292D7C65F2C86F2C2A09E6FFF522FB192C0FA5810D1BB1EF76EEC2CF7DB71AE77D06FDD17AE4F2A4497E812163BE69220D492B4D1AD4973D07651B37F715E16379CB3D532D30DD5BC8342A699920E6EF4A4B72C13216EC6B06C1D1574D7E50B6F0161A733DA46B464649C8BD7CBF9CD9C75FA91219DA0B143B8B941AAD0CF6F9D2A4597DDB2BE2D75B747FA0F122276B; userInfo=userName=1002014080500050")
                .data("status", status)//处理状态
                .data("hotelgroup", "GL")
                .data("createtimeStart", "")
                .data("createtimeEnd", beginTime)//"2022-07-27"
                .data("requesttype1", "来电在线")
                .data("requesttype1", "客户投诉")
                .data("requesttype", "'来电在线','客户投诉'")
                .data("pageform_pager_select", "0")
                .data("pageform_pager_radio_10", "10")
                .data("PageIndex", "0")
                .data("PageSize", "10")
                .post();
        Elements span = doc.getElementsByClass("total_count");
        System.out.println(span.text());
        return 0;
    }
}
