package Jdbc;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;


import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Classname JdbcUtil
 * @Auuthor goulijun
 * @Descripton // TODO
 * @Date 2019/2/13 14:47
 * @Version v1.0
 */


public class JdbcUtils {

    // 用于操作数据库的客户端
    private JDBCClient dbClient;

    public JdbcUtils(Vertx vertx) {

        JsonObject config = new JsonObject()
                .put("url", "jdbc:mysql://localhost:3306/oxbridge_official_tech_db?characterEncoding=utf-8&useSSL=true")
                .put("user", "root")
                .put("password", "123456")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("max_pool_size", 30);

        // 创建客户端
        dbClient = JDBCClient.createShared(vertx, config);

        // 创建客户端
        dbClient = JDBCClient.createShared(vertx, config);

    }

    // 提供一个公共方法来获取客户端
    public JDBCClient getDbClient() {
        return this.dbClient;
    }


}