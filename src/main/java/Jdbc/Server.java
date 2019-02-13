/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package Jdbc;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.junit.runner.Runner;

/**
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class Server extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle( Server.class.getName());
  }

  private JDBCClient client;

  @Override
  public void start() {

    Server that = this;

    // Create a JDBC client with a test database
    //使用测试数据库创建JDBC客户端

      client = JDBCClient.createShared(vertx, new JsonObject()
              .put("url", "jdbc:mysql://localhost:3306/oxbridge_official_tech_db?characterEncoding=utf-8&useSSL=true")
              .put("user", "root")
              .put("password", "123456")
              .put("driver_class", "com.mysql.jdbc.Driver")
              .put("max_pool_size", 30));

    setUpInitialData(ready -> {
      Router router = Router.router(vertx);

      router.route().handler(BodyHandler.create());

      //为了最小化回调的嵌套，我们可以将JDBC连接放在所有路由的上下文中
      // that match /products
      //这应该真的被封装在一个可重用的JDBC处理程序中，使用它只需添加到他们的应用程序中
      router.route("/products*").handler(routingContext -> client.getConnection(res -> {
        if (res.failed()) {
          routingContext.fail(res.cause());
        } else {
          SQLConnection conn = res.result();

          //保存上下文中的连接
          routingContext.put("conn", conn);

          //我们需要将连接返回给jdbc池。为了做到这一点，我们需要关闭它，保持
          // 剩下的代码可读，可以添加一个头部结束处理程序来关闭连接。
          routingContext.addHeadersEndHandler(done -> conn.close(v -> { }));

          routingContext.next();
        }
      })).failureHandler(routingContext -> {
        SQLConnection conn = routingContext.get("conn");
        if (conn != null) {
          conn.close(v -> {
          });
        }
      });

      router.get("/products/:productID").handler(that::handleGetProduct);
      router.post("/products").handler(that::handleAddProduct);
      router.get("/products").handler(that::handleListProducts);

      vertx.createHttpServer().requestHandler(router).listen(8080);
    });
  }

  private void handleGetProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      SQLConnection conn = routingContext.get("conn");

      conn.queryWithParams("SELECT id, name, price, weight FROM products where id = ?", new JsonArray().add(Integer.parseInt(productID)), query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          if (query.result().getNumRows() == 0) {
            sendError(404, response);
          } else {
            response.putHeader("content-type", "application/json").end(query.result().getRows().get(0).encode());
          }
        }
      });
    }
  }

  private void handleAddProduct(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();

    SQLConnection conn = routingContext.get("conn");
    JsonObject product = routingContext.getBodyAsJson();

    conn.updateWithParams("INSERT INTO products (name, price, weight) VALUES (?, ?, ?)",
      new JsonArray().add(product.getString("name")).add(product.getFloat("price")).add(product.getInteger("weight")), query -> {
        if (query.failed()) {
          sendError(500, response);
        } else {
          response.end();
        }
      });
  }

  private void handleListProducts(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    SQLConnection conn = routingContext.get("conn");

    conn.query("SELECT id, name, price, weight FROM products", query -> {
      if (query.failed()) {
        sendError(500, response);
      } else {
        JsonArray arr = new JsonArray();
        query.result().getRows().forEach(arr::add);
        routingContext.response().putHeader("content-type", "application/json").end(arr.encode());
      }
    });
  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }

  private void setUpInitialData(Handler<Void> done) {
    client.getConnection(res -> {
      if (res.failed()) {
        throw new RuntimeException(res.cause());
      }

      final SQLConnection conn = res.result();

      conn.execute("CREATE TABLE IF NOT EXISTS products(id INT IDENTITY, name VARCHAR(255), price FLOAT, weight INT)", ddl -> {
        if (ddl.failed()) {
          throw new RuntimeException(ddl.cause());
        }

        conn.execute("INSERT INTO products (name, price, weight) VALUES ('Egg Whisk', 3.99, 150), ('Tea Cosy', 5.99, 100), ('Spatula', 1.00, 80)", fixtures -> {
          if (fixtures.failed()) {
            throw new RuntimeException(fixtures.cause());
          }

          done.handle(null);
        });
      });
    });
  }
}
