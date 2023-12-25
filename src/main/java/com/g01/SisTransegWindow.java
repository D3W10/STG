package com.g01;

import com.g01.connection.Connection;
import com.g01.connection.ConnectionInfo;
import com.g01.connection.models.EventoTransito;
import com.g01.connection.models.LocalCtrlTransito;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class SisTransegWindow {
    private final ConnectionInfo info;

    private JTabbedPane tabbedPanel;
    private JPanel lctPanel;
    private JPanel gerenPanel;
    private JPanel notiPanel;
    private JPanel contraPanel;
    private JPanel mainPanel;
    private JButton lctInserirButton;
    private JButton lctEditarButton;
    private JButton lctEliminarButton;
    private JButton gerenEliminarButton;
    private JButton gerenInserirButton;
    private JButton gerenEditarButton;
    private JList<String> gerenList;
    private JPanel homePanel;
    private JButton notiEliminarButton;
    private JButton notiInserirButton;
    private JButton notiEditarButton;
    private JList<String> notiList;
    private JButton contraEliminarButton;
    private JButton contraInserirButton;
    private JButton contraEditarButton;
    private JList<String> contraList;
    private JTable lctTable;
    private JPanel etPanel;
    private JButton etEliminarButton;
    private JButton etInserirButton;
    private JTable etTable;

    public SisTransegWindow() {
        info = new ConnectionInfo("sis_transeg", "postgres", "is");

        tabbedPanel.addChangeListener(e -> {
            String tabTitle = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());

            if (tabTitle.equals(tabbedPanel.getTitleAt(1)))
                tab(LocalCtrlTransito::new, lctTable);
            else if (tabTitle.equals(tabbedPanel.getTitleAt(2)))
                tab(EventoTransito::new, etTable);
        });

        lctTable.getSelectionModel().addListSelectionListener(e -> {
            lctEditarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
            lctEliminarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
        });
        lctInserirButton.addActionListener(e -> insertAction(LocalCtrlTransito::new, lctTable));
        lctEliminarButton.addActionListener(e -> eliminarAction(LocalCtrlTransito::new, lctTable));

        etTable.getSelectionModel().addListSelectionListener(e -> etEliminarButton.setEnabled(!etTable.getSelectionModel().isSelectionEmpty()));
        etInserirButton.addActionListener(e -> insertAction(EventoTransito::new, etTable));
        etEliminarButton.addActionListener(e -> eliminarAction(EventoTransito::new, etTable));
    }

    private <T extends Connection> void tab(Supplier<T> supT, JTable table) {
        try (T obj = supT.get()) {
            obj.connect(info);

            ResultSet lctEntries = obj.getEntries(null);
            table.setModel(obj.getTableModel(lctEntries));
        }
        catch (SQLException e) {
            errorMsg(e.getMessage());
        }
    }

    private <T extends Connection> void insertAction(Supplier<T> supT, JTable table) {
        try (T obj = supT.get()) {
            obj.connect(info);
            obj.insertForm();

            tab(supT, table);
        }
        catch (SQLException ex) {
            errorMsg(ex.getMessage());
        }
    }

    private <T extends Connection> void eliminarAction(Supplier<T> supT, JTable table) {
        if (!confirmMsg())
            return;

        try (T obj = supT.get()) {
            obj.connect(info);

            int len = table.getSelectedRows().length - 1;
            for (int i = len; i >= 0; i--)
                obj.delete(table.getValueAt(table.getSelectedRows()[i], 0).toString());

            tab(supT, table);
        }
        catch (SQLException ex) {
            errorMsg(ex.getMessage());
        }
    }

    private boolean confirmMsg() {
        return showConfirmDialog(null, "Tem a certeza que pretende eliminar os elementos selecionados?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void errorMsg(String errorMsg) {
        showMessageDialog(null, "Não foi possível conectar à base de dados:" + "\n\n" + errorMsg, "Falha na conexão", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema TranSeg");
        frame.setContentPane(new SisTransegWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);
    }
}
