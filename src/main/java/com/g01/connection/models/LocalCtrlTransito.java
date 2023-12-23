package com.g01.connection.models;

import com.g01.connection.Connection;

import java.sql.SQLException;
import java.sql.Statement;

public class LocalCtrlTransito extends Connection {
    public LocalCtrlTransito() {
        super("LOCAL_CTRL_TRANSITO");
    }

    public int delete(String id) throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeUpdate("DELETE FROM " + entity + " WHERE CoordGeografica = '" + id + "'");
    }
}