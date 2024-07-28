package br.com.ifce;

import br.com.ifce.network.activemq.BrokerMediator;
import br.com.ifce.view.MainView;

import javax.swing.*;

import static java.lang.Runtime.getRuntime;

public class Main {
    public static void main(String[] args) {
        var brokerMediator = BrokerMediator.getInstance();
        brokerMediator.start();
        getRuntime().addShutdownHook(new Thread(brokerMediator::close));
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