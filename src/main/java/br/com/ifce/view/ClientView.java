package br.com.ifce.view;

import br.com.ifce.network.activemq.BrokerMediator;
import br.com.ifce.network.activemq.Listener;
import br.com.ifce.network.activemq.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientView implements Listener {

    private final JFrame frame;

    private final String windowName;

    private final JTextArea logsTextArea = new JTextArea();

    private final List<String> messages = new ArrayList<>();

    public ClientView(String windowName, List<String> topics) {
        this.windowName = windowName;
        this.frame = new JFrame(windowName);
        topics.forEach(topic -> this.onMessage(new Message(null, "Listening to " + topic + "...")));
    }

    public void setUpFrame() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        int frameWidth = 620;
        int frameHeight = 790;
        frame.setSize(frameWidth, frameHeight);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                BrokerMediator.getInstance().closeConsumer(windowName);
                frame.dispose();
            }
        });

        this.renderHeading();
        this.renderLogsTextArea();

        frame.setVisible(true);
    }

    private void renderHeading() {
        JPanel panel = new JPanel();
        JLabel heading = new JLabel("Event Log");
        heading.setFont(new Font("Serif", Font.PLAIN, 18));
        panel.add(heading);

        frame.add(panel);
    }

    private void renderLogsTextArea() {
        this.logsTextArea.setBounds(10, 30, 200, 200);
        this.logsTextArea.setEditable(false);
        this.frame.add(this.logsTextArea);
    }

    @Override
    public void onMessage(Message message) {
        this.messages.add(message.toString());
        this.logsTextArea.setText(String.join("\n", this.messages));
    }
}
