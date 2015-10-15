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

public class Monitor {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Timer timer = new Timer();
        MyTask secondTask = new MyTask(2);
        timer.schedule(secondTask, 1000, Long.parseLong(GetConfig.rb.getString("interval")));
        // 1秒后启动任务,以后每隔10秒执行一次线程
        //Mail oramail = new Mail("ERROR! the oracle has ORA-","这是一个测试");
       // oramail.SendMail();
    }
}
