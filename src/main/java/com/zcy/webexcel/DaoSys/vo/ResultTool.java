package com.zcy.webexcel.DaoSys.vo;

/**
 * @Author: zcy
 * @Description:
 * @Date Create in 2022/7/21 19:52
 */
public class ResultTool {
    public static JsonResult<Boolean> success() {
        return new JsonResult<>(true);
    }

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(true, data);
    }

    public static JsonResult<Boolean> fail() {
        return new JsonResult<>(false);
    }

    public static JsonResult<ResultCode> fail(ResultCode resultEnum) {
        return new JsonResult<>(false, resultEnum);
    }
}
