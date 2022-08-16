package com.zcy.webexcel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class webExcelApplicationtTestsService {
    @Autowired
    PasswordEncoder passwordEncoder;




    @Test
    public void testEncoder(){
       String pw = passwordEncoder.encode("lishilong998");
        System.out.println(pw);
    }

    @Test
    public void test(){
        String typeName1 = "哈哈";
        String typeName2 = "嘿嘿";
        String a = "\""+typeName1+","+typeName2+"\"";
        System.out.println(a);
    }

    @Test
    public void testTime() throws Exception {


    }



}
