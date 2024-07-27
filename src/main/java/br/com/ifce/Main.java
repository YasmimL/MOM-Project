package br.com.ifce;

import br.com.ifce.network.activemq.BrokerMediator;
import br.com.ifce.view.MainView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        BrokerMediator.getInstance().start();
        SwingUtilities.invokeLater(() -> {
            try {
                var view = new MainView();
                view.setUpFrame();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}