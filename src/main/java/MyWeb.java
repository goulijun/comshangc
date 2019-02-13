

import Jdbc.JdbcUtils;
import contcoller.Lianjie1;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;


public class MyWeb extends AbstractVerticle {




    @Override
    public void start()  {
//        JsonObject config = new JsonObject()
//                .put("url", "jdbc:mysql://localhost:3306/oxbridge_official_tech_db?characterEncoding=utf-8&useSSL=true")
//                .put("user", "root")
//                .put("password", "123456")
//                .put("driver_class", "com.mysql.jdbc.Driver")
//                .put("max_pool_size", 30);
//
//        // 创建客户端
//        dbClient = JDBCClient.createShared(vertx, config);
        JdbcUtils jdbcUtils = new JdbcUtils( vertx );
        JDBCClient dbClient = jdbcUtils.getDbClient();
        dbClient.getConnection(res -> {
            if (res.succeeded()) {

                SQLConnection connection = res.result();

                connection.query("SELECT * FROM login", res2 -> {
                    if (res2.succeeded()) {

                        ResultSet rs = res2.result();
                        // Do something with resultsd
                        System.out.println( "成功" );
                        System.out.println( rs.getRows() );
                    }
                });
            } else {
                // Failed to get connection - deal with it
                System.out.println( "失败" );
            }
        });


            Lianjie1 lianjie1 = new Lianjie1();
            HttpServer server = vertx.createHttpServer();
            Router router =  Router.router(vertx);
             lianjie1.as1( router );


        server.requestHandler(router::accept).listen(8080);


        }
}