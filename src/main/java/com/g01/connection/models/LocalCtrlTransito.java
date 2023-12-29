package com.g01.connection.models;

import com.g01.connection.Connection;
import com.g01.connection.ConstantServe;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

import static javax.swing.JOptionPane.*;

public class LocalCtrlTransito extends Connection {
    @Override
    public ResultSet getEntry(Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM LOCAL_CTRL_TRANSITO WHERE CoordGeografica = ?");

        statement.setString(1, (String) id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            String tipo = resultSet.getString("TipoLocal");

            if (tipo.equals("Velocidade Média"))
                statement = con.prepareStatement("SELECT LOCAL_CTRL_TRANSITO.CoordGeografica, TipoLocal, DataInstalacao, Comarca, Rodovia, Quilometro, VelocidadeLimite, ErroVelocidade, CoordenadaEntrada, CoordenadaSaida FROM LOCAL_CTRL_TRANSITO INNER JOIN L_VELOCIDADE ON LOCAL_CTRL_TRANSITO.CoordGeografica = L_VELOCIDADE.CoordGeografica INNER JOIN L_VELOCIDADE_MEDIA ON LOCAL_CTRL_TRANSITO.coordgeografica = L_VELOCIDADE_MEDIA.coordgeografica WHERE LOCAL_CTRL_TRANSITO.CoordGeografica = ?");
            else if (tipo.equals("Velocidade Instantânea"))
                statement = con.prepareStatement("SELECT LOCAL_CTRL_TRANSITO.CoordGeografica, TipoLocal, DataInstalacao, Comarca, Rodovia, Quilometro, VelocidadeLimite, ErroVelocidade FROM LOCAL_CTRL_TRANSITO INNER JOIN L_VELOCIDADE ON LOCAL_CTRL_TRANSITO.CoordGeografica = L_VELOCIDADE.CoordGeografica INNER JOIN L_VELOCIDADE_INSTANTANEA ON LOCAL_CTRL_TRANSITO.coordgeografica = L_VELOCIDADE_INSTANTANEA.coordgeografica WHERE LOCAL_CTRL_TRANSITO.CoordGeografica = ?");
            else if (tipo.equals("Sinal Vermelho"))
                statement = con.prepareStatement("SELECT LOCAL_CTRL_TRANSITO.CoordGeografica, TipoLocal, DataInstalacao, Comarca, Rodovia, Quilometro, Cruzamento, Municipio FROM LOCAL_CTRL_TRANSITO INNER JOIN L_SINAL_VERMELHO ON LOCAL_CTRL_TRANSITO.CoordGeografica = L_SINAL_VERMELHO.CoordGeografica WHERE LOCAL_CTRL_TRANSITO.CoordGeografica = ?");

            statement.setObject(1, id);
            return statement.executeQuery();
        }

        return null;
    }

    @Override
    public ResultSet getEntries() throws SQLException {
        Statement statement = con.createStatement();

        return statement.executeQuery("SELECT * FROM LOCAL_CTRL_TRANSITO");
    }

    @Override
    public void form(Object... defaults) throws SQLException {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        JTextField coordGeografica = new JTextField();
        coordGeografica.setBorder(new TitledBorder("Coordenada Geográfica"));
        coordGeografica.setEnabled(defaults.length == 0);
        if (defaults.length > 0)
            coordGeografica.setText((String) defaults[0]);
        constraints.gridy = 0;
        panel.add(coordGeografica, constraints);

        JComboBox<String> tipoLocal = new JComboBox<>(new String[]{"Velocidade Média", "Velocidade Instantânea", "Sinal Vermelho"});
        if (defaults.length > 1)
            tipoLocal.setSelectedItem(defaults[1]);
        constraints.gridy = 1;
        panel.add(tipoLocal, constraints);

        DatePicker dataInstalacao = new DatePicker(ConstantServe.getDatePickerSettings());
        dataInstalacao.setBorder(new TitledBorder("Data de Instalação"));
        if (defaults.length > 2)
            dataInstalacao.setDate(LocalDate.parse((String) defaults[2]));
        constraints.gridy = 2;
        panel.add(dataInstalacao, constraints);

        JTextField comarca = new JTextField();
        comarca.setBorder(new TitledBorder("Comarca"));
        if (defaults.length > 3)
            comarca.setText((String) defaults[3]);
        constraints.gridy = 3;
        panel.add(comarca, constraints);

        JTextField rodovia = new JTextField();
        rodovia.setBorder(new TitledBorder("Rodovia"));
        if (defaults.length > 4)
            rodovia.setText((String) defaults[4]);
        constraints.gridy = 4;
        panel.add(rodovia, constraints);

        JSpinner quilometro = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
        quilometro.setBorder(new TitledBorder("Quilómetro"));
        if (defaults.length > 5)
            quilometro.setValue(Integer.valueOf((String) defaults[5]));
        constraints.gridy = 5;
        panel.add(quilometro, constraints);

        JPanel conditionalOptions = new JPanel();
        constraints.gridy = 6;
        panel.add(conditionalOptions, constraints);

        var conditionalObj = new Object() {
            JSpinner velocidadeLimite = new JSpinner();
            JSpinner erroVelocidade = new JSpinner();
            JTextField coordenadaEntrada = new JTextField();
            JTextField coordenadaSaida = new JTextField();
            JTextField cruzamento = new JTextField();
            JTextField municipio = new JTextField();
        };

        ActionListener onChange = e -> {
            conditionalOptions.removeAll();
            conditionalOptions.setLayout(new GridBagLayout());

            if (tipoLocal.getSelectedIndex() == 0 || tipoLocal.getSelectedIndex() == 1) {
                conditionalObj.velocidadeLimite = new JSpinner(new SpinnerNumberModel(50, 1, 150, 5));
                conditionalObj.velocidadeLimite.setBorder(new TitledBorder("VelocidadeLimite"));
                if (defaults.length > 6)
                    conditionalObj.velocidadeLimite.setValue(Integer.valueOf((String) defaults[6]));
                constraints.gridy = 0;
                conditionalOptions.add(conditionalObj.velocidadeLimite, constraints);

                conditionalObj.erroVelocidade = new JSpinner(new SpinnerNumberModel(5, 0, 30, 1));
                conditionalObj.erroVelocidade.setBorder(new TitledBorder("ErroVelocidade"));
                if (defaults.length > 7)
                    conditionalObj.erroVelocidade.setValue(Integer.valueOf((String) defaults[7]));
                constraints.gridy = 1;
                conditionalOptions.add(conditionalObj.erroVelocidade, constraints);

                if (tipoLocal.getSelectedIndex() == 0) {
                    conditionalObj.coordenadaEntrada = new JTextField();
                    conditionalObj.coordenadaEntrada.setBorder(new TitledBorder("Coordenada de Entrada"));
                    if (defaults.length > 8)
                        conditionalObj.coordenadaEntrada.setText((String) defaults[8]);
                    constraints.gridy = 2;
                    conditionalOptions.add(conditionalObj.coordenadaEntrada, constraints);

                    conditionalObj.coordenadaSaida = new JTextField();
                    conditionalObj.coordenadaSaida.setBorder(new TitledBorder("Coordenada de Saída"));
                    if (defaults.length > 9)
                        conditionalObj.coordenadaSaida.setText((String) defaults[9]);
                    constraints.gridy = 3;
                    conditionalOptions.add(conditionalObj.coordenadaSaida, constraints);
                }
            }
            else if (tipoLocal.getSelectedIndex() == 2) {
                conditionalObj.cruzamento = new JTextField();
                conditionalObj.cruzamento.setBorder(new TitledBorder("Cruzamento"));
                if (defaults.length > 6)
                    conditionalObj.cruzamento.setText((String) defaults[6]);
                constraints.gridy = 0;
                conditionalOptions.add(conditionalObj.cruzamento, constraints);

                conditionalObj.municipio = new JTextField();
                conditionalObj.municipio.setBorder(new TitledBorder("Municipio"));
                if (defaults.length > 7)
                    conditionalObj.municipio.setText((String) defaults[7]);
                constraints.gridy = 1;
                conditionalOptions.add(conditionalObj.municipio, constraints);
            }

            conditionalOptions.revalidate();
            JOptionPane.getFrameForComponent(panel).revalidate();
        };

        onChange.actionPerformed(null);
        tipoLocal.addActionListener(onChange);

        int res = showOptionDialog(null, new JScrollPane(panel), defaults.length == 0 ? "Inserir" : "Editar", DEFAULT_OPTION, QUESTION_MESSAGE, null, new String[]{defaults.length == 0 ? "Inserir" : "Guardar"}, defaults.length == 0 ? "Inserir" : "Guardar");

        if (res >= 0) {
            if (coordGeografica.getText().isBlank())
                showMessageDialog(null, "Por favor forneça uma coordenada geográfica válida.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
            else if (dataInstalacao.getText().isBlank())
                showMessageDialog(null, "Por favor forneça uma data de instalação válida.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
            else {
                update(defaults.length > 0, coordGeografica.getText(), (String) tipoLocal.getSelectedItem(), Date.valueOf(dataInstalacao.getDate()), comarca.getText(), rodovia.getText(), (Integer) quilometro.getValue());

                if (defaults.length > 0)
                    deleteAllSub(coordGeografica.getText());

                if (tipoLocal.getSelectedIndex() == 0 || tipoLocal.getSelectedIndex() == 1)
                    updateVelocidade(tipoLocal.getSelectedIndex(), coordGeografica.getText(), (Integer) conditionalObj.velocidadeLimite.getValue(), (Integer) conditionalObj.erroVelocidade.getValue(), conditionalObj.coordenadaEntrada.getText(), conditionalObj.coordenadaSaida.getText());
                else
                    updateSinalVermelho(coordGeografica.getText(), conditionalObj.cruzamento.getText(), conditionalObj.municipio.getText());
            }
        }
    }

    public void update(boolean editar, String coordenada, String tipo, Date instalacao, String comarca, String rodovia, int quilometro) throws SQLException {
        String sql = !editar ? "INSERT INTO LOCAL_CTRL_TRANSITO(TipoLocal, DataInstalacao, Comarca, Rodovia, Quilometro, CoordGeografica) VALUES (?, ?, ?, ?, ?, ?)" : "UPDATE LOCAL_CTRL_TRANSITO SET TipoLocal = ?, DataInstalacao = ?, Comarca = ?, Rodovia = ?, Quilometro = ? WHERE CoordGeografica = ?";
        PreparedStatement statement = con.prepareStatement(sql);

        statement.setString(1, tipo);
        statement.setDate(2, instalacao);
        statement.setString(3, comarca);
        statement.setString(4, rodovia);
        statement.setInt(5, quilometro);
        statement.setString(6, coordenada);
        statement.executeUpdate();
    }

    public void updateVelocidade(int type, String coordenada, int limite, int erro, String entrada, String saida) throws SQLException {
        PreparedStatement existsSql = con.prepareStatement("SELECT CoordGeografica FROM L_VELOCIDADE WHERE CoordGeografica = ?");
        existsSql.setString(1, coordenada);

        boolean exist = existsSql.executeQuery().next();
        String sql = !exist ? "INSERT INTO L_VELOCIDADE(VelocidadeLimite, ErroVelocidade, CoordGeografica) VALUES (?, ?, ?)" : "UPDATE L_VELOCIDADE SET VelocidadeLimite = ?, ErroVelocidade = ? WHERE CoordGeografica = ?";
        PreparedStatement statement = con.prepareStatement(sql);

        statement.setInt(1, limite);
        statement.setInt(2, erro);
        statement.setString(3, coordenada);
        statement.executeUpdate();

        if (type == 0) {
            existsSql = con.prepareStatement("SELECT CoordGeografica FROM L_VELOCIDADE_MEDIA WHERE CoordGeografica = ?");
            existsSql.setString(1, coordenada);

            exist = existsSql.executeQuery().next();
            sql = !exist ? "INSERT INTO L_VELOCIDADE_MEDIA(CoordenadaEntrada, CoordenadaSaida, CoordGeografica) VALUES (?, ?, ?)" : "UPDATE L_VELOCIDADE_MEDIA SET CoordenadaEntrada = ?, CoordenadaSaida = ? WHERE CoordGeografica = ?";
            statement = con.prepareStatement(sql);

            statement.setString(1, entrada);
            statement.setString(2, saida);
            statement.setString(3, coordenada);
            statement.executeUpdate();
        }
        else if (type == 1 && !exist) {
            statement = con.prepareStatement("INSERT INTO L_VELOCIDADE_INSTANTANEA(CoordGeografica) VALUES (?)");

            statement.setString(1, coordenada);
            statement.executeUpdate();
        }
    }

    public void updateSinalVermelho(String coordenada, String cruzamento, String municipio) throws SQLException {
        PreparedStatement existsSql = con.prepareStatement("SELECT CoordGeografica FROM L_VELOCIDADE WHERE CoordGeografica = ?");
        existsSql.setString(1, coordenada);

        boolean exist = existsSql.executeQuery().next();
        String sql = !exist ? "INSERT INTO L_SINAL_VERMELHO(Cruzamento, Municipio, CoordGeografica) VALUES (?, ?, ?)" : "UPDATE L_SINAL_VERMELHO SET Cruzamento = ?, Municipio = ? WHERE CoordGeografica = ?";
        PreparedStatement statement = con.prepareStatement(sql);

        statement.setString(1, cruzamento);
        statement.setString(2, municipio);
        statement.setString(3, coordenada);
        statement.executeUpdate();
    }

    @Override
    public void delete(Object id) throws SQLException {
        PreparedStatement statement = con.prepareStatement("DELETE FROM LOCAL_CTRL_TRANSITO WHERE CoordGeografica = ?");

        statement.setString(1, (String) id);

        statement.executeUpdate();
    }

    public void deleteAllSub(String id) throws SQLException {
        String[] tables = new String[] {"L_VELOCIDADE", "L_VELOCIDADE_MEDIA", "L_VELOCIDADE_INSTANTANEA", "L_SINAL_VERMELHO"};

        for (String table : tables) {
            PreparedStatement statement = con.prepareStatement("DELETE FROM " + table + " WHERE CoordGeografica = ?");
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }
}