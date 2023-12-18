package com.g01;

import javax.swing.*;

public class SisTransegWindow {
    private JTabbedPane tabbedPanel;
    private JPanel lctPanel;
    private JPanel gerenciarPanel;
    private JPanel notificationsPanel;
    private JPanel contraordenacaoPanel;
    private JPanel mainPanel;
    private JButton inserirButton;
    private JButton editarButton;
    private JList list1;
    private JButton eliminarButton;
    private JButton eliminarButton1;
    private JButton inserirButton1;
    private JButton editarButton1;
    private JList list2;
    private JPanel homePanel;
    private JButton eliminarButton2;
    private JButton inserirButton2;
    private JButton editarButton2;
    private JList list3;
    private JButton eliminarButton3;
    private JButton inserirButton3;
    private JButton editarButton3;
    private JList list4;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema TranSeg");
        frame.setContentPane(new SisTransegWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        //frame.pack();
        frame.setVisible(true);
    }
}
