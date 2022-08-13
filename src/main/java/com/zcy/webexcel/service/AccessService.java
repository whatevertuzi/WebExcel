package com.zcy.webexcel.service;

import com.zcy.webexcel.DaoSys.pojo.SysUser;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;

public interface AccessService {

    JsonResult<String> login (SysUser user);

    JsonResult<ResultCode> logout (String token) throws Exception;

}
