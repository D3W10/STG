package com.g01.connection.models;

import com.g01.connection.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Infracao extends Connection {
    private final LocalDate start;

    private final LocalDate end;

    private final int filter;

    public Infracao(LocalDate start, LocalDate end, int filter) {
        this.start = start;
        this.end = end;
        this.filter = filter;
    }

    @Override
    public ResultSet getEntry(Object id) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getEntries() throws SQLException {
        String suffix = filter == 1 ? " AND Condutor IS NULL" : (filter == 2 ? " AND Condutor IS NOT NULL" : "");
        PreparedStatement statement = con.prepareStatement("SELECT INFRACAO.NumeroProcesso, DataCriacao, NumeroAuto FROM INFRACAO INNER JOIN NOTIFICACAO ON INFRACAO.NumeroProcesso = NOTIFICACAO.NumeroProcesso WHERE DataCriacao > ? AND DataCriacao < ?" + suffix);

        statement.setDate(1, Date.valueOf(start));
        statement.setDate(2, Date.valueOf(end));

        return statement.executeQuery();
    }

    @Override
    public void form(Object... defaults) throws SQLException {}

    @Override
    public void delete(Object id) throws SQLException {}
}
