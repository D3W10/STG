package com.g01.connection.models;

import com.g01.connection.Connection;
import com.g01.connection.ConstantServe;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

import static javax.swing.JOptionPane.*;

public class Notificacao extends Connection {
    @Override
    public ResultSet getEntry(Object id) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getEntries() throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("SELECT * FROM NOTIFICACAO ORDER BY NumeroProcesso, CodigoNotificacao");
    }

    @Override
    public void form(Object... defaults) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JTextField nif = new JTextField();
        nif.setBorder(new TitledBorder("NIF do Condutor"));
        nif.setColumns(12);
        constraints.gridy = 0;
        panel.add(nif, constraints);

        int res = showOptionDialog(null, panel, "Gerar Segunda Via", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{"Gerar"}, "Gerar");

        if (res >= 0) {
            if (nif.getText().isBlank() || !nif.getText().matches("^\\d+$"))
                showMessageDialog(null, "Por favor forneça um NIF válido.", "Erro a gerar", JOptionPane.ERROR_MESSAGE);
            else
                insert(Integer.parseInt(nif.getText()), (Integer) defaults[0]);
        }
    }

    public void formComunicacao(int numProc, int codNoti) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        DatePicker dataComunicacao = new DatePicker(ConstantServe.getDatePickerSettings());
        dataComunicacao.setBorder(new TitledBorder("Data de Comunicação"));
        constraints.gridy = 0;
        panel.add(dataComunicacao, constraints);

        int res = showOptionDialog(null, panel, "Definir Comunicação", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{"Definir"}, "Definir");

        if (res >= 0) {
            if (dataComunicacao.getText().isBlank())
                showMessageDialog(null, "Por favor forneça uma data de comunicação válida.", "Erro a definir", JOptionPane.ERROR_MESSAGE);
            else
                communicate(numProc, codNoti, dataComunicacao.getDate());
        }
    }

    public void insert(int nif, int processNum) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT count(*) FROM CONDUTOR where Nif = ?");

        statement.setInt(1, nif);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next() && resultSet.getInt(1) > 0) {
            statement = con.prepareStatement("INSERT INTO NOTIFICACAO(NumeroProcesso, DataEnvio, DataComunicacao, DataLiquidacao, Condutor, PropTitular) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setInt(1, processNum);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setNull(3, Types.DATE);
            statement.setNull(4, Types.DATE);
            statement.setInt(5, nif);
            statement.setNull(6, Types.INTEGER);
            statement.executeUpdate();
        }
        else
            showMessageDialog(null, "O NIF não corresponde a nenhum condutor ou o veículo não tem um proprietário titular associado.", "Erro a gerar", JOptionPane.ERROR_MESSAGE);
    }

    public void communicate(int numProc, int codNoti, LocalDate dataComunicacao) throws SQLException {
        PreparedStatement statement = con.prepareStatement("UPDATE NOTIFICACAO SET DataComunicacao = ? WHERE NumeroProcesso = ? AND CodigoNotificacao = ?");

        statement.setDate(1, Date.valueOf(dataComunicacao));
        statement.setInt(2, numProc);
        statement.setInt(3, codNoti);
        statement.executeUpdate();
    }

    public void pay(int numProc, int codNoti) throws SQLException {
        PreparedStatement statement = con.prepareStatement("UPDATE NOTIFICACAO SET DataLiquidacao = ? WHERE NumeroProcesso = ? AND CodigoNotificacao = ?");

        statement.setDate(1, Date.valueOf(LocalDate.now()));
        statement.setInt(2, numProc);
        statement.setInt(3, codNoti);
        statement.executeUpdate();
    }

    @Override
    public void delete(Object id) throws SQLException {}

    public void deleteNoti(Object numProc, Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("DELETE FROM NOTIFICACAO WHERE NumeroProcesso = ? AND CodigoNotificacao = ?");

        statement.setInt(1, Integer.parseInt((String) numProc));
        statement.setInt(2, Integer.parseInt((String) id));

        statement.executeUpdate();
    }
}
