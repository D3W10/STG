package com.g01.connection.models;

import com.g01.connection.Connection;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Statement;

import static javax.swing.JOptionPane.showMessageDialog;

public class LocalCtrlTransito extends Connection {
    public LocalCtrlTransito() {
        super("LOCAL_CTRL_TRANSITO");
    }

    @Override
    public void insertForm() throws SQLException {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, 0));
        JTextField coordGeografica = new JTextField();
        coordGeografica.setBorder(new TitledBorder("Coordenada Geográfica"));
        panel.add(coordGeografica);
        JTextField tipoLocal = new JTextField();
        tipoLocal.setBorder(new TitledBorder("Tipo de Local"));
        panel.add(tipoLocal);
        DatePicker dataInstalacao = new DatePicker(DP_SETTINGS);
        dataInstalacao.setBorder(new TitledBorder("Data de Instalação"));
        panel.add(dataInstalacao);
        JTextField comarca = new JTextField();
        comarca.setBorder(new TitledBorder("Comarca"));
        panel.add(comarca);
        JTextField rodovia = new JTextField();
        rodovia.setBorder(new TitledBorder("Rodovia"));
        panel.add(rodovia);
        JSpinner quilometro = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
        quilometro.setBorder(new TitledBorder("Quilómetro"));
        panel.add(quilometro);

        showMessageDialog(null, panel, "Inserir", JOptionPane.PLAIN_MESSAGE);

        if (coordGeografica.getText().isBlank())
            showMessageDialog(null, "Por favor forneça uma coordenada geográfica válida.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
        else if (dataInstalacao.getText().isBlank())
            showMessageDialog(null, "Por favor forneça uma data de instalação válida.", "Erro a inserir", JOptionPane.ERROR_MESSAGE);
        else
            insert(coordGeografica.getText(), tipoLocal.getText(), dataInstalacao.getText(), comarca.getText(), rodovia.getText(), String.valueOf(quilometro.getValue()));
    }

    @Override
    public void delete(String id) throws SQLException {
        Statement statement = con.createStatement();

        statement.executeUpdate("DELETE FROM " + entity + " WHERE CoordGeografica = '" + id + "'");
    }
}