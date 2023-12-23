package com.g01.connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

public abstract class Connection {
    protected java.sql.Connection con;

    protected String entity;

    protected String fields = "";

    public Connection(String entity) {
        this.entity = entity;
    }

    public Connection(String entity, String fields) {
        this.entity = entity;
        this.fields = fields;
    }

    /**
     * Inicia uma nova conexão à base de dados
     *
     * @param info As informações relativas à base de dados
     *
     * @return Devolve {@literal true} se a conexão foi efetuada com sucesso, {@literal false} caso contrário.
     * */
    public boolean connect(ConnectionInfo info) {
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

    /**
     * Fecha a conexão à base de dadps
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    public void close() throws SQLException {
        con.close();
    }

    public ResultSet getEntries(String... columns) throws SQLException {
        Statement statement = con.createStatement();
        String colString = columns.length != 0 ? Arrays.toString(columns).substring(1, Arrays.toString(columns).lastIndexOf(']') - 1) : "*";

        return statement.executeQuery("SELECT " + colString + " FROM " + entity);
    }

    public ResultSet create(String... values) throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("INSERT INTO " + entity + fields + " VALUES (" + values + ")");
    }
}