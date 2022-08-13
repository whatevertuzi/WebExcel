package com.zcy.webexcel.controller;

import com.zcy.webexcel.DaoSys.pojo.SysUser;
import com.zcy.webexcel.DaoSys.pojo.LogOut;
import com.zcy.webexcel.vo.JsonResult;
import com.zcy.webexcel.vo.ResultCode;
import com.zcy.webexcel.service.AccessService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("user")
@CrossOrigin
public class AccessController {
    private final AccessService loginService;

    public AccessController(AccessService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("login")
        public JsonResult<String> login(@RequestBody SysUser sysUser){
            return loginService.login(sysUser);
        }

        @PostMapping("logout")
        public JsonResult<ResultCode> logout(@RequestBody LogOut logOut) throws Exception {
            return loginService.logout(logOut.getToken());
        }
}


