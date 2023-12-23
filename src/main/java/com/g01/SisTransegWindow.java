package com.g01;

import javax.swing.*;

public class SisTransegWindow {
    private JTabbedPane tabbedPane1;
    private JPanel lct;
    private JPanel bo;
    private JPanel notifications;
    private JPanel contraordenacao;
    private JPanel mainPanel;
    private JButton inserirButton;
    private JButton editarButton;
    private JList list1;
    private JButton eliminarButton;

    public SisTransegWindow() {

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema TranSeg");
        frame.setContentPane(new SisTransegWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        //frame.pack();
        frame.setVisible(true);
    }
}
