package com.g01;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

public class Connection {
    protected java.sql.Connection con;

    protected boolean connect(ConnectionInfo info) {
        Properties properties = new Properties();
        properties.put("user", info.getUser());
        properties.put("password", info.getPassword());

        try {
            con = DriverManager.getConnection(info.getUrl(), properties);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    public ResultSet getEntries(String entity, String... columns) throws SQLException {
        Statement statement = con.createStatement();
        String colString = columns.length != 0 ? Arrays.toString(columns).substring(1, Arrays.toString(columns).lastIndexOf(']') - 1) : "*";

        return statement.executeQuery("SELECT " + colString + " FROM " + entity);
    }
}