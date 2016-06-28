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

    public static void main(String[] args) throws SQLException {
        PropertyConfigurator.configure(args[0] + "log4j.properties");
        ResourceBundle rb = GetConfig.getfile(args[0]);
        Timer timer = new Timer();
        PoolDataSource pds = GetUcp.getucppool(args[0]);
        MyTask secondTask = new MyTask(2, args[0], rb, pds);
        logger.info("************Start to run monitor job!*********************");
        timer.schedule(secondTask, 1000, Long.parseLong(rb.getString("interval")));
    }
}
