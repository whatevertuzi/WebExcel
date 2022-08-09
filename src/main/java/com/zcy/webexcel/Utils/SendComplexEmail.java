package com.zcy.webexcel.Utils;

import com.sun.mail.util.MailSSLSocketFactory;
import com.zcy.webexcel.service.EmailServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Properties;

public class SendComplexEmail {
    private static final Logger LOGGER = LogManager.getLogger(SendComplexEmail.class);
    public static void send(String begintime,String domain) throws GeneralSecurityException, MessagingException, UnsupportedEncodingException {
        Properties prop = new Properties();
        prop.setProperty("mail.host", "smtp.qq.com");
        prop.setProperty("mail.port", "465");
        prop.setProperty("mail.transport.protocol", "smtp"); // 邮件发送协议
        prop.setProperty("mail.smtp.auth", "true"); // 需要验证用户名密码

        // QQ邮箱设置SSL加密
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);

        //1、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getDefaultInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //传入发件人的姓名和授权码
                return new PasswordAuthentication("2365118193@QQ.com","hzmokzjqypbbeadg");
            }
        });

        //2、通过session获取transport对象
        Transport transport = session.getTransport();

        //3、通过transport对象邮箱用户名和授权码连接邮箱服务器
        transport.connect("smtp.qq.com","2365118193@QQ.com","hzmokzjqypbbeadg");

        //4、创建邮件,传入session对象
        MimeMessage mimeMessage = complexEmail(session,begintime,domain);

        //5、发送邮件
        transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

        //6、关闭连接
        transport.close();
    }

    public static MimeMessage complexEmail(Session session,String begintime ,String domain) throws MessagingException, UnsupportedEncodingException {
        //消息的固定信息
        MimeMessage mimeMessage = new MimeMessage(session);

        //发件人
        mimeMessage.setFrom(new InternetAddress("2365118193@qq.com","IT"));

        //收件人
        InternetAddress zhuwentao = new InternetAddress("zhuwentao@998.com","朱文涛");
        InternetAddress shenjun = new InternetAddress("shenjun@998.com","沈珺");
        InternetAddress shenming = new InternetAddress("shenming@998.com","沈鸣");
        InternetAddress zhangqian = new InternetAddress("zhangqiana@998.com","张倩");
        InternetAddress zhangchenyang = new InternetAddress("zhangchenyang@998.com","张晨阳");
        if (Objects.equals(domain,"yuding.greentree.cn")){
            mimeMessage.setRecipient(Message.RecipientType.TO,zhuwentao);
            mimeMessage.setRecipient(Message.RecipientType.CC,shenming);
            LOGGER.info("发送给"+mimeMessage.getAllRecipients().toString()+"了一封日期为"+begintime.substring(0,10)+"的预订部日报邮件");
        } else if (Objects.equals(domain,"helpdesk.greentree.cn")) {
            mimeMessage.setRecipient(Message.RecipientType.TO,zhuwentao);
            InternetAddress[] internetAddressCC={shenjun,zhangqian,zhangchenyang};
            mimeMessage.setRecipients(Message.RecipientType.CC,internetAddressCC);
            LOGGER.info("发送给"+mimeMessage.getAllRecipients()+"了一封日期为"+begintime.substring(0,10)+"的实施组日报邮件");
        }
//        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress("zhuwentao@998.com","朱文涛"));
//        mimeMessage.setRecipient(Message.RecipientType.CC,new InternetAddress("zhangqiana@998.com","张倩"));
//        mimeMessage.setRecipient(Message.RecipientType.TO,new InternetAddress("zhangchenyang@998.com","张晨阳"));
        MimeBodyPart text = new MimeBodyPart();
        MimeBodyPart appendix = new MimeBodyPart();
        MimeBodyPart image = new MimeBodyPart();
        if (Objects.equals(domain,"yuding.greentree.cn")){
            //邮件标题
            mimeMessage.setSubject(begintime.substring(0,10)+"日报-预订部");

            //准备图片
            DataHandler dh = new DataHandler(new FileDataSource("FilesYuding/Yuding"+begintime.substring(0,10)+".jpg")); // 读取本地文件
            image.setDataHandler(dh);		            // 将图片数据添加到“节点”
            image.setContentID("image_fairy_tail");	    // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）

            //准备文本
            text.setContent(begintime.substring(0,10)+"日报-预订部<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");

            //附件
            String fileName = "Yuding"+begintime.substring(0,10)+".xlsx";
            appendix.setDataHandler(new DataHandler(new FileDataSource("FilesYuding/"+fileName)));
            appendix.setFileName(fileName);
        } else if (Objects.equals(domain,"helpdesk.greentree.cn")) {
            //邮件标题
            mimeMessage.setSubject("日报-实施服务组");

            //准备图片
            DataHandler dh = new DataHandler(new FileDataSource("FilesIt/IT"+begintime.substring(0,10)+".jpg")); // 读取本地文件
            image.setDataHandler(dh);		            // 将图片数据添加到“节点”
            image.setContentID("image_fairy_tail");	    // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）

            //准备文本
            text.setContent(begintime.substring(0,10)+"日报-实施服务组<br/><img src='cid:image_fairy_tail'/>","text/html;charset=utf-8");

            //附件
            String fileName = "IT"+begintime.substring(0,10)+".xlsx";
            appendix.setDataHandler(new DataHandler(new FileDataSource("FilesIt/"+fileName)));
            appendix.setFileName(fileName);
        }

        //拼装邮件正文
        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(text);
        mimeMultipart.addBodyPart(image);
        mimeMultipart.setSubType("related");//文本内嵌成功

        //将拼装好的正文内容设置为主体
        MimeBodyPart contentText = new MimeBodyPart();
        contentText.setContent(mimeMultipart);

        //拼接附件
        MimeMultipart allFile = new MimeMultipart();
        allFile.addBodyPart(appendix);//附件
        allFile.addBodyPart(contentText);//正文
        allFile.setSubType("mixed"); //正文和附件都存在邮件中，所有类型设置为mixed


        //放到Message消息中
        mimeMessage.setContent(allFile);
        mimeMessage.saveChanges();//保存修改

        return mimeMessage;
    }
}

