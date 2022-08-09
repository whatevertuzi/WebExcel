package com.zcy.webexcel.DaoSys.mapper;

import com.zcy.webexcel.DaoSys.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author GT-IT
* @description 针对表【sys_permission(权限表)】的数据库操作Mapper
* @createDate 2022-07-21 15:21:58
* @Entity com.zcy.webexcel.DaoSys.SysPermission
*/
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> selectListByUser(Integer userId);
    List<SysPermission> selectListByUsername(String userName);
}




