/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.sql.SQLException;
import java.util.ResourceBundle;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolDataSource;
import org.apache.log4j.*;

/**
 *
 * @author liupeng
 */
public class GetUcp {

    private static PoolDataSource pds;
    private static final Logger logger = Logger.getLogger(MyTask.class);
    private static ResourceBundle rb;

    public static PoolDataSource getucppool(String path) throws SQLException {
        try {

            GetUcp.rb = GetConfig.getfile(path);
            pds = PoolDataSourceFactory.getPoolDataSource();
            pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
            String poolurl = "jdbc:oracle:" + "thin:@" + rb.getString("server") + ":" + rb.getString("port") + ":" + rb.getString("servicename");
            String pooluser = rb.getString("username");// 用户名,系统默认的账户名
            String pollpassword = rb.getString("password");
            pds.setURL(poolurl);
            pds.setUser(pooluser);
            pds.setPassword(pollpassword);
            pds.setInitialPoolSize(3);

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return pds;
    }
}
