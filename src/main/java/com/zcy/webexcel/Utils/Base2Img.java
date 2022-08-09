package com.zcy.webexcel.Utils;

import com.zcy.webexcel.DaoSys.vo.Result;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Base2Img {
    public static Result generateImg(byte[] bytes,String beginTime,String domain){
        OutputStream out = null;
        if (Objects.equals(domain,"helpdesk.greentree.cn")){
            try{
                out = Files.newOutputStream(Paths.get("FilesIt/IT" + beginTime.substring(0, 10) + ".jpg"));
                out.write(bytes);
                out.flush();
                out.close();
            }catch (Exception e){
                Result.fail(401,"生成图片失败");
                e.printStackTrace();
            }
        }
        if (Objects.equals(domain,"yuding.greentree.cn")){
            try{
                out = Files.newOutputStream(Paths.get("FilesYuding/Yuding" + beginTime.substring(0, 10) + ".jpg"));
                out.write(bytes);
                out.flush();
                out.close();
            }catch (Exception e){
                Result.fail(401,"生成图片失败");
                e.printStackTrace();
            }
        }
        return Result.success(null);
    }
}
