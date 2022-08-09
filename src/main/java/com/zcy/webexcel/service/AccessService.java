package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.mapper.SysUser;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;
import com.zcy.webexcel.DaoSys.vo.ResultTool;
import org.springframework.security.core.userdetails.User;

public interface AccessService {

    JsonResult login (SysUser user);

    JsonResult logout (String token) throws Exception;

}
