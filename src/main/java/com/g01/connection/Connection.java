package com.g01.connection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

public abstract class Connection implements AutoCloseable {
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
     * Inicia uma nova conexão à base de dados.
     *
     * @param info as informações relativas à base de dados
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    public void connect(ConnectionInfo info) throws SQLException {
        Properties properties = new Properties();
        properties.put("user", info.getUser());
        properties.put("password", info.getPassword());

        con = DriverManager.getConnection(info.getUrl(), properties);
    }

    /**
     * Fecha a conexão à base de dadps.
     *
     * @throws SQLException se ocorreu um erro na conexão à base de dados
     * */
    @Override
    public void close() throws SQLException {
        con.close();
    }

    public DefaultTableModel getTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Vector<String> columns = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        for (int column = 1; column <= metaData.getColumnCount(); column++)
            columns.add(metaData.getColumnName(column).substring(0, 1).toUpperCase() + metaData.getColumnName(column).substring(1));

        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();

            for (int i = 1; i <= metaData.getColumnCount(); i++)
                vector.add(resultSet.getObject(i));

            data.add(vector);
        }

        return new DefaultTableModel(data, columns);
    }

    public ResultSet getEntries(String... columns) throws SQLException {
        Statement statement = con.createStatement();
        String colString = columns != null ? Arrays.toString(columns).substring(1, Arrays.toString(columns).lastIndexOf(']') - 1) : "*";

        return statement.executeQuery("SELECT " + colString + " FROM " + entity);
    }

    public ResultSet insert(String... values) throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("INSERT INTO " + entity + fields + " VALUES (" + values + ")");
    }
}