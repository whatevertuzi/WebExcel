package com.zcy.webexcel.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Base2ImgUtil {
    private static final Logger log = LogManager.getLogger(Base2ImgUtil.class);
    public static void generateImg(byte[] bytes,String path){
        OutputStream out;
        try{
            out = Files.newOutputStream(Paths.get(path));//"FilesIt/IT" + beginTime.substring(0, 10) + ".jpg"
            out.write(bytes);
            out.flush();
            out.close();
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }
}
