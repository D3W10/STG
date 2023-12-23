package com.g01.connection.models;

import com.g01.connection.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalCtrlTransito extends Connection {
    public LocalCtrlTransito() {
        super("LOCAL_CTRL_TRANSITO");
    }

    public ResultSet delete(String id) throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("DELETE FROM " + entity + " WHERE CoordGeografica = '" + id + "'");
    }
}