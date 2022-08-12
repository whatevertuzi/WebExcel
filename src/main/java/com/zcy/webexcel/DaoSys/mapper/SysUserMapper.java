package com.zcy.webexcel.DaoSys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcy.webexcel.DaoSys.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
* @author GT-IT
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2022-07-21 15:20:34
* @Entity com.zcy.webexcel.DaoSys.pojo.SysUser
*/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser selectByName(String userName);

    SysUser selectIdByAccount(String account);

}




