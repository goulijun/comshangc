package contcoller;

import Jdbc.JdbcConfig;
import Jdbc.JdbcUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Lianjie1  {
//    public  void jsbc(Vertx vertx) throws SQLException {
//
//
//
//            // 获取到数据库连接的客户端
//            JDBCClient dbClient = new JdbcUtils( vertx ).getDbClient();
//
//            // 构造参数
//
//            dbClient.getConnection(res -> {
//                if (res.succeeded()) {
//
//                    SQLConnection connection = res.result();
//
//                    connection.query("SELECT * FROM login", res2 -> {
//                        if (res2.succeeded()) {
//
//                            ResultSet rs = res2.result();
//                            // 用结果集results进行其他操作
//                            System.out.println(  rs.getResults() );
//
//
//                        }
//                    });
//                } else {
//                    // 获取连接失败 - 处理失败的情况
//                    System.out.println( "123" );
//                }
//            });
//    }
    public void as1(Router router){


    Route route = router.post( "/some/path/" );

            route.handler( routingContext -> {
                //        // 所有以下路径的请求都会调用这个处理器:

        // `/some/path`
        // `/some/path/`
        // `/some/path//`
        //
        // 但不包括：
        // `/some/path/subdir`
                HttpServerRequest request = routingContext.request();

                System.out.println(   request.getParam( "g" ));
        HttpServerResponse response = routingContext.response();

        response.putHeader("content-type", "text/plain");
        // 由于我们会在不同的处理器里写入响应，因此需要启用分块传输
        // 仅当需要通过多个处理器输出响应时才需要
        response.setChunked(true);
        response.write("/some/path/");

        // 结束响应
        routingContext.response().end();
    } );

    }
}
