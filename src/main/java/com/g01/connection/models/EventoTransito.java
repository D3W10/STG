package com.g01.connection.models;

import com.g01.connection.Connection;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

import static javax.swing.JOptionPane.*;

public class EventoTransito extends Connection {
    @Override
    public ResultSet getEntry(Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM EVENTO_TRANSITO WHERE IdEvento = ?");

        statement.setString(1, (String) id);

        ResultSet sa = statement.executeQuery();
        if (sa.next()) {
            String tipo = sa.getString("TipoEvento");

            if (tipo.equals("Velocidade Média"))
                statement = con.prepareStatement("SELECT EVENTO_TRANSITO.IdEvento, Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao, TipoMovimento, VelocidadeRegistada, Coordenada FROM EVENTO_TRANSITO INNER JOIN E_VELOCIDADE ON EVENTO_TRANSITO.IdEvento = E_VELOCIDADE.IdEvento INNER JOIN E_VELOCIDADE_MED ON EVENTO_TRANSITO.IdEvento = E_VELOCIDADE_MED.IdEvento WHERE EVENTO_TRANSITO.IdEvento = ?");
            else if (tipo.equals("Velocidade Instantânea"))
                statement = con.prepareStatement("SELECT EVENTO_TRANSITO.IdEvento, Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao, TipoMovimento, VelocidadeRegistada, Coordenada FROM EVENTO_TRANSITO INNER JOIN E_VELOCIDADE ON EVENTO_TRANSITO.IdEvento = E_VELOCIDADE.IdEvento INNER JOIN E_VELOCIDADE_INST ON EVENTO_TRANSITO.IdEvento = E_VELOCIDADE_INST.IdEvento WHERE EVENTO_TRANSITO.IdEvento = ?");
            else if (tipo.equals("Sinal Vermelho"))
                statement = con.prepareStatement("SELECT EVENTO_TRANSITO.IdEvento, Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao, TempoSinalizacao, Coordenada FROM EVENTO_TRANSITO INNER JOIN E_SINAL_VERMELHO ON EVENTO_TRANSITO.IdEvento = E_SINAL_VERMELHO.IdEvento WHERE EVENTO_TRANSITO.IdEvento = ?");

            return statement.executeQuery();
        }

        return null;
    }

    @Override
    public ResultSet getEntries() throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("SELECT * FROM EVENTO_TRANSITO");
    }

    @Override
    public void form(Object... defaults) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JTextArea evidencia = new JTextArea(5, 0);
        evidencia.setBorder(new TitledBorder("Evidência"));
        evidencia.setEnabled(defaults.length == 0);
        constraints.gridy = 0;
        panel.add(new JScrollPane(evidencia), constraints);

        JTextField identificacao = new JTextField();
        identificacao.setBorder(new TitledBorder("Identificacao"));
        constraints.gridy = 1;
        panel.add(identificacao, constraints);

        DatePicker dia = new DatePicker(DP_SETTINGS);
        dia.setBorder(new TitledBorder("Dia"));
        constraints.gridy = 2;
        panel.add(dia, constraints);

        TimePicker hora = new TimePicker(TP_SETTINGS);
        hora.setBorder(new TitledBorder("Hora"));
        constraints.gridy = 3;
        panel.add(hora, constraints);

        JComboBox<String> tipoEvento = new JComboBox<>(new String[]{"Velocidade Média", "Velocidade Instantânea", "Sinal Vermelho"});
        constraints.gridy = 4;
        panel.add(tipoEvento, constraints);

        JPanel conditionalOptions = new JPanel();
        constraints.gridy = 5;
        panel.add(conditionalOptions, constraints);

        JTextField coordenada = new JTextField();
        coordenada.setBorder(new TitledBorder("Coordenada"));
        constraints.gridy = 6;
        panel.add(coordenada, constraints);

        var conditionalObj = new Object() {
            JComboBox<String> tipoMovimento = new JComboBox<>();
            JSpinner velocidadeRegistada = new JSpinner();
            JSpinner tempoSinalizacao = new JSpinner();
        };

        ActionListener onChange = e -> {
            conditionalOptions.removeAll();
            conditionalOptions.setLayout(new GridBagLayout());

            if (tipoEvento.getSelectedIndex() == 0 || tipoEvento.getSelectedIndex() == 1) {
                conditionalObj.tipoMovimento = new JComboBox<>(new String[]{"Aproximação", "Afastamento"});
                constraints.gridy = 0;
                conditionalOptions.add(conditionalObj.tipoMovimento, constraints);

                conditionalObj.velocidadeRegistada = new JSpinner(new SpinnerNumberModel(90, 1, 200, 5));
                conditionalObj.velocidadeRegistada.setBorder(new TitledBorder("Velocidade Registada"));
                constraints.gridy = 1;
                conditionalOptions.add(conditionalObj.velocidadeRegistada, constraints);
            }
            else if (tipoEvento.getSelectedIndex() == 2) {
                conditionalObj.tempoSinalizacao = new JSpinner(new SpinnerNumberModel(40, 1, 180, 1));
                conditionalObj.tempoSinalizacao.setBorder(new TitledBorder("Tempo Sinalização"));
                constraints.gridy = 0;
                conditionalOptions.add(conditionalObj.tempoSinalizacao, constraints);
            }

            conditionalOptions.revalidate();
        };

        onChange.actionPerformed(null);
        tipoEvento.addActionListener(onChange);

        int res = showOptionDialog(null, new JScrollPane(panel), "Inserir", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{"Inserir"}, "Inserir");

        if (res >= 0) {
            if (dia.getText().isBlank())
                showMessageDialog(null, "Por favor forneça um dia válido.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
            else if (hora.getText().isBlank())
                showMessageDialog(null, "Por favor forneça uma hora válida.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
            else {
                int id = insert(evidencia.getText(), identificacao.getText(), Date.valueOf(dia.getDate()), Time.valueOf(hora.getTime()), (String) tipoEvento.getSelectedItem());

                if (tipoEvento.getSelectedIndex() == 0 || tipoEvento.getSelectedIndex() == 1)
                    insertVelocidade(tipoEvento.getSelectedIndex(), id, (String) conditionalObj.tipoMovimento.getSelectedItem(), (Integer) conditionalObj.velocidadeRegistada.getValue(), coordenada.getText());
                else
                    insertSinalVermelho(id, (Integer) conditionalObj.tempoSinalizacao.getValue(), coordenada.getText());
            }
        }
    }

    public int insert(String evidencia, String identificacao, Date dia, Time hora, String tipo) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO EVENTO_TRANSITO(Evidencia, Identificacao, Dia, Hora, TipoEvento, Infracao) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, evidencia);
        statement.setString(2, identificacao);
        statement.setDate(3, dia);
        statement.setTime(4, hora);
        statement.setString(5, tipo);
        statement.setNull(6, Types.INTEGER);
        statement.executeUpdate();

        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();

        return resultSet.getInt(1);
    }

    public void insertVelocidade(int type, int id, String tipoMovimento, int velocidadeRegistada, String coordenada) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO E_VELOCIDADE(TipoMovimento, VelocidadeRegistada, IdEvento) VALUES (?, ?, ?)");

        statement.setString(1, tipoMovimento);
        statement.setInt(2, velocidadeRegistada);
        statement.setInt(3, id);
        statement.executeUpdate();

        statement = con.prepareStatement("INSERT INTO " + (type == 0 ? "E_VELOCIDADE_MED" : "E_VELOCIDADE_INST") + "(Coordenada, IdEvento) VALUES (?, ?)");
        if (!coordenada.isBlank())
            statement.setString(1, coordenada);
        else
            statement.setNull(1, Types.VARCHAR);
        statement.setInt(2, id);
        statement.executeUpdate();
    }

    public void insertSinalVermelho(int id, int tempoSinalizacao, String coordenada) throws SQLException {
        PreparedStatement statement = con.prepareStatement("INSERT INTO E_SINAL_VERMELHO(TempoSinalizacao, Coordenada, IdEvento) VALUES (?, ?, ?)");

        statement.setInt(1, tempoSinalizacao);
        if (!coordenada.isBlank())
            statement.setString(2, coordenada);
        else
            statement.setNull(2, Types.VARCHAR);
        statement.setInt(3, id);
        statement.executeUpdate();
    }

    @Override
    public void delete(Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("DELETE FROM EVENTO_TRANSITO WHERE IdEvento = ?");

        statement.setString(1, (String) id);

        statement.executeUpdate();
    }
}