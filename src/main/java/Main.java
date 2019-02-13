import Jdbc.JdbcConfig;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Main {


        public static void main(String[] args){

            Vertx vertx = Vertx.vertx();

            vertx.deployVerticle(MyWeb.class.getName());

    }
}
