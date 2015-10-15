/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Administrator
 */
public class Mail {

    private final String reciever;
    private final String sender;
    private final String host;
    private final String title;
    private final String content;

    public Mail(String title, String content) {
        this.sender = GetConfig.rb.getString("sender");
        this.reciever = GetConfig.rb.getString("reciever");
        this.host = GetConfig.rb.getString("host");
        this.title = title;
        this.content = content;
    }

    public void SendMail() {
        // 收件人电子邮箱
        String to = this.reciever;

        // 发件人电子邮箱
        String from = this.sender;

        // 指定发送邮件的主机为 localhost
        String host = this.host;  //QQ 邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(GetConfig.rb.getString("mailuser"), GetConfig.rb.getString("mailpass")); //发件人邮件用户名、密码
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject(this.title);

            // 设置消息体
            message.setText(this.content);

            // 发送消息
            Transport.send(message);
            //System.out.println(this.content);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
