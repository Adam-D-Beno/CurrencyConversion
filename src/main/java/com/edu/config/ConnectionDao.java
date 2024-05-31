package com.edu.config;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionDao {

    Connection getConnectionDB() throws SQLException;

}
