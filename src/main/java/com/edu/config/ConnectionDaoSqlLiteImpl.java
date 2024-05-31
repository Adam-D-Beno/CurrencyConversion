package com.edu.config;

import com.edu.utils.PropertiesUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionDaoSqlLiteImpl implements ConnectionDao{

  private static final String url = "db.url";
  private static final String driver = "db.driver";

  static {
    loadDriver();
  }

    @Override
    public Connection getConnectionDB() throws SQLException {
        return  DriverManager.getConnection(PropertiesUtil.get(url));
    }

    //todo replace on throw
    public static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(driver));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
