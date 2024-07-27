package br.com.ifce.view;

import br.com.ifce.extension.SensorTableModel;
import br.com.ifce.model.Parameter;
import br.com.ifce.network.activemq.BrokerMediator;
import br.com.ifce.network.activemq.Consumer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class MainView {

    private final JFrame frame = new JFrame("Sensor Management");

    private final SensorTableModel sensorTableModel = new SensorTableModel();

    private final List<ClientView> clientViews = new ArrayList<>();

    public void setUpFrame() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int frameWidth = 1270;
        int frameHeight = 600;
        frame.setSize(frameWidth, frameHeight);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        this.renderHeading();
        this.renderActions();
        this.renderSensorTable();

        frame.setVisible(true);
    }

    private void renderHeading() {
        JPanel panel = new JPanel();
        JLabel heading = new JLabel("Management");
        heading.setFont(new Font("Serif", Font.PLAIN, 24));
        panel.add(heading);

        frame.add(panel);
    }

    private void renderActions() {
        JPanel panel = new JPanel();

        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setLayout(grid);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(this.renderAddSensorButton(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(this.renderAddClientButton(), gbc);

        frame.add(panel);
    }

    private Button renderAddSensorButton() {
        var button = new Button("Add Sensor");
        button.addActionListener(e -> showCreateSensorDialog());

        return button;
    }

    private void showCreateSensorDialog() {
        var parameterField = new JComboBox<>(Parameter.textValues());
        var minLimitField = new JTextField();
        var maxLimitField = new JTextField();
        var currentValueField = new JTextField();
        var fields = new Object[]{
            "Parameter", parameterField,
            "Min Limit", minLimitField,
            "Max Limit", maxLimitField,
            "Current Value", currentValueField
        };

        var option = JOptionPane.showConfirmDialog(frame, fields, "Create Sensor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) return;

        var providedParameter = Objects.requireNonNull(parameterField.getSelectedItem()).toString();
        var providedMinLimit = minLimitField.getText();
        var providedMaxLimit = maxLimitField.getText();
        var providedCurrentValue = currentValueField.getText();
        handleCreateSensor(
            providedParameter,
            providedMinLimit,
            providedMaxLimit,
            providedCurrentValue
        );
    }

    private void handleCreateSensor(String providedParameter, String providedMinLimit, String providedMaxLimit, String providedCurrentValue) {
        try {
            var parameter = Parameter.fromString(providedParameter);
            var minLimit = Double.valueOf(providedMinLimit);
            var maxLimit = Double.valueOf(providedMaxLimit);
            var currentValue = Double.valueOf(providedCurrentValue);

            if (Stream.of(parameter, minLimit, maxLimit, currentValue).anyMatch(Objects::isNull)) return;

            BrokerMediator.getInstance().addSensor(parameter, minLimit, maxLimit, currentValue);
            sensorTableModel.fireTableDataChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button renderAddClientButton() {
        var button = new Button("Add Client");
        button.addActionListener(e -> showCreateClientDialog());

        return button;
    }

    private void showCreateClientDialog() {
        var topicsList = new JList<>(BrokerMediator.getInstance().getTopics().toArray());
        topicsList.setVisibleRowCount(3);
        topicsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        var topicsField = new JScrollPane(topicsList);
        var fields = new Object[]{
            "Select Topics", topicsField,
        };

        var option = JOptionPane.showConfirmDialog(frame, fields, "Create Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) return;

        var selectedTopics = topicsList.getSelectedValuesList();
        handleCreateClient(selectedTopics);
    }

    private void handleCreateClient(List<Object> selectedTopics) {
        try {
            if (selectedTopics.isEmpty()) return;
            var topics = selectedTopics.stream().map(String::valueOf).toList();

            var clientView = new ClientView("Client " + (clientViews.size() + 1));
            clientView.setUpFrame();
            clientViews.add(clientView);

            var consumer = Consumer.newInstance(topics, clientView);
            BrokerMediator.getInstance().addConsumer(consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderSensorTable() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Topic");
        tableModel.addColumn("Parameter");
        tableModel.addColumn("Current Value");
        tableModel.addColumn("Min Limit");
        tableModel.addColumn("Max Limit");

        JTable table = new JTable(sensorTableModel);
        table.setRowSelectionAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 40, 200, 300);

        frame.add(scrollPane);
    }
}
