package com.g01;

import com.g01.connection.Connection;
import com.g01.connection.ConnectionInfo;
import com.g01.connection.ConstantServe;
import com.g01.connection.models.EventoTransito;
import com.g01.connection.models.Infracao;
import com.g01.connection.models.LocalCtrlTransito;
import com.g01.connection.models.Notificacao;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

public class SisTransegWindow {
    private final ConnectionInfo info;

    //region Components
    private JTabbedPane tabbedPanel;
    private JPanel lctPanel;
    private JPanel gesPanel;
    private JPanel notiPanel;
    private JPanel infraPanel;
    private JPanel mainPanel;
    private JButton lctInserirButton;
    private JButton lctEditarButton;
    private JButton lctEliminarButton;
    private JButton gesEliminarButton;
    private JButton gesInserirButton;
    private JButton gesEditarButton;
    private JPanel homePanel;
    private JButton notiEliminarButton;
    private JButton notiGerarButton;
    private JButton notiSegundaButton;
    private JTable lctTable;
    private JPanel etPanel;
    private JButton etEliminarButton;
    private JButton etInserirButton;
    private JTable etTable;
    private DatePicker infraStart;
    private DatePicker infraEnd;
    private JButton infraProcurar;
    private JTable infraTable;
    private JComboBox<String> infraFilter;
    private JTable notiTable;
    private JTable gesTable;
    //endregion

    public SisTransegWindow() {
        info = new ConnectionInfo("sis_transeg", "postgres", "is");
        infraStart.setSettings(ConstantServe.getDatePickerSettings());
        infraEnd.setSettings(ConstantServe.getDatePickerSettings());

        tabbedPanel.addChangeListener(e -> {
            String tabTitle = tabbedPanel.getTitleAt(tabbedPanel.getSelectedIndex());

            if (tabTitle.equals(tabbedPanel.getTitleAt(1)))
                tab(LocalCtrlTransito::new, lctTable);
            else if (tabTitle.equals(tabbedPanel.getTitleAt(2)))
                tab(EventoTransito::new, etTable);
            else if (tabTitle.equals(tabbedPanel.getTitleAt(4)))
                tab(Notificacao::new, notiTable);
        });

        lctTable.getSelectionModel().addListSelectionListener(e -> {
            lctEditarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
            lctEliminarButton.setEnabled(!lctTable.getSelectionModel().isSelectionEmpty());
        });
        lctInserirButton.addActionListener(e -> insertAction(LocalCtrlTransito::new, lctTable));
        lctEditarButton.addActionListener(e -> editAction(LocalCtrlTransito::new, lctTable));
        lctEliminarButton.addActionListener(e -> eliminarAction(LocalCtrlTransito::new, lctTable));

        etTable.getSelectionModel().addListSelectionListener(e -> etEliminarButton.setEnabled(!etTable.getSelectionModel().isSelectionEmpty()));
        etInserirButton.addActionListener(e -> insertAction(EventoTransito::new, etTable));
        etEliminarButton.addActionListener(e -> eliminarAction(EventoTransito::new, etTable));

        notiTable.getSelectionModel().addListSelectionListener(e -> {
            boolean secondWay = true;

            if (notiTable.getSelectedRow() != -1) {
                Integer selectedRow = (Integer) notiTable.getValueAt(notiTable.getSelectedRow(), 0);

                for (int i = 0; i < notiTable.getModel().getRowCount(); i++) {
                    if (notiTable.getModel().getValueAt(i, 0).equals(selectedRow) && notiTable.getSelectedRow() != i) {
                        secondWay = false;
                        break;
                    }
                }
            }

            notiSegundaButton.setEnabled(!notiTable.getSelectionModel().isSelectionEmpty() && secondWay);
            notiEliminarButton.setEnabled(!notiTable.getSelectionModel().isSelectionEmpty());
        });
        notiGerarButton.addActionListener(e -> insertAction(Notificacao::new, notiTable));
        notiSegundaButton.addActionListener(e -> secondNotification());
        notiEliminarButton.addActionListener(e -> eliminarNotificacao());

        infraProcurar.addActionListener(e -> searchOffenses());
    }

    //region CRUD
    private <T extends Connection> void tab(Supplier<T> supT, JTable table) {
        try (T obj = supT.get()) {
            obj.connect(info);

            ResultSet lctEntries = obj.getEntries();
            table.setModel(obj.getTableModel(lctEntries));
        }
        catch (SQLException e) {
            errorMsg(e.getMessage());
        }
    }

    private <T extends Connection> void insertAction(Supplier<T> supT, JTable table) {
        try (T obj = supT.get()) {
            obj.connect(info);
            obj.form();

            tab(supT, table);
        }
        catch (SQLException ex) {
            errorMsg(ex.getMessage());
        }
    }

    private <T extends Connection> void editAction(Supplier<T> supT, JTable table) {
        try (T obj = supT.get()) {
            obj.connect(info);

            ResultSet res = obj.getEntry(table.getModel().getValueAt(table.getSelectedRow(), 0));
            if (res.next()) {
                List<Object> val = new ArrayList<>();
                for (int i = 1; i <= res.getMetaData().getColumnCount(); i++)
                    val.add(res.getString(i));

                obj.form(val.toArray());

                tab(supT, table);
            }
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
    //endregion

    //region Notificação
    private void secondNotification() {
        try (Notificacao obj = new Notificacao()) {
            obj.connect(info);
            obj.secondForm((Integer) notiTable.getModel().getValueAt(notiTable.getSelectedRow(), 0));

            tab(Notificacao::new, notiTable);
        }
        catch (SQLException ex) {
            errorMsg(ex.getMessage());
        }
    }

    private void eliminarNotificacao() {
        if (!confirmMsg())
            return;

        try (Notificacao obj = new Notificacao()) {
            obj.connect(info);

            int len = notiTable.getSelectedRows().length - 1;
            for (int i = len; i >= 0; i--)
                obj.deleteNoti(notiTable.getValueAt(notiTable.getSelectedRows()[i], 0).toString(), notiTable.getValueAt(notiTable.getSelectedRows()[i], 1).toString());

            tab(Notificacao::new, notiTable);
        }
        catch (SQLException ex) {
            errorMsg(ex.getMessage());
        }
    }
    //endregion

    private void searchOffenses() {
        try (Infracao inf = new Infracao(infraStart.getDate(), infraEnd.getDate(), infraFilter.getSelectedIndex())) {
            inf.connect(info);

            ResultSet resultSet = inf.getEntries();
            infraTable.setModel(inf.getTableModel(resultSet));
        }
        catch (SQLException e) {
            errorMsg(e.getMessage());
        }
    }

    private boolean confirmMsg() {
        return showConfirmDialog(null, "Tem a certeza que pretende eliminar os elementos selecionados?", "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void errorMsg(String errorMsg) {
        showMessageDialog(null, "Houve um erro ao comunicar com a base de dados:\n\n" + errorMsg, "Falha na conexão", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema TranSeg");
        frame.setContentPane(new SisTransegWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
