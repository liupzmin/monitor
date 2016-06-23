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
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.apache.log4j.*;

public class Monitor {

    /**
     *
     * @param args
     */
    private static Logger logger = Logger.getLogger(Monitor.class);
    public static void main(String[] args) {
        PropertyConfigurator.configure(args[0] + "log4j.properties");
        ResourceBundle rb=GetConfig.getfile(args[0]);
       // System.out.println(args[0]);
        Timer timer = new Timer();
        try{
            //创建UCP连接池
            PoolDataSource  pds = PoolDataSourceFactory.getPoolDataSource();
            //设置连接属性
            pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            String poolurl="jdbc:oracle:" + "thin:@" + rb.getString("server") + ":" + rb.getString("port") + ":" + rb.getString("servicename");
            String pooluser = rb.getString("username");// 用户名,系统默认的账户名
            String pollpassword = rb.getString("password");
            pds.setURL(poolurl);
            pds.setUser(pooluser);
            pds.setPassword(pollpassword);
            pds.setInitialPoolSize(5);
            
            MyTask secondTask = new MyTask(2,args[0],rb,pds);
            logger.info("************Start to run monitor job!*********************");
            timer.schedule(secondTask, 1000, Long.parseLong(rb.getString("interval")));
        }
        catch(SQLException e)
        {
         System.out.println("BasicConnectionExample - " +
          "main()-SQLException occurred : "
              + e.getMessage());
        }


        // 1秒后启动任务,以后每隔10秒执行一次线程
        //Mail oramail = new Mail("ERROR! the oracle has ORA-","这是一个测试");
       // oramail.SendMail();
    }
}
