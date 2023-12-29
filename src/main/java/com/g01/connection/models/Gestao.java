package com.g01.connection.models;

import com.g01.connection.Connection;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

import static javax.swing.JOptionPane.*;
import static javax.swing.JOptionPane.showMessageDialog;

public class Gestao extends Connection {
    @Override
    public ResultSet getEntry(Object id) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getEntries() throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("SELECT IdEvento, Evidencia, Identificacao, NumeroAuto, COUNT(NOTIFICACAO.CodigoNotificacao) AS \"Qtd. Notificações\" FROM EVENTO_TRANSITO FULL OUTER JOIN INFRACAO ON EVENTO_TRANSITO.infracao = INFRACAO.NumeroProcesso LEFT JOIN NOTIFICACAO ON INFRACAO.NumeroProcesso = NOTIFICACAO.NumeroProcesso GROUP BY IdEvento, INFRACAO.NumeroProcesso ORDER BY IdEvento");
    }

    @Override
    public void form(Object... defaults) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JTextField matricula = new JTextField();
        matricula.setBorder(new TitledBorder("Matrícula"));
        matricula.setColumns(12);
        constraints.gridy = 0;
        panel.add(matricula, constraints);

        int res = showOptionDialog(null, panel, "Associar", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{"Associar"}, "Associar");

        if (res >= 0) {
            if (matricula.getText().isBlank())
                showMessageDialog(null, "Por favor forneça uma matrícula válida.", "Erro a associar", JOptionPane.ERROR_MESSAGE);
            else
                insert((Integer) defaults[0], matricula.getText());
        }
    }

    public void insert(int idEvento, String matricula) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT PROPRIETARIO_VEICULO.Nif FROM PROPRIETARIO_VEICULO INNER JOIN PROP_TITULAR ON PROPRIETARIO_VEICULO.Nif = PROP_TITULAR.Nif WHERE Matricula = ?");

        statement.setString(1, matricula);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int nif = resultSet.getInt(1);
            statement = con.prepareStatement("INSERT INTO INFRACAO(DataCriacao, NumeroAuto) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setString(2, matricula);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            resultSet.next();
            int numProc = resultSet.getInt(1);

            statement = con.prepareStatement("INSERT INTO NOTIFICACAO(NumeroProcesso, DataEnvio, DataComunicacao, DataLiquidacao, Condutor, PropTitular) VALUES (?, ?, ?, ?, ?, ?)");

            statement.setInt(1, numProc);
            statement.setDate(2, Date.valueOf(LocalDate.now()));
            statement.setNull(3, Types.DATE);
            statement.setNull(4, Types.DATE);
            statement.setNull(5, Types.INTEGER);
            statement.setInt(6, nif);
            statement.executeUpdate();

            statement = con.prepareStatement("UPDATE EVENTO_TRANSITO SET Infracao = ? WHERE IdEvento = ?");

            statement.setInt(1, numProc);
            statement.setInt(2, idEvento);
            statement.executeUpdate();
        }
        else
            showMessageDialog(null, "Não existe nenhum veículo associado a esta matrícula.", "Erro a associar", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void delete(Object id) throws SQLException {

    }
}
