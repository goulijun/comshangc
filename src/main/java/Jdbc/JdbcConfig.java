package Jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.DataSourceProvider;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname JdbcConfig
 * @Auuthor goulijun
 * @Descripton // TODO
 * @Date 2019/2/13 12:10
 * @Version v1.0
 */

public class JdbcConfig implements DataSourceProvider {

    @Override
    public int maximumPoolSize(DataSource dataSource, JsonObject jsonObject) throws SQLException {
        return 0;
    }

    @Override
    public DataSource getDataSource(JsonObject config) throws SQLException {

        DruidDataSource ds = new DruidDataSource();
        Method[] methods = DruidDataSource.class.getMethods();
        Map<String,Method> methodmap = new HashMap<>();
        for (Method method : methods) {
            methodmap.put(method.getName(),method);
        }

        for (Map.Entry<String, Object> entry : config) {
            String name = entry.getKey();

            if ("provider_class".equals(name)) {
                continue;
            }
            String mName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

            try {
                Class paramClazz = entry.getValue().getClass();
                if(paramClazz.equals(Integer.class)){
                    paramClazz = int.class;
                }else if(paramClazz.equals(Long.class)){
                    paramClazz = long.class;
                }else if(paramClazz.equals(Boolean.class)){
                    paramClazz = boolean.class;
                }
                Method method = DruidDataSource.class.getMethod(mName, paramClazz);
                method.invoke(ds, entry.getValue());
            } catch (NoSuchMethodException e) {
                System.out.println(entry.getValue().getClass());
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return ds;
    }

    @Override
    public void close(DataSource dataSource) throws SQLException {

    }
}
