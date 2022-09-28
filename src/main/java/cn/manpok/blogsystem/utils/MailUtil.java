package cn.manpok.blogsystem.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 发邮件工具类
 * 使用方式  先调用 setMailConfig 方法配置发件箱
 * 再调用 sendForeach 方法 发送邮件
 */
public class MailUtil {

    //发件箱
    private static String from;
    //秘钥
    private static String pass;
    //邮件服务器
    private static String host;

    private static List<Map<String, String>> configList = new ArrayList();

    /**
     * @param _from 发件箱 123456@qq.com
     * @param _pass 发件密码  gudsafwupxiibhjd
     * @param _host 邮件服务器  smtp.qq.com
     */
    public static void setMailConfig(String _from, String _pass, String _host) {
        //初始化
        if (from == null || pass == null || host == null) {
            from = _from;
            pass = _pass;
            host = _host;
        }

        HashMap<String, String> map = new HashMap();
        map.put("from", _from);
        map.put("pass", _pass);
        map.put("host", _host);
        configList.add(map);
    }

    private static int index = 0;


    /**
     * @param receive 收件人
     * @param subject 邮件主题
     * @param msg     邮件内容
     * @return
     */
    public static boolean sendForeach(String receive, String subject, String msg) {
        return sendForeach(receive, subject, msg, null);
    }


    /**
     * @param receive  收件人
     * @param subject  邮件主题
     * @param msg      邮件内容
     * @param filename 附件地址
     * @return
     */
    public static boolean sendForeach(String receive, String subject, String msg, String filename) {
        return sendForeach(receive, subject, msg, filename, null);
    }


    /**
     * 会使用不同的发件箱发送 直到所有配置的邮箱都试过失败
     *
     * @param receive  收件人
     * @param subject  邮件主题
     * @param msg      邮件内容
     * @param filename 附件地址
     * @param reName   修改附件名
     * @return 成功和失败
     */
    public static boolean sendForeach(String receive, String subject, String msg, String filename, String reName) {
        if (isEmpty(receive)) {
            return false;
        }

        boolean reBoo = true;
        try {
            send(receive, subject, msg, filename, reName);
        } catch (Exception e) {
            System.out.println(new Date() + "发送邮件失败，当前发件箱为：" + from);
            index++;
            if (index >= configList.size()) {
                return false;
            }
            changeConfig();
            reBoo = sendForeach(receive, subject, msg, filename, reName);
        }
        return reBoo;
    }

    private static boolean isEmpty(String receive) {
        return receive == null || "".equals(receive);
    }

    private static void changeConfig() {
        Map<String, String> map = configList.get(index);
        from = map.get("from");
        pass = map.get("pass");
        host = map.get("host");
    }


    /**
     * @param receive  收件人
     * @param subject  邮件主题
     * @param msg      邮件内容
     * @param filename 附件地址
     * @param reName   修改附件名称
     * @return
     */
    private static boolean send(String receive, String subject, String msg, String filename, String reName)
            throws Exception {
        if (isEmpty(receive)) {
            return false;
        }
        if (configList.size() == 0) {
            return false;
        }

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() { // qq邮箱服务器账户、第三方登录授权码
                return new PasswordAuthentication(from, pass); // 发件人邮件用户名、密码
            }
        });

        // 创建默认的 MimeMessage 对象
        MimeMessage message = new MimeMessage(session);

        // Set From: 头部头字段
        String nick = "";
        try {
            nick = javax.mail.internet.MimeUtility.encodeText("Manpok博客系统");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.setFrom(new InternetAddress(nick + " <" + from + ">"));

        // Set To: 头部头字段
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receive));

        // Set Subject: 主题文字
        message.setSubject(subject);

        // 创建消息部分
        BodyPart messageBodyPart = new MimeBodyPart();

        // 消息
        messageBodyPart.setText(msg);

        // 创建多重消息
        Multipart multipart = new MimeMultipart();

        // 设置文本消息部分
        multipart.addBodyPart(messageBodyPart);

        if (!isEmpty(filename)) {
            // 附件部分
            messageBodyPart = new MimeBodyPart();
            // 设置要发送附件的文件路径
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            // messageBodyPart.setFileName(filename);
            // 处理附件名称中文（附带文件路径）乱码问题
            String fileName = MimeUtility.encodeText(source.getName());
            if (!isEmpty(reName)) {
                fileName = reName;
            }
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);
        }


        // 发送完整消息
        message.setContent(multipart);

        // 发送消息
        Transport.send(message);
        return true;
    }
}
