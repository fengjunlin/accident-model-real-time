package com.fengjunlin.accident.dao.hbase;

import com.fengjunlin.accident.utils.LogManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * @Description 成为obd，midas，歌途云镜Hbase,两客一危Hbase配置文件
 * @Author fengjl
 * @Date 2019/6/26 10:18
 * @Version 1.0
 **/
@Component
@SuppressWarnings("all")
public class HbaseConfig {

    @Value("${hbase.zookeeper.quorum}")
    private String zkQuorum;

    @Value("${hbase.master}")
    private String hbaseMaster;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String zkPort;

    @Value("${zookeeper.znode.parent}")
    private String znode;
    /**
     * 成为obd，midas，歌途云镜Hbase集群配置
     */
    private Configuration conf;
    /**
     * 成为obd，midas，歌途云镜Hbase集群连接
     */
    private Connection connection;

    /**
     * 获取成为obd，midas，歌途云镜Hbase集群连接
     *
     * @return 连接
     */
    public Connection getHbaseConnection() {
        if (null != connection) {
            return connection;
        } else {
            return createHbaseConnection();
        }
    }

    private Connection createHbaseConnection() {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", zkQuorum);
        conf.set("hbase.zookeeper.property.clientPort", zkPort);
        conf.set("zookeeper.znode.parent", znode);
        conf.set("hbase.master", hbaseMaster);

        try {
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
            LogManager.error(e, HbaseConfig.class);
        }
        return connection;
    }

}
