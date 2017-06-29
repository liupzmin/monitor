/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

/**
 *
 * @author Administrator
 */
import java.util.*;
import java.util.TimerTask;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.io.*;
import org.apache.log4j.*;
import java.util.ResourceBundle;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSource;

public class MyTask extends TimerTask {

    private final int id;
    private String path;
    private int position;
    private static int lastPostion;
    private Mail oramail;
    private String content;
    private static Logger logger = Logger.getLogger(MyTask.class);
    private ResourceBundle rb;
    private PoolDataSource pds;

    public MyTask(int id, String path, ResourceBundle handle, PoolDataSource pool) throws SQLException {
        this.id = id;
        this.path = path;
        this.rb = handle;
        this.pds = pool;
        PropertyConfigurator.configure(this.path + "log4j.properties");

    }

    //  @Override
    @SuppressWarnings("override")
    public void run() {
        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        //System.out.println(df.format(new java.util.Date()));// new Date()为获取当前系统时间
        try {
            checkAlert();
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
    }

    public void checkAlert() throws SQLException {
        Connection conn = pds.getConnection();// 创建一个数据库连接
        PreparedStatement stmt = null;
        ResultSet result = null;// 创建一个结果集对象
        /*
        int avlConnCount = pds.getAvailableConnectionsCount();
        System.out.println("\nAvailable connections: " + avlConnCount);
        int brwConnCount = pds.getBorrowedConnectionsCount();
        System.out.println("\nBorrowed connections: " + brwConnCount);
         */
        try {

            String sql = "select o.rid,o.content,max(rid) over() lastpostion,min(rid) over() headerposition"
                    + " from (select rownum rid, a.* from " + rb.getString("tablename") + " a) o,"
                    + " (select count(*) cn from " + rb.getString("tablename")
                    + ") ca"
                    + " where o.rid > ca.cn - ?";// 预编译语句，“？”代表参数
            //System.out.println(sql);
            stmt = conn.prepareStatement(sql);// 实例化预编译语句
            stmt.setInt(1, Integer.parseInt(rb.getString("lines")));// 设置参数，前面的1表示参数的索引，而不是表中列名的索引
            result = stmt.executeQuery();// 执行查询，注意括号中不需要再加参数
            //如果是第一次运行最后位置应该是0，那么设置末尾行数，直接打印尾部lines行
            if (MyTask.lastPostion == 0) {
                while (result.next()) // 当结果集不为空时
                {
                    if (MyTask.lastPostion == 0) {
                        MyTask.lastPostion = result.getInt("lastpostion");
                    }
                    this.content = result.getString("content");
                    if (this.content != null) {
                        //遇到有ORA-错误的行则发送邮件
                        if (this.content.substring(0, 4).equals("ORA-")) {
                            this.oramail = new Mail("ERROR! the " + rb.getString("server") + " oracle has ORA", result.getString("content"));
                            this.oramail.SendMail();
                        }
                        //System.out.println("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                        logger.info("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                    } else {
                        //System.out.println("rownum:" + result.getInt("rid") + " content:" + "");
                        logger.info("rownum:" + result.getInt("rid") + " content:" + "");
                    }

                }
            } else {
                //如果lastpostion不为0，则为循环检查，每次打印上次打印的末尾行之后的
                result.next();//游标向前滚动，获取lastpostion
                this.position = result.getInt("lastpostion");//设置私有position为获取lastpostion
                //判断本次获取的lines行是否和上一次的lastpostion之间有间隔
                if (MyTask.lastPostion < result.getInt("headerposition") - 1) {
                    //如果有间隔就打印一串标识符
                    System.out.println("...............");
                    logger.info("...............");
                    //判断邮件发送
                    this.content = result.getString("content");
                    if (this.content != null) {
                        //遇到有ORA-错误的行则发送邮件
                        if (this.content.substring(0, 4).equals("ORA-")) {
                            this.oramail = new Mail("ERROR! the " + rb.getString("server") + " oracle has ORA", result.getString("content"));
                            this.oramail.SendMail();
                        }
                        //有间隔了这一行需要打印出来，否则可以为了获取lastpostion而牺牲掉这一行，进入下面的while判断是否打印
                        //System.out.println("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                        logger.info("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                    } else {
                        // System.out.println("rownum:" + result.getInt("rid") + " content:" + "");
                        logger.info("rownum:" + result.getInt("rid") + " content:" + "");
                    }
                }
                while (result.next()) {
                    //如果本行位置大于上一次的lastpostion，则打印
                    if (result.getInt("rid") > MyTask.lastPostion) {
                        //判断发送邮件
                        this.content = result.getString("content");
                        if (this.content != null) {
                            //遇到有ORA-错误的行则发送邮件
                            if (this.content.substring(0, 4).equals("ORA-")) {
                                this.oramail = new Mail("ERROR! the " + rb.getString("server") + " oracle has ORA", result.getString("content"));
                                this.oramail.SendMail();
                            }
                            //System.out.println("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                            logger.info("rownum:" + result.getInt("rid") + " content:" + result.getString("content"));
                        } else {
                            //System.out.println("rownum:" + result.getInt("rid") + " content:" + "");
                            logger.info("rownum:" + result.getInt("rid") + " content:" + "");
                        }
                        //每次循环都设定私有postion
                        this.position = result.getInt("lastpostion");
                    }
                }
                //循环完毕设置本次任务lastPostion
                MyTask.lastPostion = this.position;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        } finally {
            try {
                // 逐一将上面的几个对象关闭，因为不关闭的话会影响性能、并且占用资源
                // 注意关闭的顺序，最后使用的最先关闭
                if (result != null) {
                    result.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                    conn = null;
                }
                /*
                avlConnCount = pds.getAvailableConnectionsCount();
                System.out.println("\nAvailable connections: " + avlConnCount);
                brwConnCount = pds.getBorrowedConnectionsCount();
                System.out.println("\nBorrowed connections: " + brwConnCount);
                 */
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
    }
}
