package com.g01.connection;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

public abstract class Connection implements AutoCloseable {
    protected java.sql.Connection con;

    protected String entity;

    protected String fields = "";

    protected final DatePickerSettings DP_SETTINGS = new DatePickerSettings();

    public Connection(String entity) {
        this.entity = entity;
        DP_SETTINGS.setSizeDatePanelMinimumHeight(200);
        DP_SETTINGS.setFormatForDatesCommonEra("yyyy-MM-dd");
        DP_SETTINGS.setHighlightPolicy(localDate -> {
            String dotw = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            boolean isWeekend = dotw.equals("Sat") || dotw.equals("Sun");

            return new HighlightInformation(isWeekend ? new Color(230, 230, 230) : new Color(255, 255, 255));
        });
    }

    public Connection(String entity, String fields) {
        this.entity = entity;
        this.fields = fields;
        DP_SETTINGS.setSizeDatePanelMinimumHeight(200);
        DP_SETTINGS.setFormatForDatesCommonEra("yyyy-MM-dd");
        DP_SETTINGS.setHighlightPolicy(localDate -> {
            String dotw = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            boolean isWeekend = dotw.equals("Sat") || dotw.equals("Sun");

            return new HighlightInformation(isWeekend ? new Color(230, 230, 230) : new Color(255, 255, 255));
        });
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
        if (con != null)
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

    public void insert(String... values) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Statement statement = con.createStatement();

        for (String value : values)
            sb.append(", ").append('\'').append(value).append('\'');

        statement.executeUpdate("INSERT INTO " + entity + fields + " VALUES (" + sb.substring(2) + ")");
    }

    public abstract void insertForm() throws SQLException;

    public abstract void delete(String id) throws SQLException;
}