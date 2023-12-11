package com.g01;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

public class Connection {
    protected java.sql.Connection connect(ConnectionInfo info) throws SQLException {
        Properties properties = new Properties();
        properties.put("user", info.getUser());
        properties.put("password", info.getPassword());

        return DriverManager.getConnection(info.getUrl(), properties);
    }

    public ResultSet getEntries(java.sql.Connection connection, String entity, String... columns) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String colString = Arrays.toString(columns).substring(1, Arrays.toString(columns).lastIndexOf(']') - 1);

            return statement.executeQuery("SELECT " + colString + " FROM " + entity);
        }
    }
}