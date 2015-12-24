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
import java.util.Timer;
import org.apache.log4j.*;

public class Monitor {

    /**
     *
     * @param args
     */
    private static Logger logger = Logger.getLogger(Monitor.class);
    public static void main(String[] args) {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/config/log4j.properties");
        Timer timer = new Timer();
        MyTask secondTask = new MyTask(2);
        logger.info("************Start to run monitor job!*********************");
        timer.schedule(secondTask, 1000, Long.parseLong(GetConfig.rb.getString("interval")));
        // 1秒后启动任务,以后每隔10秒执行一次线程
        //Mail oramail = new Mail("ERROR! the oracle has ORA-","这是一个测试");
       // oramail.SendMail();
    }
}
