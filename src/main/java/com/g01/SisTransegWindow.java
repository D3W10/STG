package com.g01;

import com.g01.connection.ConnectionInfo;
import com.g01.connection.models.LocalCtrlTransito;

import javax.swing.*;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public SisTransegWindow() {
        info = new ConnectionInfo("sis_transeg", "postgres", "is");

        tabbedPanel.addChangeListener(e -> {
            String tabTitle = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());

            if (tabTitle.equals(tabbedPanel.getTitleAt(1)))
                lctTab();
        });

        lctTable.getSelectionModel().addListSelectionListener(e -> {
            lctEditarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
            lctEliminarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
        });

        lctEliminarButton.addActionListener(e -> {
            for (int index : lctTable.getSelectedRows()) {
                try (LocalCtrlTransito lct = new LocalCtrlTransito()) {
                    lct.connect(info);

                    lct.delete(lctTable.getValueAt(index, 0).toString());
                    lctTab();
                }
                catch (SQLException ex) {
                    errorMsg(ex.getMessage());
                }
            }
        });
    }

    private void lctTab() {
        try (LocalCtrlTransito lct = new LocalCtrlTransito()) {
            lct.connect(info);

            ResultSet lctEntries = lct.getEntries(null);
            lctTable.setModel(lct.getTableModel(lctEntries));
        }
        catch (SQLException e) {
            errorMsg(e.getMessage());
        }
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
