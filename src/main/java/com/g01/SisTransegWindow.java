package com.g01;

import javax.swing.*;

public class SisTransegWindow {
    private JTabbedPane tabbedPane1;
    private JPanel lct;
    private JPanel et;
    private JPanel bo;
    private JPanel notifications;
    private JPanel contraordenacao;
    private JPanel mainPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SisTransegWindow");
        frame.setContentPane(new SisTransegWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
