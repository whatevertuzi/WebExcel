package com.zcy.webexcel.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.webexcel.DaoSys.pojo.LoginUser;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;
import com.zcy.webexcel.DaoSys.vo.ResultTool;
import com.zcy.webexcel.Utils.Base2Img;
import com.zcy.webexcel.Utils.SendComplexEmail;
import com.zcy.webexcel.Utils.WriteImgUtil;
import com.zcy.webexcel.DaoSys.vo.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Base64;
import java.util.Objects;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LogManager.getLogger(EmailServiceImpl.class);

    @Override
    public JsonResult exportImage(JSONObject jsonObject) {
        String imageBase = String.valueOf(jsonObject.get("imageBase64"));
        String beginTime = (String) jsonObject.get("beginTime");
        String domain = (String) jsonObject.get("domain");
        byte[] bytes= Base64.getDecoder().decode(imageBase.substring(23));
        Base2Img.generateImg(bytes,beginTime,domain);
        String user;
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")){
            user = "管理员或未登录人员";
        }else {
            user=((LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getUsername();
        }
        try {
            if (Objects.equals(domain, "yuding.greentree.cn")) {
                WriteImgUtil.addImageToExcel(new File("FilesYuding/Yuding" + beginTime.substring(0, 10) + ".xlsx"), 1, bytes,domain);
            } else if (Objects.equals(domain, "helpdesk.greentree.cn")) {
                WriteImgUtil.addImageToExcel(new File("FilesIt/IT" + beginTime.substring(0, 10) + ".xlsx"), 1, bytes,domain);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
        try{
            SendComplexEmail.send(beginTime,domain);
            LOGGER.info(user+"发送了一封日期为"+beginTime.substring(0,10)+"、域名为"+domain+"的邮件");
        }catch (Exception e){
            e.printStackTrace();
            return ResultTool.fail(ResultCode.COMMON_FAIL);
        }
        return ResultTool.success();
    }

}
