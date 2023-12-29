package com.g01.connection.models;

import com.g01.connection.Connection;

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

        return statement.executeQuery("SELECT * FROM NOTIFICACAO");
    }

    @Override
    public void form(Object... defaults) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JTextField processNum = new JTextField();
        processNum.setBorder(new TitledBorder("Número de Processo"));
        processNum.setColumns(12);
        constraints.gridy = 0;
        panel.add(processNum, constraints);

        int res = showOptionDialog(null, panel, "Gerar", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{"Gerar"}, "Gerar");

        if (res >= 0) {
            if (processNum.getText().isBlank() || !processNum.getText().matches("^\\d+$"))
                showMessageDialog(null, "Por favor forneça um número de processo válido.", "Erro a gerar", JOptionPane.ERROR_MESSAGE);
            else
                insert(Integer.parseInt(processNum.getText()));
        }
    }

    public void secondForm(int processNum) throws SQLException {
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
                insertSegundaVia(Integer.parseInt(nif.getText()), processNum);
        }
    }

    public void insert(int processNum) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT PROP_TITULAR.Nif FROM INFRACAO INNER JOIN VEICULO ON VEICULO.Matricula = INFRACAO.NumeroAuto INNER JOIN PROPRIETARIO_VEICULO ON VEICULO.Matricula = PROPRIETARIO_VEICULO.Matricula INNER JOIN PROP_TITULAR ON PROPRIETARIO_VEICULO.Nif = PROP_TITULAR.Nif where NumeroProcesso = ?");

        statement.setInt(1, processNum);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int prop = resultSet.getInt(1);

            statement = con.prepareStatement("INSERT INTO NOTIFICACAO(NumeroProcesso, DataEnvio, DataComunicacao, DataLiquidacao, Condutor, PropTitular) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setInt(1, processNum);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setNull(3, Types.DATE);
            statement.setNull(4, Types.DATE);
            statement.setNull(5, Types.INTEGER);
            statement.setInt(6, prop);
            statement.executeUpdate();
        }
        else
            showMessageDialog(null, "O número de processo não corresponde a nenhuma infração ou o veículo não tem um proprietário titular associado.", "Erro a gerar", JOptionPane.ERROR_MESSAGE);
    }

    public void insertSegundaVia(int nif, int processNum) throws SQLException {
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

    @Override
    public void delete(Object id) throws SQLException {

    }

    public void deleteNoti(Object numProc, Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("DELETE FROM NOTIFICACAO WHERE NumeroProcesso = ? AND CodigoNotificacao = ?");

        statement.setInt(1, Integer.parseInt((String) numProc));
        statement.setInt(2, Integer.parseInt((String) id));

        statement.executeUpdate();
    }
}
